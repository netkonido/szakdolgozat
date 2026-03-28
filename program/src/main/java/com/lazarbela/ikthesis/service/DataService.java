package com.lazarbela.ikthesis.service;

import com.lazarbela.ikthesis.model.*;
import com.lazarbela.ikthesis.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataService {

    CertificationRepository certificationRepository;
    EducationRepository educationRepository;
    FileMetadataRepository fileMetadataRepository;
    OtherFieldRepository otherFieldRepository;
    SessionRepository sessionRepository;
    UserRepository userRepository;
    WorkExperienceRepository workExperienceRepository;

    public List<Certification> getCertifications (String sessionId)
    {
        return certificationRepository.findBySessionId(sessionId);
    }

    public Certification saveCertification (Certification certification)
    {
        return certificationRepository.save(certification);
    }

    public Certification updateCertification (int certificationId, Certification newCertification)
    {
        certificationRepository.deleteById(certificationId);
        return saveCertification(newCertification);
    }

    public List<Education> getEducations (String sessionId)
    {
        return educationRepository.findBySessionId(sessionId);
    }

    public Education saveEducation (Education education)
    {
        return educationRepository.save(education);
    }

    public Education updateEducation (int educationId, Education newEducation)
    {
        educationRepository.deleteById(educationId);
        return saveEducation(newEducation);
    }

    public List<FileMetadata> getFiles (String sessionId)
    {
        return fileMetadataRepository.findBySessionId(sessionId);
    }

    public List<OtherField> getOtherFields (String sessionId)
    {
        return otherFieldRepository.findBySessionId(sessionId);
    }

    public OtherField saveOtherField (OtherField otherField)
    {
        return otherFieldRepository.save(otherField);
    }

    public List<WorkExperience> getWorkExperiences (String sessionId)
    {
        return workExperienceRepository.findBySessionId(sessionId);
    }

    public WorkExperience saveWorkExperience(WorkExperience workExperience)
    {
        return workExperienceRepository.save(workExperience);
    }

    public User getUserData (String sessionId)
    {
        return userRepository.findBySessionId(sessionId);
    }

    public User saveUser (User user)
    {
        return userRepository.save(user);
    }
}
