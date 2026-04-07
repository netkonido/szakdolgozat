package com.lazarbela.ikthesis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class FileMetadata {
    @Id
    private String storedName;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="session_id", nullable = false)
    private Session session;

    private String originalName;
    private String storedPath;
    private String mimeType;
    private Instant timestamp;
    private long size;
}
