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

    // Nested object for education
    @Field(type = FieldType.Nested, name = "education")
    private List<EducationES> education;

    // Nested object for experience
    @Field(type = FieldType.Nested, name = "experience")
    private List<ExperienceES> experience;

    // Getters and setters...

    // Inner class for Education
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class EducationES {
        @Field(type = FieldType.Text, name = "schoolName")
        private String schoolName;

        @Field(type = FieldType.Text, name = "fieldOfStudy")
        private String fieldOfStudy;

        // Getters and setters...
    }

    // Inner class for Experience
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ExperienceES {
        @Field(type = FieldType.Text, name = "companyName")
        private String companyName;

        @Field(type = FieldType.Text, name = "jobTitle")
        private String jobTitle;

        // Getters and setters...
    }


}
