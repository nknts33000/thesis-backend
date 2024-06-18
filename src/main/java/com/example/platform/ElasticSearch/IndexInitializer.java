package com.example.platform.ElasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;

@Component
public class IndexInitializer {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void initializeIndex() {
        try {
            // Check if index exists
            boolean exists = elasticsearchClient.indices()
                    .exists(ExistsRequest.of(e -> e.index("advertindex")))
                    .value();

            if (!exists) {
                // Load settings from JSON file
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> settings = objectMapper.readValue(
                        new ClassPathResource("elasticsearch-settings.json").getFile(),
                        Map.class
                );

                // Create index with settings
                CreateIndexRequest request = CreateIndexRequest.of(c -> c
                        .index("advertindex")
                        .settings((IndexSettings) settings.get("settings"))
                );

                elasticsearchClient.indices().create(request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
