package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long profile_id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id") //(foreign key referencing Users)
    @JsonBackReference
    private User user;

    private String picture_url;
    @Lob
    @Column(name = "profile_picture", nullable = true, columnDefinition = "BLOB")
    private byte[] profilePicture; // Store image as a byte array
    private String headline;
    private String summary;
    private String industry;

    public Profile() {

    }

    public Profile(User user){
        this.user=user;
    }

}
