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

    /**
     * Retrieves a job description.
     *
     * @param sessionId id of the session linked to the job description.
     * @return {@code JobDescription} linked to the requested session.
     */
    @GetMapping("/job-description")
    public ResponseEntity<JobDescription> getJobDescription(@CookieValue("sessionId") String sessionId)
    {
        try {
            return ResponseEntity.ok(dataService.getJobDescription(sessionId));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Creates a job description.
     *
     * @param sessionId id of the session linked to the new job description.
     * @param content content of the new job description.
     * @return the created {@code JobDescription}.
     */
    @PostMapping("/job-description")
    public ResponseEntity<JobDescription> postJobDescription(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        Session session;
        try {
            session = sessionService.getSessionById(sessionId);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        JobDescription jobDescription = new JobDescription();
        jobDescription.setContent(content);
        jobDescription.setSession(session);

        dataService.saveJobDescription(jobDescription);
        return ResponseEntity.ok(jobDescription);
    }

    /**
     * Updates a job description.
     *
     * @param sessionId id of the session linked to the job description.
     * @param content new content for the job description.
     * @return the updated {@code JobDescription}.
     */
    @PatchMapping("/job-description")
    public ResponseEntity<JobDescription> updateJobDescription(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("content") String content
    ) {
        JobDescription jobDescriptionUpdated;

        try {
            jobDescriptionUpdated = dataService.updateJobDescription(sessionId, content);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(jobDescriptionUpdated);
    }

    /**
     * Retrieves user data.
     *
     * @param sessionId id of the session linked to the user data.
     * @return {@code UserData} linked to the requested session.
     */
    @GetMapping("/user-data")
    public ResponseEntity<UserData> getUserData(@CookieValue("sessionId") String sessionId)
    {
        try{
            return ResponseEntity.ok(dataService.getUserData(sessionId));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Creates user data.
     *
     * @param sessionId id of the session linked to the new user data.
     * @param name name of the new user.
     * @param email email address of the new user.
     * @param telephone telephone number of the new user.
     * @return the created {@code UserData}.
     */
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
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Updates user data.
     *
     * @param name new name for the user.
     * @param email new email address for the new user.
     * @param telephone new telephone number for the new user.
     * @return the updated {@code UserData}.
     */
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
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieves certifications.
     *
     * @param sessionId id of the session linked to the certifications.
     * @return {@code Set<Certification>} set of certifications linked to the requested session.
     */
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

    /**
     * Creates a certification.
     *
     * @param sessionId id of the session linked to the new certification.
     * @param content of the new certification.
     * @return the created {@code Certification}.
     */
    @PostMapping("/certifications")
    public ResponseEntity<Certification> postCertification (
            @CookieValue("sessionId") String sessionId,
            @RequestParam("content") String content)
    {
        try {
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
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Updates a certification.
     *
     * @param sessionId id of the session linked to the certification.
     * @param certificationId id of the certification.
     * @param content content of the certification.
     * @return the updated {@code Certification}.
     */
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

    /**
     * Deletes a certification.
     *
     * @param sessionId id of the session linked to the certification.
     * @param certificationId id of the certification.
     */
    @DeleteMapping("/certifications")
    public ResponseEntity<Void> deleteCertification(
            @CookieValue("sessionId") String sessionId,
            @RequestParam("id") int certificationId
    ) {
        try {
            dataService.deleteCertification(sessionId, certificationId);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves educations.
     *
     * @param sessionId id of the session linked to the educations.
     * @return {@code Set<Education>} set of educations linked to the requested session.
     */
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

    /**
     * Created an education.
     *
     * @param sessionId id of the session linked to the new education.
     * @param content of the new education.
     * @return the created {@code Education}.
     */
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

    /**
     * Updates an education.
     *
     * @param sessionId id of the session linked to the education.
     * @param educationId id of the education.
     * @param content content of the education.
     * @return the updated {@code Education}.
     */
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

    /**
     * Deletes an education.
     *
     * @param sessionId id of the session linked to the education.
     * @param educationId id of the education.
     */
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

    /**
     * Retrieves other fields.
     *
     * @param sessionId id of the session linked to the other fields.
     * @return {@code Set<OtherField>} set of other fields linked to the requested session.
     */
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

    /**
     * Creates a new other field.
     *
     * @param sessionId id of the session linked to the other field.
     * @param content of the new other field.
     * @return the created {@code OtherField}.
     */
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

    /**
     * Updates an other field.
     *
     * @param sessionId id of the session linked to the other field.
     * @param otherFieldId id of the other field.
     * @param content content of the other field.
     * @return the updated {@code OtherField}.
     */
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

    /**
     * Deletes an other field.
     *
     * @param sessionId id of the session linked to the other field.
     * @param otherFieldId id of the other field.
     */
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

    /**
     * Retrieves work experiences.
     *
     * @param sessionId id of the session linked to the work experiences.
     * @return {@code Set<WorkExperience>} set of work experiences linked to the requested session.
     */
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

    /**
     * Creates a new work experience.
     *
     * @param sessionId id of the session linked to the work experience.
     * @param content of the new work experience.
     * @return the created {@code WorkExperience}.
     */
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

    /**
     * Updates work experiences.
     *
     * @param sessionId id of the session linked to the work experience.
     * @param workExperienceId id of the work experience.
     * @param content content of the work experience.
     * @return the updated {@code WorkExperience}.
     */
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

    /**
     * Deletes a work experience.
     *
     * @param sessionId id of the session linked to the work experience.
     * @param workExperienceId id of the work experience.
     */
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
