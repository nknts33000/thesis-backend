package com.example.platform.ElasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.io.IOException;
import java.util.ArrayList;
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

//    public List<UserES> searchRecommendedUsers(List<String> skills, List<String> education, List<String> experiences, String location) throws IOException {
//        // Create a SearchRequest with a terms query to match skills
//        SearchRequest searchRequest = SearchRequest.of(s -> s
//                .index("userindex")  // Index name for UserES
//                .query(q -> q
//                        .terms(t -> t
//                                .field("skills.keyword")  // Use the field where skills are stored
//                                .terms(tq -> tq
//                                        .value(skills.stream()
//                                                .map(skill -> FieldValue.of(skill))
//                                                .toList())  // Convert each skill into FieldValue
//                                )
//                        )
//                )
//        );
//
//        // Execute search
//        SearchResponse<UserES> response = elasticsearchClient.search(searchRequest, UserES.class);
//
//        // Process hits and map them to UserES objects
//        List<UserES> users = new ArrayList<>();
//        for (Hit<UserES> hit : response.hits().hits()) {
//            users.add(hit.source());
//        }
//
//        return users;
//    }

    public List<UserES> searchRecommendedUsers(List<String> skills, List<String> education, List<String> experiences, String location) throws IOException {
        // Create a SearchRequest with a boolean query to boost relevance based on matches in multiple fields
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("userindex")  // Index name for UserES
                .query(q -> q
                        .bool(b -> b
                                .should(s1 -> s1
                                        .terms(t -> t
                                                .field("skills.keyword")  // Match on skills
                                                .terms(tq -> tq
                                                        .value(skills.stream()
                                                                .map(skill -> FieldValue.of(skill))
                                                                .toList())
                                                )
                                        )
                                )
                                .should(s2 -> s2
                                        .terms(t -> t
                                                .field("education.keyword")  // Match on education
                                                .terms(tq -> tq
                                                        .value(education.stream()
                                                                .map(edu -> FieldValue.of(edu))
                                                                .toList())
                                                )
                                        )
                                )
                                .should(s3 -> s3
                                        .terms(t -> t
                                                .field("experience.keyword")  // Match on experience
                                                .terms(tq -> tq
                                                        .value(experiences.stream()
                                                                .map(exp -> FieldValue.of(exp))
                                                                .toList())
                                                )
                                        )
                                )
                                .should(s4 -> s4
                                        .match(mt -> mt
                                                .field("location")  // Match on location
                                                .query(location)
                                        )
                                )
                                .minimumShouldMatch("1")  // At least one of the fields should match
                        )
                )
        );

        // Execute search
        SearchResponse<UserES> response = elasticsearchClient.search(searchRequest, UserES.class);

        // Process hits and map them to UserES objects
        List<UserES> users = new ArrayList<>();
        for (Hit<UserES> hit : response.hits().hits()) {
            users.add(hit.source());
        }

        return users;
    }


}
