package com.lazarbela.ikthesis.service;

import com.lazarbela.ikthesis.model.*;
import com.lazarbela.ikthesis.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DataService {

    CertificationRepository certificationRepository;
    EducationRepository educationRepository;
    FileMetadataRepository fileMetadataRepository;
    OtherFieldRepository otherFieldRepository;
    SessionRepository sessionRepository;
    UserDataRepository userDataRepository;
    WorkExperienceRepository workExperienceRepository;

    @Autowired
    public DataService (CertificationRepository cr, EducationRepository er, FileMetadataRepository fmr, OtherFieldRepository ofr, SessionRepository sr, UserDataRepository udr, WorkExperienceRepository wer)
    {
        certificationRepository = cr;
        educationRepository = er;
        fileMetadataRepository = fmr;
        otherFieldRepository = ofr;
        sessionRepository = sr;
        userDataRepository = udr;
        workExperienceRepository = wer;
    }

    public List<Certification> getCertifications (String sessionId)
    {
        return certificationRepository.findBySessionId(sessionId);
    }

    public Certification saveCertification (Certification certification)
    {
        return certificationRepository.save(certification);
    }

    public Certification updateCertification (String sessionId, int certificationId, String newContent)
    {
        Optional<Certification> certificationToModify = certificationRepository.findById(certificationId);
        if(certificationToModify.isEmpty())
            throw new IllegalArgumentException("Certification id not found");

        if(!certificationToModify.get().getSessionId().equals(sessionId))
            throw new SecurityException();

        certificationToModify.get().setSessionId(sessionId);
        certificationToModify.get().setContent(newContent);

        return saveCertification(certificationToModify.get());
    }

    public Certification deleteCertification (String sessionId, int certificationId)
    {
        Optional<Certification> certificationToDelete = certificationRepository.findById(certificationId);
        if(certificationToDelete.isEmpty())
            throw new IllegalArgumentException("Certification id not found");

        if(!certificationToDelete.get().getSessionId().equals(sessionId))
            throw new SecurityException();

        certificationRepository.delete(certificationToDelete.get());
        return  certificationToDelete.get();
    }

    public List<Education> getEducations (String sessionId)
    {
        return educationRepository.findBySessionId(sessionId);
    }

    public Education saveEducation (Education education)
    {
        return educationRepository.save(education);
    }

    public Education updateEducation (String sessionId, int educationId, String newContent)
    {
        Optional<Education> educationToModify = educationRepository.findById(educationId);
        if(educationToModify.isEmpty())
            throw new IllegalArgumentException("Education id not found");

        if(!educationToModify.get().getSessionId().equals(sessionId))
            throw new SecurityException();

        educationToModify.get().setContent(newContent);

        return saveEducation(educationToModify.get());
    }

    public Education deleteEducation (String sessionId, int educationId)
    {
        Optional<Education> educationToDelete = educationRepository.findById(educationId);
        if(educationToDelete.isEmpty())
            throw new IllegalArgumentException("Education id not found");

        if(!educationToDelete.get().getSessionId().equals(sessionId))
            throw new SecurityException();

        educationRepository.delete(educationToDelete.get());
        return  educationToDelete.get();
    }

    public List<OtherField> getOtherFields (String sessionId)
    {
        return otherFieldRepository.findBySessionId(sessionId);
    }

    public OtherField saveOtherField (OtherField otherField)
    {
        return otherFieldRepository.save(otherField);
    }

    public OtherField updateOtherField (String sessionId, int otherFieldId, String newContent)
    {
        Optional<OtherField> otherFieldToModify = otherFieldRepository.findById(otherFieldId);
        if(otherFieldToModify.isEmpty())
            throw new IllegalArgumentException("Other Field id not found");

        if(!otherFieldToModify.get().getSessionId().equals(sessionId))
            throw new SecurityException();

        otherFieldToModify.get().setContent(newContent);

        return otherFieldRepository.save(otherFieldToModify.get());
    }

    public OtherField deleteOtherField (String sessionId, int otherFieldId)
    {
        Optional<OtherField> otherFieldToDelete = otherFieldRepository.findById(otherFieldId);
        if(otherFieldToDelete.isEmpty())
            throw new IllegalArgumentException("Other Field id not found");

        if(!otherFieldToDelete.get().getSessionId().equals(sessionId))
            throw new SecurityException();

        otherFieldRepository.delete(otherFieldToDelete.get());
        return  otherFieldToDelete.get();
    }

    public List<WorkExperience> getWorkExperiences (String sessionId)
    {
        return workExperienceRepository.findBySessionId(sessionId);
    }

    public WorkExperience saveWorkExperience (WorkExperience workExperience)
    {
        return workExperienceRepository.save(workExperience);
    }

    public WorkExperience updateWorkExperience (String sessionId, int workExperienceId, String newContent)
    {
        Optional<WorkExperience> workExperienceToModify = workExperienceRepository.findById(workExperienceId);
        if(workExperienceToModify.isEmpty())
            throw new IllegalArgumentException("Work experience id not found");

        if(!workExperienceToModify.get().getSessionId().equals(sessionId))
            throw new SecurityException();

        workExperienceToModify.get().setContent(newContent);

        return workExperienceRepository.save(workExperienceToModify.get());
    }

    public WorkExperience deleteWorkExperience (String sessionId, int workExperienceId)
    {
        Optional<WorkExperience> workExperienceToDelete = workExperienceRepository.findById(workExperienceId);
        if(workExperienceToDelete.isEmpty())
            throw new IllegalArgumentException("Work experience id not found");

        if(!workExperienceToDelete.get().getSessionId().equals(sessionId))
            throw new SecurityException();

        workExperienceRepository.delete(workExperienceToDelete.get());
        return  workExperienceToDelete.get();
    }

    public UserData getUserData (String sessionId)
    {
        return userDataRepository.findBySessionId(sessionId);
    }

    public UserData saveUserData (UserData userData)
    {
        if(userDataRepository.existsBySessionId(userData.getSessionId()))
            throw new IllegalArgumentException("User data already exists.");

        return userDataRepository.save(userData);
    }

    public UserData updateUserData (String sessionId, Optional<String> name, Optional<String> email, Optional<String> telephone)
    {
        UserData userDataToModify = userDataRepository.findBySessionId(sessionId);
        if(userDataToModify == null)
            throw new IllegalArgumentException("User data does not exist");

        name.ifPresent(userDataToModify::setName);
        email.ifPresent(userDataToModify::setEmailAddress);
        telephone.ifPresent(userDataToModify::setTelephoneNumber);

        return userDataRepository.save(userDataToModify);
    }

    public UserData deleteUserData (String sessionId)
    {
        UserData toDelete = userDataRepository.findBySessionId(sessionId);
        userDataRepository.delete(toDelete);
        return toDelete;
    }

    public List<FileMetadata> getFiles (String sessionId)
    {
        return fileMetadataRepository.findBySessionId(sessionId);
    }
}
