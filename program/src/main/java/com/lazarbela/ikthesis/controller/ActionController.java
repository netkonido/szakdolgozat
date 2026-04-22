package com.lazarbela.ikthesis.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/actions")
public class ActionController {

    @GetMapping("/extract-link")
    public ResponseEntity<?> extractLink(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("link") String link,
            @RequestParam("type") String type)
    {
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
    public ResponseEntity<?> regenerate(@RequestParam("sessionId") String sessionId)
    {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}
