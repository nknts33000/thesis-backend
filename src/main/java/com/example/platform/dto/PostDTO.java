package com.example.platform.dto;

import com.example.platform.model.Post;
import com.example.platform.model.Profile;
import com.example.platform.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PostDTO {
    private Post post;

    //to be replaced by user after the Post component is made in the front end.
    private String firstname;
    private String lastname;
    private String picture_url;
    //

    private List<CommentDTO> comments;//to be replaced by comment
}
