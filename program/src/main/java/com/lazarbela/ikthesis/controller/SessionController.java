package com.lazarbela.ikthesis.controller;

import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.service.DataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/session")
@AllArgsConstructor
public class SessionController {

    private final DataService dataService;

    @GetMapping("/new")
    public ResponseEntity<String> getNewSession()
    {
        return ResponseEntity.ok(dataService.newSession().getSessionId());
    }

    @GetMapping("/get")
    public ResponseEntity<?> getSession(@RequestParam("sessionId") String sessionId)
    {
        Session session;
        try{
            session = dataService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(session);
    }

    @DeleteMapping("/end")
    public ResponseEntity<?> deleteSession(@RequestParam("sessionId") String sessionId)
    {
        Session session;
        try {
            session = dataService.endSession(sessionId);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch(IOException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(session);
    }
}
