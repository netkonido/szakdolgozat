package com.lazarbela.ikthesis.controller;

import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.service.AIService;
import com.lazarbela.ikthesis.service.DataService;
import com.lazarbela.ikthesis.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@CrossOrigin(origins="http://localhost:5173/", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/session")
@AllArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/new")
    public ResponseEntity<?> getNewSession(HttpServletResponse response)
    {
        String sessionId = sessionService.newSession().getSessionId();
        HttpCookie cookie = ResponseCookie.from("sessionId", sessionId)
                .httpOnly(true)
                .path("/")
                .build();
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(sessionId);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getExistingSession(@CookieValue("sessionId") String sessionId)
    {
        Session session;
        try{
            session = sessionService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(session);
    }

    @DeleteMapping("/end")
    public ResponseEntity<?> deleteSession(@CookieValue("sessionId") String sessionId)
    {
        Session session;
        try {
            session = sessionService.endSession(sessionId);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e)
        {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, "").body(session);
    }

    @GetMapping("/resume-preview")
    public ResponseEntity<?> resumePreview(@CookieValue("sessionId") String sessionId)
    {
        Session session;
        try{
            session = sessionService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }

        while(session.getResumePreviewString().isBlank()){
            synchronized (AIService.resumePreviewWaitObject){
                try {
                    AIService.resumePreviewWaitObject.wait();
                } catch (InterruptedException e) {
                    return ResponseEntity.internalServerError().body("wait interrupted.");
                }
            }
        }
        return ResponseEntity.ok(session.getResumePreviewString());
    }

    @PostMapping("/resume-preview")
    public ResponseEntity<?> postResumePreview(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        try{
            Session session = sessionService.getSessionById(sessionId);
            session.setResumePreviewString(content);
            return ResponseEntity.ok(sessionService.saveSession(session));
        }
        catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
