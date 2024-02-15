package com.example.platform.model;

import jakarta.persistence.*;
import org.springframework.data.repository.cdi.Eager;

@Entity
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long skill_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;
    @Column(nullable = false)
    private String skill_name;
}
