package com.lazarbela.ikthesis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="session_id", nullable = false)
    private Session session;


    public WorkExperience (String content)
    {
        this.content = content;
    }
}
