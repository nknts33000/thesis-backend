package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "likes")
@NoArgsConstructor

@Getter
@Setter
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long like_id;

    @JsonBackReference
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name="postId",referencedColumnName = "postId")
    private Post post;

    @JsonBackReference
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",referencedColumnName = "id")
    private User user;

    public Like(User user,Post post){
        this.user=user;
        this.post=post;
    }
}
