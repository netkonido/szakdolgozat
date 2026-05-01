package com.lazarbela.ikthesis.service;

import com.lazarbela.ikthesis.model.FileMetadata;
import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.repository.FileMetadataRepository;
import com.lazarbela.ikthesis.repository.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileService {
    private final FileStorageProperties properties;
    private final FileMetadataRepository fileMetadataRepository;
    private final LocalFileStorageService storageService;
    private final SessionRepository sessionRepository;

    @Autowired
    public FileService(FileStorageProperties properties, FileMetadataRepository repository, LocalFileStorageService storageService, SessionRepository sessionRepository)
    {
        this.properties = properties;
        this.fileMetadataRepository = repository;
        this.storageService = storageService;
        this.sessionRepository = sessionRepository;
    }

    public FileMetadata uploadFile(MultipartFile file, String sessionId) throws IOException
    {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");

        validateFile(file);

        String storagePath;
        try(InputStream inputStream = file.getInputStream())
        {
            storagePath = storageService.storeFile(inputStream, file.getOriginalFilename(), session.get().getSessionId());
        }

        String fileName = storagePath.substring(storagePath.lastIndexOf(File.separator) + 1);
        FileMetadata metadata = FileMetadata.builder()
                .session(session.get())
                .timestamp(Instant.now())
                .fileName(fileName)
                .originalName(file.getOriginalFilename())
                .storedPath(storagePath)
                .mimeType(file.getContentType())
                .size(file.getSize())
                .isResume(false)
                .build();

        return fileMetadataRepository.save(metadata);
    }

    public Resource getFileResource (String fileName) throws IOException
    {
        FileMetadata metadata = getFileMetadata(fileName);
        return storageService.getFileResource(metadata.getStoredPath());
    }

    public FileMetadata getFileMetadata(String fileName) throws IOException
    {
        return fileMetadataRepository.findById(fileName).orElseThrow(() -> new FileNotFoundException("No such file"));
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

    @Transactional
    public FileMetadata deleteFile(String fileName) throws IOException
    {
        FileMetadata metadata = getFileMetadata(fileName);
        storageService.deleteFile(metadata.getStoredPath());
        fileMetadataRepository.delete(metadata);
        fileMetadataRepository.flush();
        return metadata;
    }

    @Transactional
    public FileMetadata deleteFile(FileMetadata metadata) throws IOException
    {
        storageService.deleteFile(metadata.getStoredPath());
        fileMetadataRepository.delete(metadata);
        fileMetadataRepository.flush();
        return metadata;
    }

    public void deleteResumes(String sessionId) throws IOException {
        Set<FileMetadata> sessionResumes = fileMetadataRepository
                .findAll()
                .stream()
                .filter(fileMetadata -> fileMetadata
                        .getSession()
                        .getSessionId()
                        .equals(sessionId)
                        && fileMetadata.isResume())
                .collect(Collectors.toSet());

        sessionResumes.forEach(fileMetadata -> {
            try{
                deleteFile(fileMetadata);
            }
            catch (IOException e){
                System.out.println(fileMetadata.getStoredPath());
            }
        });
    }

    public FileMetadata deleteConvertedResumes(String sessionId) throws IOException{
        Set<FileMetadata> resumes = fileMetadataRepository
                .findAll()
                .stream()
                .filter(fileMetadata -> fileMetadata
                            .getSession()
                            .getSessionId()
                            .equals(sessionId)
                && fileMetadata.isResume())
                .collect(Collectors.toSet());

        FileMetadata markdownResume;
        markdownResume = resumes
                .stream()
                .filter(fileMetadata ->
                        fileMetadata
                                .getMimeType()
                                .equals("text/markdown")
                )
                .findFirst()
                .orElseThrow(()-> new NoSuchElementException("no Markdown resume found"));

        resumes.remove(markdownResume);
        resumes.forEach(fileMetadata ->{
            try{
                deleteFile(fileMetadata);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                //throw new RuntimeException(e.getMessage());
            }
        });
        return markdownResume;
    }

    public Set<FileMetadata> deleteSessionFiles(String sessionId) throws IOException
    {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");

        Set<FileMetadata> filesToDelete = session.get().getFiles();
        Set<FileMetadata> deletedFiles = new HashSet<>();
        for(FileMetadata metadata : filesToDelete)
        {
            try {
                storageService.deleteFile(metadata.getStoredPath());
                fileMetadataRepository.delete(metadata);
                deletedFiles.add(metadata);
            }
            catch (IOException e)
            {
                System.err.println("Could not delete file " +metadata.getStoredPath());
            }
        }
        storageService.deleteDirectory(sessionId);
        return deletedFiles;
    }

    public String cleanFileStorage(Set<String> validSessionIds)
    {
        return storageService.cleanStorageFolder(validSessionIds);
    }

    public Set<String> getAvailableResumeFileTypes(String sessionId){
        Optional<Session> maybeSession = sessionRepository.findById(sessionId);
        if(maybeSession.isEmpty())
            throw new IllegalArgumentException("Session id not found");
        Session session =maybeSession.get();


        return session
                .getFiles()
                .stream()
                .filter(FileMetadata::isResume)
                .map(fileMetadata -> mimeTypeExtensionMapper(fileMetadata.getMimeType()))
                .collect(Collectors.toSet());
    }

    public FileMetadata saveResume(InputStream fileStream, String sessionId, String extension, long fileSize) throws IOException {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");

        String storagePath;
        storagePath = storageService.storeFile(fileStream, session.get().getUserData().getName() + "." + extension, session.get().getSessionId());

        String fileName = storagePath.substring(storagePath.lastIndexOf(File.separator) + 1);
        FileMetadata metadata = FileMetadata.builder()
                .session(session.get())
                .timestamp(Instant.now())
                .fileName(fileName)
                .originalName("Resume " + session.get().getUserData().getName() + "." + extension)
                .storedPath(storagePath)
                .mimeType(mimeTypeExtensionMapper(extension))
                .size(fileSize)
                .isResume(true)
                .build();

        return fileMetadataRepository.save(metadata);
    }

    public Set<FileMetadata> convertResumesFromMarkdown (FileMetadata markdownMetadata) throws IOException
    {
        Set<String> formats = Set.of("docx","html");
        Set<FileMetadata> outputFiles = new HashSet<FileMetadata>();
        for(String format : formats)
        {

            Map<String, Long> storageProperties = storageService.convertFile(
                    markdownMetadata.getSession().getSessionId(),
                    markdownMetadata.getFileName(),
                    markdownMetadata.getSession().getUserData().getName() + "." + format,
                    "markdown" ,
                    format
            );

            String storagePath = storageProperties.keySet().stream().findAny().orElse("");

            String fileName = storagePath.substring(storagePath.lastIndexOf(File.separator) + 1);
            long fileSize = storageProperties.get(storagePath);
            FileMetadata metadata = FileMetadata.builder()
                    .session(markdownMetadata.getSession())
                    .timestamp(Instant.now())
                    .fileName(fileName)
                    .originalName("Resume " + markdownMetadata.getSession().getUserData().getName() + "." + format)
                    .storedPath(storagePath)
                    .mimeType(mimeTypeExtensionMapper(format))
                    .size(fileSize)
                    .isResume(true)
                    .build();
            outputFiles.add(fileMetadataRepository.save(metadata));
        }
        return outputFiles;
    }

    public String mimeTypeExtensionMapper(String from)
    {
        return properties.mimeTypeExtensionMap().get(from);
    }

}
