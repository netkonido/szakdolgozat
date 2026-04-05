package com.lazarbela.ikthesis.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
public class WorkExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String content;

    @ManyToOne
    @JoinColumn(name="session_sessionId", nullable = false)
    private Session session;


    public WorkExperience (String content)
    {
        this.content = content;
    }
}
