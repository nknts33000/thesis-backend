package com.example.platform.controller;

import com.example.platform.exceptions.UserExistsException;
import com.example.platform.model.User;
import com.example.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    UserController(UserService userService){
       this.userService=userService;
    }
    // @RequestParam("email") String email,@RequestParam("password") String password,
    //                         @RequestParam("name") String name,
    //                         @RequestParam(value="location",required = false) String location
    //
    //                         @RequestParam("name") String name,@RequestParam(value="location",required = false) String location
    //
    @ResponseBody
    @PostMapping
    public void Register(@RequestBody User user)throws UserExistsException {
      userService.addUser(user);

    }


}
