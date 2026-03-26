package com.lazarbela.ikthesis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private String originalName;
    private String storedPath;
    private String sessionId;
    private String mimeType;
    private Instant timestamp;
    private long size;
}
