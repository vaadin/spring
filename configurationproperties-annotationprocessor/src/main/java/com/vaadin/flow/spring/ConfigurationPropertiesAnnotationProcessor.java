package com.vaadin.flow.spring;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.ElementKind;
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
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<String, CompilationUnit> compilationUnitCache = new HashMap<>();

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

        public FieldDescriptor(String formerPropertyName, String type,
                               String defaultValueSetter, String comments) {
            assert formerPropertyName != null;
            assert type != null;
            this.formerPropertyName = formerPropertyName;
            this.defaultValueSetter = defaultValueSetter;
            this.fieldName = toCamelCase(formerPropertyName, false);
            // we do not want primitives, as we want any property to be nullable
            // to know if the value has actually been set by the user
            if ("int".equals(type)) type = "Integer";
            else if ("double".equals(type)) type = "Double";
            else if ("boolean".equals(type)) type = "Boolean";
            this.type = type;
            this.comments = comments;
        }


    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE,
                "Starting annotation processing from " + getClass().getName());
        annotations.forEach(annotation -> processAnnotation(roundEnv, annotation));
        return false;
    }

    private void processAnnotation(RoundEnvironment roundEnv, TypeElement annotation) {
        roundEnv.getElementsAnnotatedWith(annotation).forEach(e -> {
            List<FieldDescriptor> fields = new ArrayList<>();

            // SpringConfigurationPropertiesGenerator is restricted to Types, so it's safe to cast the element
            addFieldsFromTypeElement((TypeElement) e, fields);

            addFieldsFromInitParametersClass(fields);

            // same package, VaadinConfigurationProperties as class name
            String annotatedClassName = e.toString();
            String generatedClassName = e.getEnclosingElement().toString()
                    + "." + "VaadinConfigurationProperties";

            String prefix = "vaadin";

            try {
                writeJavaCodeToFile(annotatedClassName, generatedClassName,
                        prefix, fields);
            } catch (IOException ioException) {
                messager.printMessage(Diagnostic.Kind.ERROR, ioException.getClass().getSimpleName() + ": " + ioException.getMessage());
            }
        });
    }

    private void addFieldsFromInitParametersClass(List<FieldDescriptor> fields) {
        String pathToInitParameters = "/" + INITPARAMETERS_CLASS_NAME
                .replaceAll("\\.", "/") + ".java";
        InputStream r = getClass().getResourceAsStream(pathToInitParameters);
        ParseResult<CompilationUnit> parsed = new JavaParser().parse(r);
        if (parsed.getResult().isPresent()) {
            addFieldsFromCompilationUnit(parsed.getResult().get(), fields);
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    pathToInitParameters
                            + " is missing. Please check if the flow-server " +
                            "sources are available as a dependency");
        }
    }

    private void addFieldsFromCompilationUnit(CompilationUnit cu,
                                              List<FieldDescriptor> fields) {
        cu.accept(new VoidVisitorAdapter<List<FieldDescriptor>>() {
            @Override
            public void visit(FieldDeclaration fieldDeclaration,
                              List<FieldDescriptor> arg) {
                super.visit(fieldDeclaration, arg);

                if (hasInitialValue(fieldDeclaration)) {
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
            }
        }, fields);
    }


    private void addFieldsFromTypeElement(TypeElement initParams,
                                          List<FieldDescriptor> fields) {
        processingEnv.getElementUtils().getAllMembers(initParams).stream()
                .filter(m -> ElementKind.FIELD.equals(m.getKind()))
                .filter(m -> m instanceof VariableElement) // in theory all fields are VariableElements
                .map(m -> (VariableElement) m)
                .map(m -> new FieldDescriptor(m.getSimpleName().toString(),
                        m.asType().toString(),
                        getInitialValueAssignmentCode(m),
                        processingEnv.getElementUtils().getDocComment(m)))
                .forEach(fields::add);
    }

    private String getInitialValueAssignmentCode(VariableElement m) {
        String className = m.getEnclosingElement().toString();

        CompilationUnit cu = getCompilationUnit(className);

        String value = getInitialValueAssignmentCode(cu, m.getSimpleName().toString());

        return value;
    }

    private CompilationUnit getCompilationUnit(String className) {
        return compilationUnitCache.computeIfAbsent(className, key -> {
            String pkgName = key.substring(0, key.lastIndexOf('.'));
            String simpleClassName = key.substring(key.lastIndexOf('.') + 1);
            CompilationUnit cu = null;
            try {
                FileObject source = processingEnv.getFiler()
                        .getResource(StandardLocation.SOURCE_PATH, pkgName,
                                simpleClassName + ".java");
                ParseResult<CompilationUnit> parsed = new JavaParser()
                        .parse(source.openInputStream());
                if (parsed.getResult().isPresent()) {
                    cu = parsed.getResult().get();
                }
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Unable to parse InitParameters.java ");
            }
            return cu;
        });
    }

    private String getInitialValueAssignmentCode(CompilationUnit cu, String fieldName) {
        assert fieldName != null;

            String[] value = new String[1];
            if (cu != null) {
                cu.accept(new VoidVisitorAdapter<String[]>() {
                    @Override
                    public void visit(FieldDeclaration fieldDeclaration,
                                      String[] arg) {
                        super.visit(fieldDeclaration, arg);

                        if (matchesAndHasInitialValue(fieldDeclaration, fieldName)) {
                            String sourceLine = fieldDeclaration.
                                    getVariable(0).toString();
                            if (sourceLine.contains("=")) {
                                String assignment = sourceLine.substring(
                                        sourceLine.indexOf("=") + 1);
                                arg[0] = assignment;
                            }
                        }
                    }
                }, value);
            }
            return value[0];
    }

    private boolean matchesAndHasInitialValue(FieldDeclaration fieldDeclaration,
                                              String fieldName) {
        return hasInitialValue(fieldDeclaration) && fieldDeclaration.getVariable(0).
                getNameAsString().equals(fieldName);
    }

    private boolean hasInitialValue(FieldDeclaration fieldDeclaration) {
        return fieldDeclaration.getVariables().size() > 0
                && fieldDeclaration.getVariable(0) != null
                && fieldDeclaration.getVariable(0).
                    getInitializer().isPresent();
    }


    private void writeJavaCodeToFile(String annotatedClassName,
                                     String generatedClassName,
                                     String prefix,
                                     List<FieldDescriptor> fields)
            throws IOException {

        JavaFileObject builderFile = processingEnv.getFiler()
                .createSourceFile(generatedClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            String packageName = generatedClassName
                    .substring(0, generatedClassName.lastIndexOf("."));
            String simpleClassName = generatedClassName
                    .substring(packageName.length() + 1);

            out.println("package " + packageName + ";");
            out.println();

            out.println(getImportsAsString(annotatedClassName));

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

    private String getImportsAsString(String className) {
        StringBuilder value = new StringBuilder();
        CompilationUnit cu = getCompilationUnit(className);
        if (cu != null) {
            cu.accept(new VoidVisitorAdapter<StringBuilder>() {

                @Override
                public void visit(ImportDeclaration fieldDeclaration,
                                  StringBuilder arg) {
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

}