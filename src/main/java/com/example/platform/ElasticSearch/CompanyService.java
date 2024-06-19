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
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public CompanyES saveCompany(CompanyES companyES) {
        return companyRepository.save(companyES);
    }

    // Advanced search example
    public List<CompanyES> search(String query) {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index("companyindex")
                    .query(q -> q
                            .match(m -> m
                                    .field("name")
                                    .query(query)
                            )
                    ).size(50)
            );

            SearchResponse<CompanyES> response = elasticsearchClient.search(request, CompanyES.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }


}
