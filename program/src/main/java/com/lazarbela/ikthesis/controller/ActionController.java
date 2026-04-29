package com.lazarbela.ikthesis.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.service.AIService;
import com.lazarbela.ikthesis.service.DataService;
import com.lazarbela.ikthesis.service.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

@CrossOrigin(origins="http://localhost:5173/", allowCredentials = "true")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/actions")
public class ActionController {

    AIService aiService;
    SessionService sessionService;
    DataService dataService;

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

        String response = aiService.createResume(session);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/prepare")
    public ResponseEntity<?> prepareResume(@CookieValue("sessionId") String sessionId)
    {
        return ResponseEntity.ok().build();
    }

}
