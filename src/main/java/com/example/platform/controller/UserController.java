package com.example.platform.controller;

import com.example.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;
    @Autowired
    UserController(UserService userService){
        this.userService=userService;
    }

    /*@PostMapping("/insert")
    public */
}
