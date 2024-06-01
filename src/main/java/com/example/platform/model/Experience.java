package com.example.platform.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long experience_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String company_name;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private Date start_date;
    @Column(nullable = false)
    private Date end_date;

    public Experience(){}

    public Experience(String company_name,String title,String location,
                      Date start_date,Date end_date,User user){
        this.company_name=company_name;
        this.title=title;
        this.location=location;
        this.start_date=start_date;
        this.end_date=end_date;
        this.user=user;
    }
}
