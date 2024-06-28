package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.example.platform.model.User;


@Entity
@Getter
@Setter
@Table(name = "connections")
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id",referencedColumnName = "id")
    private User user1;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id",referencedColumnName = "id")
    private User user2;

    @Column(nullable = false)
    private String connection_status;

    public Connection(){}

    public Connection(User user1,User user2,String connection_status){
        this.user1=user1;
        this.user2=user2;
        this.connection_status=connection_status;
    }
}
