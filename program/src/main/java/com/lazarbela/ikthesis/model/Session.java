package com.lazarbela.ikthesis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.io.File;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class Session {
    @Id
    @GeneratedValue
    private String SessionId;

    private User user;
    private List<Certification> certifications;
    private List<Education> educations;
    private List<FileLink> fileLinks;
    private List<OtherField> otherFields;
    private List<WorkExperience> workExperiences;

}
