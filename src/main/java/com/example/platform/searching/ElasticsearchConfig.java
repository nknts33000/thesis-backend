package com.example.platform.searching;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClients;

public class ElasticsearchConfig {
    public RestHighLevelClient getClient() {
        return RestClients.create().rest();
    }
}
