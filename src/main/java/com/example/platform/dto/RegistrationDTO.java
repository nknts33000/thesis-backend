package com.example.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public class RegistrationDTO {

    private String email;

    private String password;

    private String firstname;

    private String lastname;
    private String location;

}
