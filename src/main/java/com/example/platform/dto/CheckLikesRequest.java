package com.example.platform.dto;

import java.util.List;

public class CheckLikesRequest {

    private List<Long> postIds;

    public List<Long> getPostIds() {
        return postIds;
    }

    public void setPostIds(List<Long> postIds) {
        this.postIds = postIds;
    }
}
