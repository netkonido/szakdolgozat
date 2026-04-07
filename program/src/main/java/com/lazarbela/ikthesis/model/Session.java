package com.lazarbela.ikthesis.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sessionId;

    private Instant timestamp;

    @JsonManagedReference
    @OneToOne(mappedBy = "session")
    private UserData userData;

    @JsonManagedReference
    @OneToOne(mappedBy = "session")
    private JobDescription jobDescription;

    @JsonManagedReference
    @Singular
    @OneToMany(mappedBy = "session")
    private Set<Certification> certifications = new HashSet<>();

    @JsonManagedReference
    @Singular
    @OneToMany(mappedBy = "session")
    private Set<Education> educations;

    @JsonManagedReference
    @Singular
    @OneToMany(mappedBy = "session")
    private Set<FileMetadata> files;

    @JsonManagedReference
    @Singular
    @OneToMany(mappedBy = "session")
    private Set<OtherField> otherFields;

    @JsonManagedReference
    @Singular
    @OneToMany(mappedBy = "session")
    private Set<WorkExperience> workExperiences;

}
