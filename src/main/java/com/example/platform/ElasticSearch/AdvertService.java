package com.example.platform.ElasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvertService {

    @Autowired
    private AdvertRepository advertRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;


    // Save or update an AdvertES
    public AdvertES saveAdvert(AdvertES advert) {
        return advertRepository.save(advert);
    }

    // Find an AdvertES by ID
    public AdvertES findAdvertById(String id) {
        return advertRepository.findById(id).orElse(null);
    }

    // Find all AdvertES
    public Iterable<AdvertES> findAllAdverts() {
        return advertRepository.findAll();
    }

    // Custom search (example: by job title)
    public List<AdvertES> findByJobTitle(String jobTitle) {
        return advertRepository.findByJobTitle(jobTitle);
    }

    // Advanced search example
    public List<AdvertES> searchByJobSummary(String query) {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index("advertindex")
                    .query(q -> q
                            .match(m -> m
                                    .field("jobSummary")
                                    .query(query)
                            )
                    )
            );

            SearchResponse<AdvertES> response = elasticsearchClient.search(request, AdvertES.class);
            return response.hits().hits().stream()
                    .map(Hit::source).filter(advertes -> advertes != null)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

}
