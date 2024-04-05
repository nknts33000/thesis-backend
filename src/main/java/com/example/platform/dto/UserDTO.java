package com.example.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDTO {
    private long id;
    private String email;
    private String firstname;
    private String lastname;
    private String token;

}
