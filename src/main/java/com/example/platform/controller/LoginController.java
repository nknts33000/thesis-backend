package com.example.platform.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/login")
    public ResponseEntity login(HttpServletResponse response){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "localhost:3000/");
        return new ResponseEntity(headers, HttpStatus.FOUND);
    }
}
