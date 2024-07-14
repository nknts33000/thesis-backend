package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import org.mapstruct.control.MappingControl;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Getter
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId") // changed from post_id to postId so i could the method in postRepo would recognize the field
    private long postId;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",referencedColumnName = "id")
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="companyId",referencedColumnName = "companyId")
    @JsonBackReference
    private Company company;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime post_date= LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "post")
    private List<Like> likes;

    @JsonIgnore
    @OneToMany(mappedBy = "post")
    private List<Share> shares;

    @JsonManagedReference
    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;

    public Post(String content, User user){
        this.content=content;
        this.user=user;
    }

    public Post(String content,Company company){
        this.content=content;
        this.user=user;
        this.company=company;
    }

    public Post() {

    }
}
