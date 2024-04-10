package com.example.platform.security.config;

import org.hibernate.annotations.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class SecretKeyConfig {
    private String secretKey="${security.jwt.token.secret-key:secret-value}";

    public String SecretValue() {
        // Encoding the secret key using Base64
        //secretKey =
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

}
