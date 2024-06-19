package com.example.platform.ElasticSearch;

import com.example.platform.model.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends ElasticsearchRepository<UserES, String> {
    List<UserES> findByFirstname(String firstname);
    List<UserES> findByLastname(String lastname);
}
