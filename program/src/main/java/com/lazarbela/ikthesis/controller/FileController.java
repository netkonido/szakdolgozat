package com.lazarbela.ikthesis.controller;

import com.lazarbela.ikthesis.model.FileMetadata;
import com.lazarbela.ikthesis.service.FileService;
import jdk.jshell.spi.ExecutionControl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

// File storage controller for uploads and downloads
//local file store: ./files
//remote file store (maybe @casabellanas.synology.me file server hosted by me)

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/files")
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("sessionId") String sessionId, @RequestParam("file")MultipartFile file)
    {
        try{
            FileMetadata metadata = fileService.uploadFile(file, sessionId);

            return ResponseEntity.ok(Map.ofEntries(
                            Map.entry("fileId", metadata.getStoredName()),
                            Map.entry("originalName", metadata.getOriginalName())
                    ));
        } catch (IOException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // redo to only allow resume download
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("sessionId") String sessionId)
    {
        try
        {
            FileMetadata metadata = fileService.getFileMetadata(sessionId);
            Resource resource = fileService.getFileResource(sessionId);
            return ResponseEntity
                    .ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + metadata.getOriginalName() + "\""
                    )
                    .contentType(MediaType.parseMediaType(metadata.getMimeType()))
                    .contentLength(metadata.getSize())
                    .body(resource);
        }
        catch (IOException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam("fileName") String fileName, @RequestParam("sessionId") String sessionId)
    {
        try
        {
            FileMetadata metadata = fileService.deleteFile(fileName);
            if(!metadata.getSessionId().equals(sessionId))
            {
                throw new SecurityException("Access denied.");
            }
            return ResponseEntity.ok(Map.ofEntries(Map.entry("fileId", metadata.getStoredName()), Map.entry("originalName", metadata.getOriginalName())));
        }
        catch (IOException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/delete-all-files")
    public ResponseEntity<?> deleteSessionFiles(@RequestParam("sessionId") String sessionId)
    {
        try
        {
            List<FileMetadata> deletedFiles = fileService.deleteSessionFiles(sessionId);
            return ResponseEntity.ok(deletedFiles.stream().map((item) -> Map.entry("fileName", item.getOriginalName())));
        }
        catch (IOException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
