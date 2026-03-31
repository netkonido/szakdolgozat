package com.lazarbela.ikthesis.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sessionId;

    @NonNull
    private String secret;

    @NonNull
    private Instant timestamp;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userData_id", referencedColumnName = "id")
    private UserData userData;

    @Singular
    @OneToMany
    private List<Certification> certifications;

    @Singular
    @OneToMany
    private List<Education> educations;

    @Singular
    @OneToMany
    private List<FileMetadata> files;

    @Singular
    @OneToMany
    private List<OtherField> otherFields;

    @Singular
    @OneToMany
    private List<WorkExperience> workExperiences;

}
