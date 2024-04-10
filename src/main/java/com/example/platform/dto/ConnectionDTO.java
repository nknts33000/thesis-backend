package com.example.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConnectionDTO {
    private String token;
    private String receipient_email;
}
