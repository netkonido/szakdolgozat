package com.lazarbela.ikthesis.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@ConfigurationProperties(prefix = "app.file-storage")
@Component
public record FileStorageProperties(
        String basePath,
        Set<String> allowedFileTypes
) {
    public FileStorageProperties()
    {
        this(
                "./files",
                Set.of("application/pdf",
                        "application/docx",
                        "image/png",
                        "image/jpeg")
        );
    }
}
