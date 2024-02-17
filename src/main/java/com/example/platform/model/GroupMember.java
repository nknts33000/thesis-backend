package com.example.platform.model;

import com.example.platform.UserService;
import jakarta.persistence.*;

@Entity
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long group_member_id;

    /*@ManyToMany
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;*/

}
