package com.example.platform.controller;

import com.example.platform.dto.LoginDTO;
import com.example.platform.dto.RegistrationDTO;
import com.example.platform.dto.UserDTO;
import com.example.platform.exceptions.InvalidCredentialsException;
import com.example.platform.exceptions.UserExistsException;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.User;
import com.example.platform.security.config.UserAuthenticationProvider;
import com.example.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final UserService userService;


    @Autowired
    AuthController(UserService userService){
        this.userService=userService;
    }

    @ResponseBody
    @PostMapping("/login")
    public UserDTO login(@RequestBody LoginDTO loginDTO) throws UserExistsException, UserNotFoundException, InvalidCredentialsException {
        User user=userService.login(loginDTO);
        UserAuthenticationProvider userAuthenticationProvider=new UserAuthenticationProvider();
        UserDTO userDTO=new UserDTO(
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                userAuthenticationProvider.createToken(user.getEmail())
        );

        return userDTO;
    }

    @ResponseBody
    @PostMapping("/register")
    public void register(@RequestBody RegistrationDTO registrationDTO) throws UserExistsException {

        userService.addUser(registrationDTO);
    }
}
