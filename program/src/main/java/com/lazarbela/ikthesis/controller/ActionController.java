package com.lazarbela.ikthesis.controller;

import com.lazarbela.ikthesis.model.FileMetadata;
import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.service.AIService;
import com.lazarbela.ikthesis.service.FileService;
import com.lazarbela.ikthesis.service.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.stream.Collectors;

@CrossOrigin(origins="http://localhost:5173/", allowCredentials = "true")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/actions")
public class ActionController {

    AIService aiService;
    SessionService sessionService;
    FileService fileService;

    @PostMapping("/import-data")
    public ResponseEntity<?> importData(
            @CookieValue("sessionId") String sessionId)
    {
        Session session;
        try{
            session = sessionService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        StringBuilder returnString = new StringBuilder();

        returnString.append("Result of link: ");
        try {
            //returnString.append(aiService.extractLinkedInLink(session, link));
        }
        catch (RestClientException e){
            return ResponseEntity.internalServerError().body("Could not fetch AI data: " + e.getMessage());
        }
        catch(NullPointerException e){
            return ResponseEntity.internalServerError().body("AI return data is null");
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body("Other issue: " + e.getMessage());
        }

        returnString.append("Result of files: ");
        try {
            returnString.append( aiService.extractFiles(session));
        }
        catch (RestClientException e){
            return ResponseEntity.internalServerError().body("Could not AI process files: " + e.getMessage());
        }
        catch(NullPointerException e){
            return ResponseEntity.internalServerError().body("AI return data is null");
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body("Other issue: " + e.getMessage());
        }

        return ResponseEntity.ok(returnString.toString());
    }

    @GetMapping("/regenerate")
    public ResponseEntity<?> regenerateResume(@CookieValue("sessionId") String sessionId)
    {
        Session session;
        try{
            session = sessionService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        try{
            fileService.deleteResumes(sessionId);
        }
        catch (IOException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        try{
            String response = aiService.createResume(session);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/prepare-resume")
    public ResponseEntity<?> prepareResume(@CookieValue("sessionId") String sessionId)
    {
        try{
            fileService.deleteResumes(sessionId);
        }
        catch (IOException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        Session session;
        try{
            session = sessionService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        try {
            aiService.createResume(session);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

}
