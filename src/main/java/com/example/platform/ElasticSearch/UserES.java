package com.example.platform.ElasticSearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

@Document(indexName = "userindex")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserES {

    @Id
    private String id;
    @Field(type = FieldType.Text,name = "firstname")
    String firstname;
    @Field(type = FieldType.Text,name = "lastname")
    String lastname;

    @Field(type = FieldType.Keyword, name = "skills")
    private List<String> skills;  // Add this field to store skills

    @Field(type = FieldType.Text, name = "location")
    private String location;  // Added location field

    @Field(type = FieldType.Keyword, name = "education")
    private List<String> education;

    @Field(type = FieldType.Keyword, name = "experience")
    private List<String> experience;


    public UserES(String id, String firstname, String lastname, ArrayList<String> skills, ArrayList<String> education, ArrayList<String> experience) {
        this.id=id;
        this.firstname=firstname;
        this.lastname=lastname;
        this.skills=skills;
        this.education=education;
        this.experience=experience;
    }
}
