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
@Setting(settingPath = "/elasticsearch-settings.json")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdvertES {
    @Id
    private String id;
    @Field(type = FieldType.Text,name = "jobTitle",analyzer = "stem_analyzer")
    private String jobTitle;
    @Field(type = FieldType.Text,name = "jobSummary",analyzer = "stem_analyzer")
    private String jobSummary;
    @Field(type = FieldType.Text,name = "location")
    private String location;
    @Field(type = FieldType.Text,name = "company")
    private String company;

}
