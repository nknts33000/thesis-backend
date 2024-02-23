package com.example.platform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    private String location;
    private final LocalDateTime join_date= LocalDateTime.now();


    public User() {
        System.out.println("hello");
    }

    public User(String email,String password,String name,String location){
        this.email=email;
        this.password= password;
        this.name=name;
        this.location=location;
        //this.join_date= LocalDateTime.now();
    }

    public User(String email,String password,String name){
        System.out.println("hello2");
        this.email=email;
        this.password= password;
        this.name=name;
        //this.join_date= LocalDateTime.now();

    }

    @OneToOne(mappedBy = "user")
    private Profile profile;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @OneToMany(mappedBy = "user1")
    private List<Connection> connectionList=new ArrayList<>();///when the user initiated the connection(1st user column in connection schema)

    @OneToMany(mappedBy = "user2")
    private List<Connection> connectionOf=new ArrayList<>();///when the user accepted the connection(2nd user column in connection schema)

    @OneToMany(mappedBy = "user")
    private List<Education> education;

    @OneToMany(mappedBy = "user")
    private List<Experience> experiences;

    @OneToMany(mappedBy = "user")
    private List<Skill> skills;


    @OneToMany(mappedBy = "user")
    private List<Like> likes;

    @OneToMany(mappedBy = "user")
    private List<Share> shares;

    @OneToMany(mappedBy = "owner")
    private List<Group> groupsOwned;

    @OneToMany(mappedBy = "user")
    private Set<Comment> comments;

    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(
            name = "GROUP_MEMBERS",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<User> users;


}
