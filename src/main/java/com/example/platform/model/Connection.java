package com.example.platform.model;

import jakarta.persistence.*;

@Entity
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user1_id",referencedColumnName = "id")
    private User user1;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user2_id",referencedColumnName = "id")
    private User user2;

    @Column(nullable = false)
    private String connection_status;
}
