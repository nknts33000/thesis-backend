package com.example.platform.model;

import com.example.platform.repo.ProfileRepo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements Serializable, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false,unique = true)
    private String email;
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;
    private String location;
    private String roles;
    @JsonIgnore
    private final LocalDateTime join_date= LocalDateTime.now();

    public User() {

    }

    public User(String email,String password,String firstname,String lastname,String location){
        this.email=email;
        this.password= password;
        this.lastname=lastname;
        this.firstname=firstname;
        this.location=location;


    }

    public User(String email,String password,String firstname,String lastname){

        this.email=email;
        this.password= password;
        this.firstname=firstname;
        this.lastname=lastname;

    }

    @JsonManagedReference
    @OneToOne(mappedBy = "user")
    private Profile profile;

    @JsonIgnore
    @OneToMany(mappedBy = "creator")
    private List<Company> companies;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADMINS_OF_COMPANY_PAGES",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "companyId"))
    private Set<Company> adminOf; //admin of specific company pages

    @JsonIgnore
    @ManyToMany(mappedBy = "applicants")
    private List<Advert> applications;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @JsonManagedReference
    @OneToMany(mappedBy = "user1")
    private List<Connection> connectionList=new ArrayList<>();///when the user initiated the connection(1st user column in connection schema)

    @JsonManagedReference
    @OneToMany(mappedBy = "user2")
    private List<Connection> connectionOf=new ArrayList<>();///when the user accepted the connection(2nd user column in connection schema)

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Education> education;

    @JsonManagedReference
    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<Experience> experiences;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Skill> skills;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Like> likes;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Share> shares;

    @JsonIgnore
    @OneToMany(mappedBy = "owner")
    private List<Group> groupsOwned;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<Comment> comments;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(
            name = "GROUP_MEMBERS",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;

//        return Arrays.stream(this.roles.split(","))
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;//email is used as a unique username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
