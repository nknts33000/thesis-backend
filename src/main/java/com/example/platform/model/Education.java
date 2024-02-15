package com.example.platform.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long education_id;

    public void setId(Long id) {
        this.education_id = id;
    }

    public Long getId() {
        return education_id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;
    //(ii) user_id (foreign key referencing Users)
    @Column(nullable = false)
    private String school_name;
    @Column(nullable = false)
    private String degree;
    @Column(nullable = false)
    private String field_of_study;
    @Column(nullable = false)
    private Date start_date;
    @Column(nullable = false)
    private Date end_date;


}
