package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String filename;
    private String filepath;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advert_id", referencedColumnName = "advertId")
    @JsonBackReference
    private Advert advert;

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
}