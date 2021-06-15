package com.vaadin.flow.spring;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.Utils;

@SupportedAnnotationTypes("com.vaadin.flow.spring.SpringConfigurationPropertiesGenerator")
public class ConfigurationPropertiesAnnotationProcessor extends AbstractProcessor
{

    private static final String INITPARAMETERS_CLASS_NAME = "com.vaadin.flow.server.InitParameters";

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.WARNING, "hola!");
    }

    class FieldDescriptor {
        final String fieldName;

        final String formerPropertyName;

        final String type;

        final String defaultValueSetter;

        String comments;

        public FieldDescriptor(String formerPropertyName, String type, String defaultValueSetter, String comments) {
            assert formerPropertyName != null;
            assert type != null;
            this.formerPropertyName = formerPropertyName;
            this.defaultValueSetter = defaultValueSetter;
            this.fieldName = toCamelCase(formerPropertyName, false);
            // we do not want primitives, as we want any property to be nullable
            // to know if the value has actually been set by the user
            if ("int".equals(type)) type = "Integer";
            if ("double".equals(type)) type = "Double";
            if ("boolean".equals(type)) type = "Boolean";
            this.type = type;
            this.comments = comments;
        }


    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // use the protected member, processingEnv


        messager.printMessage(Diagnostic.Kind.NOTE, "Starting annotation processing from " + getClass().getName());
        annotations.forEach(annotation -> processAnnotation(roundEnv, annotation));
        return false;
    }

    private void processAnnotation(RoundEnvironment roundEnv, TypeElement annotation) {
        roundEnv.getElementsAnnotatedWith(annotation).forEach(e -> {
            messager.printMessage(Diagnostic.Kind.NOTE,
                    "annotated element is " + e.getSimpleName());
            Elements elementUtils = processingEnv.getElementUtils();
            String comment = elementUtils.getDocComment(e);
            messager.printMessage(Diagnostic.Kind.NOTE,
                    "comment is " + comment);

            List<FieldDescriptor> fields = new ArrayList<>();

            // SpringConfigurationPropertiesGenerator is restricted to Types, so it's safe to cast the element
            addFieldsFromTypeElement((TypeElement) e, fields);

            String pathToInitParameters = "/" + INITPARAMETERS_CLASS_NAME
                    .replaceAll("\\.", "/") + ".java";
            InputStream r = getClass().getResourceAsStream(pathToInitParameters);
            ParseResult<CompilationUnit> parsed = new JavaParser().parse(r);
            if (parsed.getResult().isPresent()) {
                addFieldsFromCompilationUnit(parsed.getResult().get(), fields);
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        pathToInitParameters
                                + " is missing. Please check if the flow-server sources are available as a dependency");
            }

            // same package, VaadinConfigurationProperties as class name
            String annotatedClassName = e.toString();
            String generatedClassName = e.getEnclosingElement().toString()
                    + "." + "VaadinConfigurationProperties";

            SpringConfigurationPropertiesGenerator an =
                    ((TypeElement) e).getAnnotation(
                            SpringConfigurationPropertiesGenerator.class);
            String prefix = an.prefix();

            try {
                writeFile(annotatedClassName, generatedClassName,
                        prefix, fields);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    private void addFieldsFromCompilationUnit(CompilationUnit cu,
                                              List<FieldDescriptor> fields) {
        cu.accept(new VoidVisitorAdapter<List<FieldDescriptor>>() {
            @Override
            public void visit(FieldDeclaration fieldDeclaration,
                              List<FieldDescriptor> arg) {
                super.visit(fieldDeclaration, arg);

                FieldDescriptor fd = new FieldDescriptor(
                        fieldDeclaration.getVariable(0).
                                getInitializer().get().
                                toStringLiteralExpr().get().
                                getValue(),
                        fieldDeclaration.getElementType().toString(),
                        null, null);
                fieldDeclaration.getComment().ifPresent(c ->
                        fd.comments = cleanComment(c.getContent()));
                arg.add(fd);
            }
        }, fields);
    }

    private String toCamelCase(String value, boolean firstUppercase) {
        if (value == null) return null;
        StringBuffer camelized = new StringBuffer();
        Arrays.stream(value
                .replaceAll("([A-Z])", "\\-$1")
                .split("[\\.\\-_)]"))
                .forEach(t -> camelized.append(capitalize(t)));
        return firstUppercase ? Utils.capitalize(camelized.toString()) :
                Utils.decapitalize(camelized.toString());
    }

    private String capitalize(String t) {
        assert t != null;
        return t.isEmpty() ? "" : Utils.capitalize(t);
    }

    private String cleanComment(String comment) {
        /*
        comments from t compilation unit come with initial asterisks and spaces
        so we want to remove them and have the clean comment text
        */
        StringBuffer clean = new StringBuffer();
        if (comment != null) {
            Arrays.stream(comment.split("\n")).
                    filter(l -> !"".equals(comment.trim()))
                    .forEach(l ->
                            clean.append(
                                    ("".equals(clean.toString()) ? "" : "\n")
                                    + l.replaceAll("^[\\* ]+",
                                    "")));
        }
        return clean.toString();
    }

    private void addFieldsFromTypeElement(TypeElement initParams,
                                          List<FieldDescriptor> fields) {
        processingEnv.getElementUtils().getAllMembers(initParams).stream()
                .filter(m -> ElementKind.FIELD.equals(m.getKind()))
                .filter(m -> m instanceof VariableElement) // in theory all fields are VariableElements
                .map(m -> (VariableElement) m)
                .map(m -> new FieldDescriptor(m.getSimpleName().toString(),
                        m.asType().toString(),
                        getinitialValue(m),
                        processingEnv.getElementUtils().getDocComment(m)))
                .forEach(fields::add);
    }

    private String getinitialValue(VariableElement m) {
        String pkgName = processingEnv.getElementUtils().getPackageOf(m.getEnclosingElement()).toString();
        String className = m.getEnclosingElement().getSimpleName().toString();
        try {
            FileObject source = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, pkgName, className + ".java");
            String value = getInitialValue(source.openInputStream(), m.getSimpleName());
            return value;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getInitialValue(InputStream openInputStream, Name fieldName) {
        ParseResult<CompilationUnit> parsed = new JavaParser().parse(openInputStream);
        if (parsed.getResult().isPresent()) {
            CompilationUnit cu = parsed.getResult().get();

            String[] value = new String[1];
            cu.accept(new VoidVisitorAdapter<String[]>() {
                @Override
                public void visit(FieldDeclaration fieldDeclaration, String[] arg) {
                    super.visit(fieldDeclaration, arg);

                    if (fieldDeclaration.getVariables().size() > 0
                            && fieldDeclaration.getVariable(0) != null
                            && fieldDeclaration.getVariable(0).getNameAsString().equals(fieldName.toString())
                            && fieldDeclaration.getVariable(0).getInitializer().isPresent()) {
                        String sourceLine = fieldDeclaration.getVariable(0).toString();
                        if (sourceLine.contains("=")) {
                            String assignment = sourceLine.substring(sourceLine.indexOf("=") + 1);
                            arg[0] = assignment;
                        }
                    }
                }
            }, value);
            return value[0];
        } else {
            return null;
        }
    }


    private void writeFile(String annotatedClassName, String generatedClassName,
                           String prefix,
                           List<FieldDescriptor> fields) throws IOException {
        JavaFileObject builderFile = processingEnv.getFiler()
                .createSourceFile(generatedClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            String packageName = generatedClassName
                    .substring(0, generatedClassName.lastIndexOf("."));
            String simpleClassName = generatedClassName
                    .substring(packageName.length() + 1);

            out.println("package " + packageName + ";");
            out.println();

            out.println(getImportsString(annotatedClassName));

            out.println("import javax.annotation.PostConstruct;");
            out.println("import org.springframework.boot.context.properties." +
                    "ConfigurationProperties;");
            out.println();
            out.println();

            out.println("/**");
            out.println("* generated by " + getClass().getName());
            out.println("* from " + annotatedClassName);
            out.println("*/");
            out.println("@ConfigurationProperties(prefix = \"" +
                    prefix + "\")");
            out.println("public class " + simpleClassName + " {");

            fields.forEach(f -> writeField(out, f));

            writePostConstruct(out, fields);

            out.println("}");
        }
    }

    private String getImportsString(String className) {
        String pkgName = className.substring(0, className.lastIndexOf('.'));
        className = className.substring(className.lastIndexOf('.') + 1);
        try {
            FileObject source = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, pkgName, className + ".java");
            String value = getImportsString(source.openInputStream());
            return value;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getImportsString(InputStream openInputStream) {
        StringBuffer value = new StringBuffer();
        ParseResult<CompilationUnit> parsed = new JavaParser().parse(openInputStream);
        if (parsed.getResult().isPresent()) {
            CompilationUnit cu = parsed.getResult().get();

            cu.accept(new VoidVisitorAdapter<StringBuffer>() {

                @Override
                public void visit(ImportDeclaration fieldDeclaration, StringBuffer arg) {
                    super.visit(fieldDeclaration, arg);
                    value.append(fieldDeclaration.toString());
                }
            }, value);
        }
        return value.toString();
    }

    private void writePostConstruct(PrintWriter out,
                                    List<FieldDescriptor> fields) {
        out.println(" @PostConstruct");
        out.println(" public void transferValuesToSystem() {");
        fields.forEach(f -> {
            out.println("  if (" + f.fieldName + " != null) " +
                    "System.setProperty(\"vaadin." + f.formerPropertyName
                    + "\", " + f.fieldName + ".toString());");
        });
        out.println(" }");
    }

    private void writeField(PrintWriter out, FieldDescriptor field) {
        out.println();

        // write comment
        String comment = field.comments;
        if (comment != null && !"".equals(comment)) {
            out.println("  /**");
            Arrays.stream(comment.split("\n")).
                    forEach(l -> out.println("  * " + l));
            out.println("  */");
        }
        // write field
        out.print("  private " + field.type + " " + field.fieldName);
        if (field.defaultValueSetter != null)
            out.print(" = " + field.defaultValueSetter);
        out.println(";");

        out.println();
        // write getter
        out.println("  public " + field.type + " get" +
                capitalize(field.fieldName) + "() {");
        out.println("    return " + field.fieldName + ";");
        out.println("  };");
        out.println();
        // write setter
        out.println("  public void set" + capitalize(field.fieldName)
                + "(" + field.type + " " + field.fieldName + ") {");
        out.println("    this." + field.fieldName + " = " +
                field.fieldName + ";");
        out.println("  };");

    }

}