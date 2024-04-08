package com.example.platform.model;

import jakarta.persistence.*;
import org.mapstruct.control.MappingControl;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long post_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime post_date= LocalDateTime.now();

    @OneToMany(mappedBy = "post")
    private List<Like> likes;

    @OneToMany(mappedBy = "post")
    private List<Share> shares;

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;

    public Post(String content, User user){
        this.content=content;
        this.user=user;
    }

    public Post() {

    }
}
