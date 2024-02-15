package com.example.platform.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long profile_id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id") //(foreign key referencing Users)
    private User user;
    private String headline;
    private String summary;
    private String industry;
    private String website;
}
