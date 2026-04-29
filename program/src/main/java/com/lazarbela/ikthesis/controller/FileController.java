package com.lazarbela.ikthesis.controller;

import com.lazarbela.ikthesis.model.FileMetadata;
import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.service.DataService;
import com.lazarbela.ikthesis.service.FileService;
import com.lazarbela.ikthesis.service.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@CrossOrigin(origins="http://localhost:5173/", allowCredentials = "true")
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileService fileService;
    private final DataService dataService;
    private final SessionService sessionService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@CookieValue("sessionId") String sessionId, @RequestParam("file")MultipartFile file)
    {
        try{
            FileMetadata metadata = fileService.uploadFile(file, sessionId);

            return ResponseEntity.ok(Map.ofEntries(
                            Map.entry("fileId", metadata.getStoredName()),
                            Map.entry("originalName", metadata.getOriginalName())
                    ));
        }
        catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // redo to only allow resume download
    @GetMapping("/download/{fileType}")
    public ResponseEntity<Resource> downloadFile(@CookieValue("sessionId") String sessionId, @PathVariable("fileType") String fileType)
    {
        try {
            Session session = sessionService.getSessionById(sessionId);
            FileMetadata metadata = fileService.getFileMetadata(sessionId + "." + fileType);
            Resource resource = fileService.getFileResource(metadata.getStoredName());
            return ResponseEntity
                    .ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"Resume " + session.getUserData().getName() + "." + fileType + "\""
                    )
                    .contentType(MediaType.parseMediaType(metadata.getMimeType()))
                    .contentLength(metadata.getSize())
                    .body(resource);
        }
        catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam("id") String fileName, @CookieValue("sessionId") String sessionId)
    {
        try
        {
            FileMetadata metadata = fileService.deleteFile(fileName);
            if(!metadata.getSession().getSessionId().equals(sessionId))
            {
                throw new SecurityException("Access denied.");
            }
            return ResponseEntity.ok(Map.ofEntries(Map.entry("id", metadata.getStoredName()), Map.entry("originalName", metadata.getOriginalName())));
        }
        catch (IOException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/delete-all-files")
    public ResponseEntity<?> deleteSessionFiles(@CookieValue("sessionId") String sessionId)
    {
        try
        {
            Set<FileMetadata> deletedFiles = fileService.deleteSessionFiles(sessionId);
            return ResponseEntity.ok(deletedFiles.stream().map((item) -> Map.entry("originalName", item.getOriginalName())));
        }
        catch (IOException e)
        {
            return ResponseEntity.internalServerError().build();
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/uploaded-files")
    public ResponseEntity<?> getUploadedFiles (@CookieValue("sessionId") String sessionId)
    {
        try{
            Set<FileMetadata> files = dataService.getFiles(sessionId);
            return ResponseEntity.ok(files
                    .stream().map(
                            (item) ->
                                    Map.ofEntries(
                                            Map.entry("id", item.getStoredName()),
                                            Map.entry("originalName", item.getOriginalName()),
                                            Map.entry("size", item.getSize())
                                    )
                    )
            );
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
