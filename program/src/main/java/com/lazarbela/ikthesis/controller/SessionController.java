package com.lazarbela.ikthesis.controller;

import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.service.AIService;
import com.lazarbela.ikthesis.service.SessionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/session")
@AllArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    /**
     * Initializes a new session.
     *
     * @return {@code ResponseEntity} with status OK, cookie set in the header
     * and a body containing the sessionId of the new {@code Session}.
     */
    @GetMapping("/new")
    public ResponseEntity<?> getNewSession(HttpServletResponse response)
    {
        String sessionId = sessionService.newSession().getSessionId();

        HttpCookie cookie = ResponseCookie.from("sessionId", sessionId).httpOnly(true).path("/").build();

        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(sessionId);
    }

    /**
     * GET request for an existing session.
     *
     * @param sessionId id of the requested session.
     * @return {@code ResponseEntity} with status OK, if the session exists,
     * {@code ResponseEntity} with status NOT FOUND otherwise.
     */
    @GetMapping("/get")
    public ResponseEntity<?> getExistingSession(
            @CookieValue("sessionId") String sessionId)
    {
        Session session;
        try {
            session = sessionService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }

    /**
     * DELETE operation for a session.
     *
     * @param sessionId id of the session to be deleted.
     * @return {@code ResponseEntity} with status OK
     * and cookie set to empty string in the header, if session with provided id exists,
     * {@code ResponseEntity} with status BAD REQUEST or INTERNAL SERVER ERROR otherwise.
     */
    // TODO: unit test for service
    @DeleteMapping("/end")
    public ResponseEntity<?> deleteSession(
            @CookieValue("sessionId") String sessionId)
    {
        try {
            sessionService.endSession(sessionId);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
        catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, "").build();
    }

    /**
     * Returns the resume's preview as a string.
     *
     * @param sessionId id of the requested session.
     * @return {@code ResponseEntity} with status OK and body containing the resume's preview as a string,
     * if the session exists, {@code ResponseEntity} with status NOT FOUND or INTERNAL SERVER ERROR otherwise.
     */
    // TODO: unit test for service
    @GetMapping("/resume-preview")
    public ResponseEntity<?> resumePreview(
            @CookieValue("sessionId") String sessionId)
    {
        Session session;
        try {
            session = sessionService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.notFound().build();
        }

        while (session.getResumePreviewString().isBlank()) {
            synchronized (AIService.resumePreviewWaitObject) {
                try {
                    AIService.resumePreviewWaitObject.wait();
                } catch (InterruptedException e) {
                    return ResponseEntity.internalServerError().body("Wait interrupted.");
                }
            }
        }

        return ResponseEntity.ok(session.getResumePreviewString());
    }

    /**
     * Returns the resume's preview as a string.
     *
     * @param sessionId id of the requested session.
     * @return {@code ResponseEntity} with status OK and body containing the resume's preview as a string,
     * if the session exists, {@code ResponseEntity} with status NOT FOUND or INTERNAL SERVER ERROR otherwise.
     */
    @PostMapping("/resume-preview")
    public ResponseEntity<?> postResumePreview(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        Session session;
        try {
            session = sessionService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }

        session.setResumePreviewString(content);

        return ResponseEntity.ok(sessionService.saveSession(session));
    }
}
