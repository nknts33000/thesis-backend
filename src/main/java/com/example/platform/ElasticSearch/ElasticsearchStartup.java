package com.example.platform.ElasticSearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
@DependsOn("ESConfig")
public class ElasticsearchStartup {

//    @Autowired
//    private ElasticsearchOperations elasticsearchOperations;
//
//    @PostConstruct
//    public void createIndexIfNotExists() {
//        IndexCoordinates indexCoordinates = IndexCoordinates.of("advertindex");
//        IndexOperations indexOperations = elasticsearchOperations.indexOps(indexCoordinates);
//
//        if (!indexOperations.exists()) {
//            indexOperations.create();
//            indexOperations.putMapping(indexOperations.createMapping(AdvertES.class));
//            System.out.println("Index 'advertindex' created.");
//        } else {
//            System.out.println("Index 'advertindex' already exists.");
//        }
//    }
}
