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
public class UserSearchingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    // Save or update an AdvertES
    public UserES saveUser(UserES user) {
        return userRepository.save(user);
    }

    // Find an AdvertES by ID
    public UserES findUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    // Find all AdvertES
    public Iterable<UserES> findAllUsers() {
        return userRepository.findAll();
    }

    // Custom search (example: by job title)
    public List<UserES> findByFirstname(String firstname) {
        return userRepository.findByFirstname(firstname);
    }

    public List<UserES> findByLastname(String lastname) {
        return userRepository.findByLastname(lastname);
    }

    // Advanced search example
    public List<UserES> search(String query) {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index("userindex")
                    .query(q -> q
                            .multiMatch(m -> m
                                    .fields("firstname","lastname")
                                    .query(query)
                            )
                    ).size(50)
            );

            SearchResponse<UserES> response = elasticsearchClient.search(request, UserES.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }


}
