package com.example.platform.dto;

import com.example.platform.model.User;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.AccessType;

@AllArgsConstructor
public class PostDTO {
    private UserDTO userDTO;
    private String content;
}
