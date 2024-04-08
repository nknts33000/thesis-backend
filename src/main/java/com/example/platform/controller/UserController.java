package com.example.platform.controller;

import com.example.platform.dto.PostDTO;
import com.example.platform.dto.RegistrationDTO;
import com.example.platform.exceptions.UserExistsException;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.User;
import com.example.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.platform.dto.UserDTO;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    private final UserService userService;

    @Autowired
    UserController(UserService userService){
       this.userService=userService;
    }

    @ResponseBody
    @PutMapping("/update")
    public void Update(@RequestBody RegistrationDTO registrationDTO) throws UserNotFoundException {

        userService.update(registrationDTO.getEmail(),registrationDTO);
    }

    @ResponseBody
    @PutMapping("/update/firstname")
    public void UpdateName(@RequestBody User user) throws UserNotFoundException {
        userService.updateUserName(user.getEmail(),user);
    }

    @ResponseBody
    @PutMapping("/update/lastname")
    public void UpdateLastName(@RequestBody User user) throws UserNotFoundException {
        userService.updateLastName(user.getEmail(),user);
    }

    @ResponseBody
    @PutMapping("/update/location")
    public void UpdateLocation(@RequestBody User user) throws UserNotFoundException {
        userService.updateUserLocation(user.getEmail(),user);
    }

    @ResponseBody
    @PutMapping("/update/password")
    public void UpdatePassword(@RequestBody User user) throws UserNotFoundException {
        userService.updateUserPassword(user.getEmail(),user);
    }

    @ResponseBody
    @PutMapping("/update/email")
    public void UpdateEmail(@RequestBody User user) throws UserNotFoundException {
        userService.updateUserEmail(user.getId(),user);
    }

    @ResponseBody
    @DeleteMapping("/delete")
    public void deleteUser(@RequestBody UserDTO userdto){

        userService.deleteUserByEmail(userdto);
    }

    @ResponseBody
    @PostMapping("/post")
    public void upload_post(@RequestBody PostDTO postDTO,@RequestBody UserDTO userDTO) throws UserNotFoundException {
        userService.addPost(postDTO,userDTO);
    }
}
