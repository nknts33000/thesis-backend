package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long skill_id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "profile_id",referencedColumnName = "profile_id")
    private Profile profile;
    @Column(nullable = false)
    private String skill_name;

    public Skill(){}
    public Skill(String skill_name,Profile profile){
        this.skill_name=skill_name;
        this.profile=profile;
    }
}