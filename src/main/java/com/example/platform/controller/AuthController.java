package com.example.platform.controller;

import com.example.platform.exceptions.UserExistsException;
import com.example.platform.model.User;
import com.example.platform.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class AuthController {

    private final UserService userService;

    @Autowired
    AuthController(UserService userService){
        this.userService=userService;
    }

    @ResponseBody
    @PostMapping("/login")
    public void login(@RequestBody User user) throws UserExistsException {
        userService.addUser(user);
    }

    @ResponseBody
    @PostMapping("/register")
    public void register(@RequestBody User user) throws UserExistsException {
        userService.addUser(user);
    }
}
