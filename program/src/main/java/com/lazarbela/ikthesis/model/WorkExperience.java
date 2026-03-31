package com.lazarbela.ikthesis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.jdbc.Work;

@Data
@NoArgsConstructor
@Entity
public class WorkExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String content;
    private String sessionId;

    public WorkExperience (String sessionId, String content)
    {
        this.content = content;
        this.sessionId = sessionId;
    }
}
