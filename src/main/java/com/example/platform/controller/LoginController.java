package com.example.platform.controller;

import com.example.platform.model.request.LoginRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/login")
    public void Login(@RequestBody LoginRequest loginRequest){

    }
}
