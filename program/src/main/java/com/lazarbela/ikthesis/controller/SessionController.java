package com.lazarbela.ikthesis.controller;

import com.lazarbela.ikthesis.model.Session;
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
}
