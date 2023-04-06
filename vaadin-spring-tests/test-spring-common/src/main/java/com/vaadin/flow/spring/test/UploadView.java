package com.vaadin.flow.spring.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Route;

@Route("multipart-upload")
public class UploadView extends Div {

    public UploadView() {
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setId("upl");

        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                    event.getFileName(),
                    buffer.getInputStream(event.getFileName()));
            add(component);
        });

        add(upload);
    }

    private Component createComponent(String mimeType, String fileName,
            InputStream stream) {
        if (!mimeType.startsWith("text")) {
            throw new IllegalStateException();
        }
        String text = "";
        try {
            text = IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            text = "exception reading stream";
        }
        Div div = new Div();
        div.setText(text);
        div.addClassName("uploaded-text");
        return div;
    }
}
