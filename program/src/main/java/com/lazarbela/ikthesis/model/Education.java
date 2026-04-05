package com.lazarbela.ikthesis.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String content;

    @ManyToOne
    @JoinColumn(name="session_sessionId", nullable = false)
    private Session session;

    public Education (Session session, String content)
    {
        this.content = content;
        this.session = session;
    }
}
