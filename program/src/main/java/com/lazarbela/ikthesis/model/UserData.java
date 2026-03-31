package com.lazarbela.ikthesis.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String emailAddress;
    private String telephoneNumber;

    private String sessionId;

    @OneToOne(mappedBy = "userData")
    private Session session;

    public UserData (String sessionId, String name, String emailAddress, String telephoneNumber)
    {
        this.sessionId = sessionId;
        this.name = name;
        this.emailAddress = emailAddress;
        this.telephoneNumber = telephoneNumber;
    }

    public UserData (String sessionId, String name, String emailAddress)
    {
        this.sessionId = sessionId;
        this.name = name;
        this.emailAddress = emailAddress;
    }
}
