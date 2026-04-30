package com.lazarbela.ikthesis.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LocalFileStorageService {
    private final FileStorageProperties properties;
    private final Path rootPath;

    public LocalFileStorageService(FileStorageProperties properties) {
        this.properties = properties;
        this.rootPath = Paths.get(properties.basePath());
    }

    public String storeFile(InputStream inputStream, String originalName, String sessionId) throws IOException {
        Path storageDirectory = rootPath.resolve(sessionId);
        Files.createDirectories(storageDirectory);

        String ext = getFileExtension(originalName);
        String storedName = UUID.randomUUID().toString() + "." + ext;
        Path filePath = storageDirectory.resolve(storedName);

        try (OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW)) {
            StreamUtils.copy(inputStream, outputStream);
        }

        return rootPath.relativize(filePath).toString();
    }

    public Resource getFileResource(String storedPath) throws IOException {
        Path filePath = rootPath.resolve(storedPath).normalize().toAbsolutePath();
        Path normalizedRoot = rootPath.normalize().toAbsolutePath();

        if (!filePath.startsWith(normalizedRoot)) {
            throw new SecurityException("Access denied");
        }

        if (!Files.exists(filePath)) {
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

    public void deleteFile(String storedPath) throws IOException {
        Path filePath = rootPath.resolve(storedPath).normalize().toAbsolutePath();
        Path normalizedRoot = rootPath.normalize().toAbsolutePath();

        if (!filePath.startsWith(normalizedRoot)) {
            throw new SecurityException("Access denied");
        }

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found");
        }

        Files.delete(filePath);
    }

    public void deleteDirectory(String directoryName) throws IOException {
        Path normalizedFolderPath = rootPath.resolve(directoryName).normalize();
        if (!Files.exists(normalizedFolderPath))
            return;
        Files.walkFileTree(normalizedFolderPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                java.nio.file.Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });
        Files.delete(normalizedFolderPath);
    }

    public String cleanStorageFolder(Set<String> validSubfolders) {
        File[] files = rootPath.toFile().listFiles();

        if (files == null)
            return "No directories discovered!";

        Set<File> directories = Stream.of(files).filter(id -> id.isDirectory() && !validSubfolders.contains(id.getName())).collect(Collectors.toSet());

        if (directories.isEmpty())
            return "No directories to delete";

        System.out.println("directories to purge: " + directories);
        for (File directory : directories) {
            try {
                deleteDirectory(directory.getName());
            } catch (IOException e) {
                System.err.println("An error occurred while deleting directory " + directory.getName());
            }
        }
        return "Deleted directories: " + directories;
    }
}