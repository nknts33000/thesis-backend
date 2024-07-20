package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "all_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long group_id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User owner;

    @ManyToMany(mappedBy = "groups")
    private Set<User> users;
    @Column(nullable = false)
    private String group_name;
    private String description;
    @Column(nullable = false)
    private Date created_date;

}
