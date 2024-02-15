package com.example.platform.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long post_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Date post_date;

    @OneToMany(mappedBy = "post")
    private List<Like> likes;
}
