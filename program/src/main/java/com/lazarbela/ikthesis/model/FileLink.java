package com.lazarbela.ikthesis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class FileLink {
    @Id
    @GeneratedValue
    private int id;
    private String filePath;
    private String sessionId;
}
