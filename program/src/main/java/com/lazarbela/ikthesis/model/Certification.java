package com.lazarbela.ikthesis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String content;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="session_id", nullable = false)
    private Session session;

    public Certification (Session session, String content)
    {
        this.content = content;
        this.session = session;
    }
}