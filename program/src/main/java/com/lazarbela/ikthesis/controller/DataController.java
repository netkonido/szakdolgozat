package com.lazarbela.ikthesis.controller;

import com.lazarbela.ikthesis.model.*;
import com.lazarbela.ikthesis.service.DataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/data")
public class DataController {

    private final DataService dataService;

    @GetMapping("/session")
    public ResponseEntity<?> getSession(@RequestParam("sessionId") Optional<String> sessionId)
    {
        if(sessionId.isEmpty())
        {
            return ResponseEntity.ok(dataService.newSession());
        }
        else{
            Session session;
            try{
                session = dataService.getSessionById(sessionId.get());
            }
            catch (IllegalArgumentException e)
            {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(session);
        }
    }

    @GetMapping("/user-data")
    public UserData getUserData (@RequestParam("sessionId") String sessionId)
    {
        return dataService.getUserData(sessionId);
    }

    @PostMapping("/user-data")
    public ResponseEntity<UserData> postUserData (
            @RequestParam("sessionId") String sessionId,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("telephone") Optional<String> telephone)
    {
        try
        {
            Session session = dataService.getSessionById(sessionId);
            UserData newUserData;
            if(telephone.isPresent())
            {
                newUserData = dataService.saveUserData(new UserData(session, name, email, telephone.get()));
            }
            else
            {
                newUserData = dataService.saveUserData(new UserData(session, name, email));
            }
            return ResponseEntity.ok(newUserData);
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PatchMapping("/user-data")
    public ResponseEntity<UserData> patchUserData (
            @RequestParam("sessionId") String sessionId,
            @RequestParam("name") Optional<String> name,
            @RequestParam("email") Optional<String> email,
            @RequestParam("telephone") Optional<String> telephone)
    {
        try
        {
            UserData newUserData = dataService.updateUserData(sessionId, name, email, telephone);
            return ResponseEntity.ok(newUserData);
        }
        catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/user-data")
    public ResponseEntity<Void> deleteUserData(@RequestParam("sessionId") String sessionId)
    {
        dataService.deleteUserData(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/certifications")
    public Set<Certification> getCertifications (@RequestParam("sessionId") String sessionId)
    {
        return dataService.getCertifications(sessionId);
    }

    @PostMapping("/certifications")
    public ResponseEntity<Certification> postCertification (
            @RequestParam("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        Session session;
        try{
            session = dataService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }

        Certification certification = new Certification();
        certification.setContent(content);
        certification.setSession(session);

        dataService.saveCertification(certification);
        return ResponseEntity.ok(certification);
    }

    @PatchMapping("/certifications")
    public ResponseEntity<Certification> updateCertification(
            @RequestParam String sessionId,
            @RequestParam int certificationId,
            @RequestParam String content
    ) {
        try {
            Certification updated = dataService.updateCertification(sessionId, certificationId, content);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/certifications")
    public ResponseEntity<Void> deleteCertification(
            @RequestParam String sessionId,
            @RequestParam int certificationId
    ) {
        try {
            dataService.deleteCertification(sessionId, certificationId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/educations")
    public Set<Education> getEducations (@RequestParam("sessionId") String sessionId)
    {
        return dataService.getEducations(sessionId);
    }

    @PostMapping("/educations")
    public ResponseEntity<Education> postEducation (
            @RequestParam("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        Session session;
        try{
            session = dataService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }

        Education education = new Education();
        education.setContent(content);
        education.setSession(session);

        dataService.saveEducation(education);
        return ResponseEntity.ok(education);
    }

    @PatchMapping("/educations")
    public ResponseEntity<Education> updateEducation(
            @RequestParam String sessionId,
            @RequestParam int educationId,
            @RequestParam String content
    ) {
        try {
            Education updated = dataService.updateEducation(sessionId, educationId, content);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/educations")
    public ResponseEntity<Void> deleteEducation(
            @RequestParam String sessionId,
            @RequestParam int educationId
    ) {
        try {
            dataService.deleteEducation(sessionId, educationId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/other-fields")
    public Set<OtherField> getOtherFields (@RequestParam("sessionId") String sessionId)
    {
        return dataService.getOtherFields(sessionId);
    }

    @PostMapping("/other-fields")
    public ResponseEntity<OtherField> postOtherField (
            @RequestParam("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        Session session;
        try{
            session = dataService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }

        OtherField otherField = new OtherField();
        otherField.setContent(content);
        otherField.setSession(session);

        dataService.saveOtherField(otherField);
        return ResponseEntity.ok(otherField);
    }

    @PatchMapping("/other-fields")
    public ResponseEntity<OtherField> updateOtherField(
            @RequestParam String sessionId,
            @RequestParam int otherFieldId,
            @RequestParam String content
    ) {
        try {
            OtherField updated = dataService.updateOtherField(sessionId, otherFieldId, content);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/other-fields")
    public ResponseEntity<Void> deleteOtherField(
            @RequestParam String sessionId,
            @RequestParam int otherFieldId
    ) {
        try {
            dataService.deleteOtherField(sessionId, otherFieldId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/work-experiences")
    public Set<WorkExperience> getWorkExperiences (@RequestParam("sessionId") String sessionId)
    {
        return dataService.getWorkExperiences(sessionId);
    }

    @PostMapping("/work-experiences")
    public ResponseEntity<WorkExperience> postWorkExperience (
            @RequestParam("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        Session session;
        try{
            session = dataService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }

        WorkExperience workExperience = new WorkExperience();
        workExperience.setContent(content);
        workExperience.setSession(session);

        dataService.saveWorkExperience(workExperience);
        return ResponseEntity.ok(workExperience);
    }

    @PatchMapping("/work-experiences")
    public ResponseEntity<WorkExperience> updateWorkExperience(
            @RequestParam String sessionId,
            @RequestParam int workExperienceId,
            @RequestParam String content
    ) {
        try {
            WorkExperience updated = dataService.updateWorkExperience(sessionId, workExperienceId, content);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/work-experiences")
    public ResponseEntity<Void> deleteWorkExperience(
            @RequestParam String sessionId,
            @RequestParam int workExperienceId
    ) {
        try {
            dataService.deleteWorkExperience(sessionId, workExperienceId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/uploaded-files")
    public ResponseEntity<?> getUploadedFiles (@RequestParam("sessionId") String sessionId)
    {
        Set<FileMetadata> files;
        try{
            files = dataService.getFiles(sessionId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(files
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
