package com.example.platform.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long experience_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String company_name;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private Date start_date;
    @Column(nullable = false)
    private Date end_date;
}
