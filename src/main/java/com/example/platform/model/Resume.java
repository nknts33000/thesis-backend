// Resume.java

package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String filename;
    private String filepath;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "advert_id", referencedColumnName = "advertId")
    @JsonBackReference
    private Advert advert;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonManagedReference
    private User user;  // New field for user

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Advert getJobAdvertisement() {
        return advert;
    }

    public void setJobAdvertisement(Advert advert) {
        this.advert = advert;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
