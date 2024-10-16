package com.example.platform.ElasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.client.elc.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
                            .multiMatch(m -> m
                                    .fields("jobSummary","jobTitle","location","company")
                                    .query(query)
                            )
                    ).size(50)
            );

            SearchResponse<AdvertES> response = elasticsearchClient.search(request, AdvertES.class);
            return response.hits().hits().stream()
                    .map(Hit::source)//.filter(advertes -> advertes != null)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<AdvertES> searchAdvertsByProfile(List<String> skills, List<String> education, List<String> experiences, String location) {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index("advertindex")
                    .query(q -> q
                            .bool(b -> b
                                    // Match for skills in jobTitle and jobSummary
                                    .should(skills.stream()
                                            .map(skill -> QueryBuilders.multiMatch(m -> m
                                                    .fields("jobTitle", "jobSummary")
                                                    .query(skill)
                                            )).toList()
                                    )
                                    // Match for education in education-related fields
                                    .should(education.stream()
                                            .map(edu -> QueryBuilders.multiMatch(m -> m
                                                    .fields("jobTitle", "jobSummary")  // Adjust fields as necessary
                                                    .query(edu)
                                            )).toList()
                                    )
                                    // Match for experience in experience-related fields
                                    .should(experiences.stream()
                                            .map(exp -> QueryBuilders.multiMatch(m -> m
                                                    .fields("jobTitle", "jobSummary")  // Adjust fields as necessary
                                                    .query(exp)
                                            )).toList()
                                    )
                                    // Filter by location
                                    .must(m -> m
                                            .match(t -> t
                                                    .field("location")  // Adjust field name if necessary
                                                    .query(location)
                                            )
                                    )
                            )
                    )
                    .size(50)  // Limit to top 50 results
            );

            SearchResponse<AdvertES> response = elasticsearchClient.search(request, AdvertES.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

}
