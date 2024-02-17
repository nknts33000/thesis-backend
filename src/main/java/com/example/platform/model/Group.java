package com.example.platform.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long group_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String group_name;
    private String description;
    @Column(nullable = false)
    private Date created_date;

}
