package com.lazarbela.ikthesis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class JobDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String content;
    private String sessionId;

    @OneToOne(mappedBy = "jobDescription")
    private Session session;


    public JobDescription(String sessionId, String content)
    {
        this.sessionId = sessionId;
        this.content = content;
    }
}
