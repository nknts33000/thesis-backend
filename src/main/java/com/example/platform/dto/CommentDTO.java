package com.example.platform.dto;

import com.example.platform.model.Post;
import com.example.platform.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentDTO {
    private String token;
    private String content;
    private long post_id;
}
