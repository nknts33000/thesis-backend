package com.example.platform.dto;

import com.example.platform.model.Company;
import com.example.platform.model.Post;
import com.example.platform.model.Profile;
import com.example.platform.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@AllArgsConstructor
@Getter
@Setter
public class PostDTO {
    private Post post;

    private User user;

    private Company company;

    private List<CommentDTO> comments;//to be replaced by comment

    private ShareEntity shareEntity;

    private LocalDateTime timestamp;

}
