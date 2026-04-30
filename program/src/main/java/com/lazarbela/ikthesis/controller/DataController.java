package com.lazarbela.ikthesis.controller;

import com.lazarbela.ikthesis.model.*;
import com.lazarbela.ikthesis.service.DataService;
import com.lazarbela.ikthesis.service.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins="http://localhost:5173/", allowCredentials = "true")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/data")
public class DataController {

    private final DataService dataService;
    private final SessionService sessionService;

    @GetMapping("/job-description")
    public ResponseEntity<JobDescription> getJobDescription (@CookieValue("sessionId") String sessionId)
    {
        try {
            return ResponseEntity.ok(dataService.getJobDescription(sessionId));
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/job-description")
    public ResponseEntity<JobDescription> postJobDescription (
            @CookieValue("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        Session session;
        try{
            session = sessionService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        JobDescription jobDescription = new JobDescription();
        jobDescription.setContent(content);
        jobDescription.setSession(session);

        dataService.saveJobDescription(jobDescription);
        return ResponseEntity.ok(jobDescription);
    }

    @PatchMapping("/job-description")
    public ResponseEntity<JobDescription> updateJobDescription(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("content") String content
    ) {
        try {
            JobDescription updated = dataService.updateJobDescription(sessionId, content);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user-data")
    public ResponseEntity<UserData> getUserData (@CookieValue("sessionId") String sessionId)
    {
        try{
            return ResponseEntity.ok(dataService.getUserData(sessionId));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/user-data")
    public ResponseEntity<UserData> postUserData (
            @CookieValue("sessionId") String sessionId,
            @RequestParam("name") Optional<String> name,
            @RequestParam("email") Optional<String> email,
            @RequestParam("telephone") Optional<String> telephone)
    {
        try {
            Session session = sessionService.getSessionById(sessionId);
            UserData newUserData = new UserData(session);

            name.ifPresent(newUserData::setName);
            email.ifPresent(newUserData::setEmailAddress);
            telephone.ifPresent(newUserData::setTelephoneNumber);

            dataService.saveUserData(newUserData);
            return ResponseEntity.ok(newUserData);
        }
        catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/user-data")
    public ResponseEntity<UserData> patchUserData (
            @CookieValue("sessionId") String sessionId,
            @RequestParam("name") Optional<String> name,
            @RequestParam("email") Optional<String> email,
            @RequestParam("telephone") Optional<String> telephone)
    {
        try {
            UserData newUserData = dataService.updateUserData(sessionId, name, email, telephone);
            return ResponseEntity.ok(newUserData);
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/certifications")
    public ResponseEntity<Set<Certification>> getCertifications (@CookieValue("sessionId") String sessionId)
    {
        try{
            return ResponseEntity.ok(dataService.getCertifications(sessionId));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/certifications")
    public ResponseEntity<Certification> postCertification (
            @CookieValue("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        try{
            Session session = sessionService.getSessionById(sessionId);

            Certification certification = new Certification();
            certification.setContent(content);
            certification.setSession(session);

            dataService.saveCertification(certification);
            return ResponseEntity.ok(certification);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/certifications")
    public ResponseEntity<Certification> updateCertification(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("id") int certificationId,
            @RequestParam("content") String content
    ) {
        try {
            Certification updated = dataService.updateCertification(sessionId, certificationId, content);
            return ResponseEntity.ok(updated);
        }
        catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        catch(Exception e) {
                return ResponseEntity.internalServerError().build();
            }
    }

    @DeleteMapping("/certifications")
    public ResponseEntity<Void> deleteCertification(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("id") int certificationId
    ) {
        try {
            dataService.deleteCertification(sessionId, certificationId);
            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        catch(Exception e) {
                return ResponseEntity.internalServerError().build();
            }
    }

    @GetMapping("/educations")
    public ResponseEntity<Set<Education>> getEducations (@CookieValue("sessionId") String sessionId)
    {
        try {
            return ResponseEntity.ok(dataService.getEducations(sessionId));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/educations")
    public ResponseEntity<Education> postEducation (
            @CookieValue("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        try{
            Session session = sessionService.getSessionById(sessionId);

            Education education = new Education();
            education.setContent(content);
            education.setSession(session);

            dataService.saveEducation(education);
            return ResponseEntity.ok(education);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }


    }

    @PatchMapping("/educations")
    public ResponseEntity<Education> updateEducation(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("id") int educationId,
            @RequestParam("content") String content
    ) {
        try {
            Education updated = dataService.updateEducation(sessionId, educationId, content);
            return ResponseEntity.ok(updated);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/educations")
    public ResponseEntity<Void> deleteEducation(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("id") int educationId
    ) {
        try {
            dataService.deleteEducation(sessionId, educationId);
            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/other-fields")
    public ResponseEntity<Set<OtherField>> getOtherFields (@CookieValue("sessionId") String sessionId)
    {
        try{
            return ResponseEntity.ok(dataService.getOtherFields(sessionId));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/other-fields")
    public ResponseEntity<OtherField> postOtherField (
            @CookieValue("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        try{
            Session session = sessionService.getSessionById(sessionId);

            OtherField otherField = new OtherField();
            otherField.setContent(content);
            otherField.setSession(session);

            dataService.saveOtherField(otherField);
            return ResponseEntity.ok(otherField);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/other-fields")
    public ResponseEntity<OtherField> updateOtherField(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("id") int otherFieldId,
            @RequestParam("content") String content
    ) {
        try {
            OtherField updated = dataService.updateOtherField(sessionId, otherFieldId, content);
            return ResponseEntity.ok(updated);
        }
        catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        catch(Exception e) {
                return ResponseEntity.internalServerError().build();
            }
    }

    @DeleteMapping("/other-fields")
    public ResponseEntity<Void> deleteOtherField(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("id") int otherFieldId
    ) {
        try {
            dataService.deleteOtherField(sessionId, otherFieldId);
            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/work-experiences")
    public ResponseEntity<Set<WorkExperience>> getWorkExperiences (@CookieValue("sessionId") String sessionId)
    {
        try{
            return ResponseEntity.ok(dataService.getWorkExperiences(sessionId));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/work-experiences")
    public ResponseEntity<WorkExperience> postWorkExperience (
            @CookieValue("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        try{
            Session session = sessionService.getSessionById(sessionId);

            WorkExperience workExperience = new WorkExperience();
            workExperience.setContent(content);
            workExperience.setSession(session);

            dataService.saveWorkExperience(workExperience);
            return ResponseEntity.ok(workExperience);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/work-experiences")
    public ResponseEntity<WorkExperience> updateWorkExperience(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("id") int workExperienceId,
            @RequestParam("content") String content
    ) {
        try {
            WorkExperience updated = dataService.updateWorkExperience(sessionId, workExperienceId, content);
            return ResponseEntity.ok(updated);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
                return ResponseEntity.internalServerError().build();
            }
    }

    @DeleteMapping("/work-experiences")
    public ResponseEntity<Void> deleteWorkExperience(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("id") int workExperienceId
    ) {
        try {
            dataService.deleteWorkExperience(sessionId, workExperienceId);
            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
