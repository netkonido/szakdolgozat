package com.lazarbela.ikthesis.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sessionId;

    private Instant timestamp;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userData_id", referencedColumnName = "id")
    private UserData userData;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "jobDescription_id", referencedColumnName = "id")
    private JobDescription jobDescription;

    @Singular
    @OneToMany(mappedBy = "session")
    private Set<Certification> certifications;

    @Singular
    @OneToMany(mappedBy = "session")
    private Set<Education> educations;

    @Singular
    @OneToMany(mappedBy = "session")
    private Set<FileMetadata> files;

    @Singular
    @OneToMany(mappedBy = "session")
    private Set<OtherField> otherFields;

    @Singular
    @OneToMany(mappedBy = "session")
    private Set<WorkExperience> workExperiences;

}
