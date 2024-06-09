package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import java.util.Date;

@Entity
@Getter
@Setter
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long education_id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;
    //(ii) user_id (foreign key referencing Users)
    @Column(nullable = false)
    private String school_name;
    @Column(nullable = false)
    private String degree;
    @Column(nullable = false)
    private String field_of_study;
    @Column(nullable = false)
    private Date start_date;
    @Column(nullable = false)
    private Date end_date;

    public Education(){}

    public Education(long education_id,String school_name,String degree, String field_of_study,Date start_date,Date end_date,User user){
        this.education_id=education_id;
        this.school_name=school_name;
        this.degree=degree;
        this.field_of_study=field_of_study;
        this.start_date=start_date;
        this.end_date=end_date;
        this.user=user;
    }

    public Education(String school_name,String degree, String field_of_study,Date start_date,Date end_date,User user){
        this.school_name=school_name;
        this.degree=degree;
        this.field_of_study=field_of_study;
        this.start_date=start_date;
        this.end_date=end_date;
        this.user=user;
    }

}
