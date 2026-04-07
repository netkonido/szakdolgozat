package com.lazarbela.ikthesis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String emailAddress;
    private String telephoneNumber;


    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    public UserData (Session session, String name, String emailAddress, String telephoneNumber)
    {
        this.session = session;
        this.name = name;
        this.emailAddress = emailAddress;
        this.telephoneNumber = telephoneNumber;
    }

    public UserData (Session session, String name, String emailAddress)
    {
        this.session = session;
        this.name = name;
        this.emailAddress = emailAddress;
    }
}
