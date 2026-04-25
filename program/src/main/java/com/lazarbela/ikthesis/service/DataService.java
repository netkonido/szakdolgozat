package com.lazarbela.ikthesis.service;

import com.lazarbela.ikthesis.model.*;
import com.lazarbela.ikthesis.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
public class DataService {

    final CertificationRepository certificationRepository;
    final EducationRepository educationRepository;
    final FileMetadataRepository fileMetadataRepository;
    final OtherFieldRepository otherFieldRepository;
    final SessionRepository sessionRepository;
    final UserDataRepository userDataRepository;
    final WorkExperienceRepository workExperienceRepository;
    final JobDescriptionRepository jobDescriptionRepository;

    final FileService fileService;

    @Autowired
    public DataService (CertificationRepository cr, EducationRepository er, FileMetadataRepository fmr, OtherFieldRepository ofr, SessionRepository sr, UserDataRepository udr, WorkExperienceRepository wer, JobDescriptionRepository jdr, FileService fs)
    {
        certificationRepository = cr;
        educationRepository = er;
        fileMetadataRepository = fmr;
        otherFieldRepository = ofr;
        sessionRepository = sr;
        userDataRepository = udr;
        workExperienceRepository = wer;
        jobDescriptionRepository = jdr;
        fileService = fs;
    }

    public Certification getCertification (int certificationId)
    {
        Optional<Certification> certification = certificationRepository.findById(certificationId);
        if(certification.isEmpty())
            throw new IllegalArgumentException("Certification id not found");
        return certification.get();
    }
    public Set<Certification> getCertifications (String sessionId)
    {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");
        return session.get().getCertifications();
    }

    public Certification saveCertification (Certification certification) { return certificationRepository.save(certification); }

    public Certification updateCertification (String sessionId, int certificationId, String newContent)
    {
        Optional<Certification> certificationToModify = certificationRepository.findById(certificationId);

        if(certificationToModify.isEmpty())
            throw new IllegalArgumentException("Certification id not found");

        if(!certificationToModify.get().getSession().getSessionId().equals(sessionId))
            throw new SecurityException("Session id does not match");

        certificationToModify.get().setContent(newContent);

        return saveCertification(certificationToModify.get());
    }

    public Certification deleteCertification (String sessionId, int certificationId)
    {
        Optional<Certification> certificationToDelete = certificationRepository.findById(certificationId);
        if(certificationToDelete.isEmpty())
            throw new IllegalArgumentException("Certification id not found");

        if(!certificationToDelete.get().getSession().getSessionId().equals(sessionId))
            throw new SecurityException("Session id does not match");

        certificationRepository.delete(certificationToDelete.get());
        return  certificationToDelete.get();
    }

    public Education getEducation(int educationId)
    {
        Optional<Education> education = educationRepository.findById(educationId);
        if(education.isEmpty())
            throw new IllegalArgumentException("Education id not found");

        return education.get();
    }

    public Set<Education> getEducations (String sessionId)
    {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");
        return session.get().getEducations();
    }

    public Education saveEducation (Education education) { return educationRepository.save(education); }

    public Education updateEducation (String sessionId, int educationId, String newContent)
    {
        Optional<Education> educationToModify = educationRepository.findById(educationId);
        if(educationToModify.isEmpty())
            throw new IllegalArgumentException("Education id not found");

        if(!educationToModify.get().getSession().getSessionId().equals(sessionId))
            throw new SecurityException("Session id does not match");

        educationToModify.get().setContent(newContent);

        return saveEducation(educationToModify.get());
    }

    public Education deleteEducation (String sessionId, int educationId)
    {
        Optional<Education> educationToDelete = educationRepository.findById(educationId);
        if(educationToDelete.isEmpty())
            throw new IllegalArgumentException("Education id not found");

        if(!educationToDelete.get().getSession().getSessionId().equals(sessionId))
            throw new SecurityException("Session id does not match");

        educationRepository.delete(educationToDelete.get());
        return  educationToDelete.get();
    }

    public OtherField getOtherField (int otherFieldId)
    {
        Optional<OtherField> otherField = otherFieldRepository.findById(otherFieldId);
        if(otherField.isEmpty())
            throw new IllegalArgumentException("Other Field id not found");

        return otherField.get();
    }

    public Set<OtherField> getOtherFields (String sessionId)
    {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");
        return session.get().getOtherFields();
    }

    public OtherField saveOtherField (OtherField otherField) { return otherFieldRepository.save(otherField); }

    public OtherField updateOtherField (String sessionId, int otherFieldId, String newContent)
    {
        Optional<OtherField> otherFieldToModify = otherFieldRepository.findById(otherFieldId);
        if(otherFieldToModify.isEmpty())
            throw new IllegalArgumentException("Other Field id not found");

        if(!otherFieldToModify.get().getSession().getSessionId().equals(sessionId))
            throw new SecurityException("Session id does not match");

        otherFieldToModify.get().setContent(newContent);

        return otherFieldRepository.save(otherFieldToModify.get());
    }

    public OtherField deleteOtherField (String sessionId, int otherFieldId)
    {
        Optional<OtherField> otherFieldToDelete = otherFieldRepository.findById(otherFieldId);
        if(otherFieldToDelete.isEmpty())
            throw new IllegalArgumentException("Other Field id not found");

        if(!otherFieldToDelete.get().getSession().getSessionId().equals(sessionId))
            throw new SecurityException("Session id does not match");

        otherFieldRepository.delete(otherFieldToDelete.get());
        return  otherFieldToDelete.get();
    }

    public WorkExperience getWorkExperience (int workExperienceId)
    {
        Optional<WorkExperience> workExperience = workExperienceRepository.findById(workExperienceId);
        if(workExperience.isEmpty())
            throw new IllegalArgumentException("Work Experience id not found");
        return workExperience.get();
    }

    public Set<WorkExperience> getWorkExperiences (String sessionId)
    {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");
        return session.get().getWorkExperiences();
    }

    public WorkExperience saveWorkExperience (WorkExperience workExperience) { return workExperienceRepository.save(workExperience); }

    public WorkExperience updateWorkExperience (String sessionId, int workExperienceId, String newContent)
    {
        Optional<WorkExperience> workExperienceToModify = workExperienceRepository.findById(workExperienceId);
        if(workExperienceToModify.isEmpty())
            throw new IllegalArgumentException("Work experience id not found");

        if(!workExperienceToModify.get().getSession().getSessionId().equals(sessionId))
            throw new SecurityException("Session id does not match");

        workExperienceToModify.get().setContent(newContent);

        return workExperienceRepository.save(workExperienceToModify.get());
    }

    public WorkExperience deleteWorkExperience (String sessionId, int workExperienceId)
    {
        Optional<WorkExperience> workExperienceToDelete = workExperienceRepository.findById(workExperienceId);
        if(workExperienceToDelete.isEmpty())
            throw new IllegalArgumentException("Work experience id not found");

        if(!workExperienceToDelete.get().getSession().getSessionId().equals(sessionId))
            throw new SecurityException("Session id does not match");

        workExperienceRepository.delete(workExperienceToDelete.get());
        return  workExperienceToDelete.get();
    }

    public JobDescription getJobDescription (String sessionId)
    {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");

        return session.get().getJobDescription();
    }

    public JobDescription saveJobDescription (JobDescription jobDescription) { return jobDescriptionRepository.save(jobDescription); }

    public JobDescription updateJobDescription (String sessionId, String newContent)
    {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");

        JobDescription jobDescriptionToModify = session.get().getJobDescription();
        if(jobDescriptionToModify == null)
            throw new IllegalArgumentException("Job Description not set");

        jobDescriptionToModify.setContent(newContent);

        return jobDescriptionRepository.save(jobDescriptionToModify);
    }

    public UserData getUserData (String sessionId)
    {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");

        return session.get().getUserData();
    }

    public UserData saveUserData (UserData userData) { return userDataRepository.save(userData); }

    public UserData updateUserData (String sessionId, Optional<String> name, Optional<String> email, Optional<String> telephone)
    {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");

        UserData userDataToModify = session.get().getUserData();
        if(userDataToModify == null)
            throw new IllegalArgumentException("User data not set");

        name.ifPresent(userDataToModify::setName);
        email.ifPresent(userDataToModify::setEmailAddress);
        telephone.ifPresent(userDataToModify::setTelephoneNumber);

        return userDataRepository.save(userDataToModify);
    }

    public Set<FileMetadata> getFiles (String sessionId)
    {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");
        return session.get().getFiles();
    }



}
