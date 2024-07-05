package com.example.platform.ElasticSearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "advertindex")
@Getter
@Setter
@Setting(settingPath = "elasticsearch-settings.json") // specify the path to your settings file
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdvertES {
    @Id
    private String id;
    @Field(type = FieldType.Text, name = "jobTitle", analyzer = "custom_analyzer")
    private String jobTitle;
    @Field(type = FieldType.Text, name = "jobSummary", analyzer = "custom_analyzer")
    private String jobSummary;
    @Field(type = FieldType.Text,name = "location")
    private String location;
    @Field(type = FieldType.Text,name = "company")
    private String company;

}
