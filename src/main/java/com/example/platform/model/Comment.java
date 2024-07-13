package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.CompletionException;

@Entity
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long comment_id;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId",referencedColumnName = "postId")
    @JsonBackReference
    private Post post;// (foreign key referencing Posts)

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @JsonBackReference
    private User user;// (foreign key referencing Users)

    @Column(nullable = false)
    private String content;

    private LocalDateTime comment_date;

    public Comment(){}

    public Comment(Post post,User user,String content){
        this.post=post;
        this.user=user;
        this.content=content;
        comment_date= LocalDateTime.now();
    }

}
