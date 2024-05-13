package com.example.platform.dto;

import com.example.platform.model.Post;
import com.example.platform.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentDTO {
    private String firstname;
    private String lastname;
    private String content;
    private String picture_url;
}
