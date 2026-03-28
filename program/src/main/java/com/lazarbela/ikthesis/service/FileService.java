package com.lazarbela.ikthesis.service;

import com.lazarbela.ikthesis.model.FileMetadata;
import com.lazarbela.ikthesis.repository.FileMetadataRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {
    private final FileStorageProperties properties;
    private final FileMetadataRepository repository;
    private final LocalFileStorageService storageService;

    @Autowired
    public FileService(FileStorageProperties properties, FileMetadataRepository repository, LocalFileStorageService storageService)
    {
        this.properties = properties;
        this. repository = repository;
        this.storageService = storageService;
    }

    public FileMetadata uploadFile(MultipartFile file, String sessionId) throws IOException
    {
        validateFile(file);

        String storagePath;
        try(InputStream inputStream = file.getInputStream())
        {
            storagePath = storageService.storeFile(inputStream, file.getOriginalFilename(), sessionId);
        }

        String fileName = storagePath.substring(storagePath.lastIndexOf(File.separator) + 1);
        FileMetadata metadata = FileMetadata.builder()
                .sessionId(sessionId)
                .timestamp(Instant.now())
                .storedName(fileName)
                .originalName(file.getOriginalFilename())
                .storedPath(storagePath)
                .mimeType(file.getContentType())
                .size(file.getSize())
                .build();

        return repository.save(metadata);
    }

    public Resource getFileResource (String fileName) throws IOException
    {
        FileMetadata metadata = getFileMetadata(fileName);
        return storageService.getFileResource(metadata.getStoredPath());
    }

    public FileMetadata getFileMetadata(String fileName) throws IOException
    {
        return repository.findById(fileName).orElseThrow(() -> new FileNotFoundException("No such file"));
    }

    private void validateFile(MultipartFile file)
    {
        if(file.isEmpty())
        {
            throw new IllegalArgumentException("File cannot be empty");
        }
        String mimeType = file.getContentType();
        if(mimeType == null || !properties.allowedFileTypes().contains(mimeType))
        {
            throw new IllegalArgumentException("Invalid mime type");
        }
    }

    public FileMetadata deleteFile(String fileName) throws IOException
    {
        FileMetadata metadata = getFileMetadata(fileName);
        storageService.deleteFile(metadata.getStoredPath());
        repository.delete(metadata);
        return metadata;
    }

    public List<FileMetadata> deleteSessionFiles(String sessionId) throws IOException
    {
        List<FileMetadata> filesToDelete = repository.findBySessionId(sessionId);
        List<FileMetadata> deletedFiles = new ArrayList<>();
        for(FileMetadata metadata : filesToDelete)
        {
                storageService.deleteFile(metadata.getStoredPath());
                repository.delete(metadata);
                deletedFiles.add(metadata);
        }
        storageService.deleteSessionFolder(sessionId);
        return deletedFiles;
    }
}
