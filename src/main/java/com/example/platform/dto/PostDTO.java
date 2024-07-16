package com.example.platform.dto;

import com.example.platform.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;


@Getter
@Setter
public class PostDTO {
    private Post post;

    private User user;

    private Company company;

    private List<CommentDTO> comments;//to be replaced by comment

    //private ShareEntity shareEntity;

    private Share share;

    private LocalDateTime timestamp;

    public PostDTO(Post post,User user,Company company,List<CommentDTO> comments,Share share,LocalDateTime timestamp){

    }

}
