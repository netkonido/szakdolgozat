package com.lazarbela.ikthesis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;


    public JobDescription(Session session, String content)
    {
        this.content = content;
        this.session = session;
    }
}
