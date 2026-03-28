package com.lazarbela.ikthesis.controller;

import com.lazarbela.ikthesis.model.*;
import com.lazarbela.ikthesis.service.DataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/data")
public class DataController {

    //TODO: DELETE mappings
    //TODO: UPDATE mappings

    private DataService dataService;

    @GetMapping("/session-information")
    public ResponseEntity<?> getSessionInformation (@PathVariable String sessionId)
    {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/user")
    public User getUserData (@RequestParam("sessionId") String sessionId)
    {
        return dataService.getUserData(sessionId);
    }

    @PostMapping("/user")
    public ResponseEntity<?> postUserData (
            @RequestParam("sessionId") String sessionId,
            @RequestParam("userData") User userData)
    {
        dataService.saveUser(userData);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/certifications")
    public List<Certification> getCertifications (@RequestParam("sessionId") String sessionId)
    {
        return dataService.getCertifications(sessionId);
    }

    @PostMapping("/certifications")
    public ResponseEntity<?> postCertification (
            @RequestParam("sessionId") String sessionId,
            @RequestParam("certification") Certification certification)
    {
        dataService.saveCertification(certification);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/educations")
    public List<Education> getEducations (@RequestParam("sessionId") String sessionId)
    {
        return dataService.getEducations(sessionId);
    }

    @PostMapping("/educations")
    public ResponseEntity<?> postEducation (
            @RequestParam("sessionId") String sessionId,
            @RequestParam("education") Education education)
    {
        dataService.saveEducation(education);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/other-fields")
    public List<OtherField> getOtherFields (@RequestParam("sessionId") String sessionId)
    {
        return dataService.getOtherFields(sessionId);
    }

    @PostMapping("/other-fields")
    public ResponseEntity<?> postOtherFields (
            @RequestParam("sessionId") String sessionId,
            @RequestParam("otherField") OtherField otherField)
    {
        dataService.saveOtherField(otherField);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/work-experiences")
    public List<WorkExperience> getWorkExperiences (@RequestParam("sessionId") String sessionId)
    {
        return dataService.getWorkExperiences(sessionId);
    }

    @GetMapping("/work-experiences")
    public ResponseEntity<?> getWorkExperiences (
            @RequestParam("sessionId") String sessionId,
            @RequestParam("workExperience") WorkExperience workExperience)
    {
        dataService.saveWorkExperience(workExperience);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/uploaded-files")
    public ResponseEntity<?> getUploadedFiles (@RequestParam("sessionId") String sessionId)
    {
        // do processing - remove store location or mask it
        return ResponseEntity.ok( dataService.getFiles(sessionId)
                .stream().map(
                        (item) ->
                                Map.ofEntries(
                                        Map.entry("originalName", item.getOriginalName()),
                                        Map.entry("size", item.getSize())
                                )
                )
        );
    }
}
