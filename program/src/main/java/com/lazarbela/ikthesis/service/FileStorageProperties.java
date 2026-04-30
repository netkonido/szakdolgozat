package com.lazarbela.ikthesis.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "app.file-storage")
@Component
public record FileStorageProperties(
        String basePath,
        Set<String> allowedFileTypes,
        Map<String, String> mimeTypeExtensionMap
) {
    public FileStorageProperties()
    {
        this(
                "./files",
                Set.of("application/pdf",
                        "vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "text/html",
                        "text/markdown",
                        "image/png",
                        "image/jpeg"),
                Map.of(
                    "application/pdf","pdf",
                    "pdf","application/pdf",
                    "text/html","html",
                    "html","text/html",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document","docx",
                    "docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "text/markdown", "md",
                    "md","text/markdown"

            )
        );
    }
}
