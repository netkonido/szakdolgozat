package com.lazarbela.ikthesis.controller;

import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.service.DataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/actions")
public class ActionController {

    DataService dataService;

    @GetMapping("/extract-link")
    public ResponseEntity<?> extractLink(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("link") String link,
            @RequestParam("type") String type)
    {
        Session session;
        try{
            session = dataService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        if(type.equals("linkedIn"))
        {
            //extract linkedIn
        }
        else if(type.equals("jobLink"))
        {
            //extract Job Link
        }
        else
        {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        //return ResponseEntity.ok().build();
    }

    @GetMapping("/regenerate")
    public ResponseEntity<?> regenerate(@CookieValue("sessionId") String sessionId)
    {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}
