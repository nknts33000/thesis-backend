package com.example.platform.security.config;

import com.example.platform.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails {
    private String email;
    private String password;
    private List<GrantedAuthority> authorities;

    public CustomUserDetails(User user){
        this.email=user.getEmail();
        this.password= user.getPassword();
        this.authorities= Arrays.stream(user.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
