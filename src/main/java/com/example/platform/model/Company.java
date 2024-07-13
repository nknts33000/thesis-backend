package com.example.platform.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.control.MappingControl;

import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long companyId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String mission;

    @Lob
    @Column(name = "company_logo", nullable = true, columnDefinition = "LONGBLOB")
    private byte[] companyLogo; // Store image as a byte array

    @JsonManagedReference
    @OneToMany(mappedBy = "company")
    private List<Post> posts;

    @JsonIgnore
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",referencedColumnName = "id")
    private User creator;

    @JsonIgnore
    @ManyToMany(mappedBy = "adminOf")
    private Set<User> admins;

    @JsonBackReference
    @ManyToMany
    @JoinTable(
            name = "followings",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> followers;

    @JsonManagedReference
    @OneToMany(mappedBy="company")
    private Set<Advert> adverts;

    public Company() {
    }

    public Company(
            String name,
            String mission,
            User creator
    ){
        this.name=name;
        this.mission=mission;
        this.creator=creator;
    }

}