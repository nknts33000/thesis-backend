package com.example.platform.ElasticSearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertRepository extends ElasticsearchRepository<AdvertES, String> {
    List<AdvertES> findByJobTitle(String jobTitle);
    List<AdvertES> findByLocation(String location);
}
