package com.example.platform.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@Getter
@Setter
public class LoginRequest {
    private String email;
    private String password;
}
