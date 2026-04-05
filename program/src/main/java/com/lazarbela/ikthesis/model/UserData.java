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


    @OneToOne(mappedBy = "userData")
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
