package com.example.platform.dto;

import com.example.platform.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.AccessType;

@AllArgsConstructor
@Getter
public class PostDTO {
    private String content;
}
