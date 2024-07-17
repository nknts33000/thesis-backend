package com.example.platform.model;

import com.example.platform.repo.ShareRepo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long share_id;

    @Column(length=5000)
    public String description;

    @Column(nullable = false)
    private LocalDateTime share_date= LocalDateTime.now();

    @JsonBackReference
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId",referencedColumnName = "postId")
    private Post post;

    @JsonManagedReference
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @JsonManagedReference
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id",referencedColumnName = "companyId")
    private Company company;

    public Share(User user,Post post,String description){
        this.user=user;
        this.post=post;
        this.description=description;
    }

    public Share(Company company,Post post,String description){
        this.company=company;
        this.post=post;
        this.description=description;
    }

}
