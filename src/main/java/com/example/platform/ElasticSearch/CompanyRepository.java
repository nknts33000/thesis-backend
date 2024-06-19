package com.example.platform.ElasticSearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends ElasticsearchRepository<CompanyES, String> {
    List<CompanyES> findByName(String name);
}
