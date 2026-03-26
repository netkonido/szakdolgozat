package com.lazarbela.ikthesis.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
public class LocalFileStorageService {
    private final FileStorageProperties properties;
    private final Path rootPath;

    public LocalFileStorageService(FileStorageProperties properties)
    {
        this.properties = properties;
        this.rootPath = Paths.get(properties.basePath());
    }

    public String storeFile (InputStream inputStream, String originalName, String sessionId) throws IOException
    {
        Path storageDirectory = rootPath.resolve(sessionId);
        Files.createDirectories(storageDirectory);

        String ext = getFileExtension(originalName);
        String storedName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
        Path filePath = storageDirectory.resolve(storedName);

        try(OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW)) {
            StreamUtils.copy(inputStream, outputStream);
        }

        return rootPath.relativize(filePath).toString();
    }

    public Resource getFileResource(String storedPath) throws IOException {
        Path filePath = rootPath.resolve(storedPath).normalize().toAbsolutePath();
        Path normalizedRoot = rootPath.normalize().toAbsolutePath();

        if(!filePath.startsWith(normalizedRoot)) {
            throw new SecurityException("Access denied");
        }

        if(!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found");
        }

        return new UrlResource(filePath.toUri());
    }

    String getFileExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex >= 0) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }
}
