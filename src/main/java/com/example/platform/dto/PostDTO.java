package com.example.platform.dto;

import com.example.platform.model.Post;
import com.example.platform.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PostDTO {
    private Post post;
    private UserDTO userdto;
    private Profile profile;
}
