package com.example.platform.controller;

import com.example.platform.ElasticSearch.UserES;
import com.example.platform.ElasticSearch.UserSearchingService;
import com.example.platform.dto.LoginDTO;
import com.example.platform.dto.RegistrationDTO;
import com.example.platform.dto.UserDTO;
import com.example.platform.exceptions.InvalidCredentialsException;
import com.example.platform.exceptions.UserExistsException;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.User;
import com.example.platform.security.config.UserAuthenticationProvider;
import com.example.platform.service.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

@RestController
@CrossOrigin
public class AuthController {

    private final UserService userService;

    private final UserSearchingService userSearchingService;



    @Autowired
    AuthController(UserService userService,UserSearchingService userSearchingService){

        this.userService=userService;
        this.userSearchingService=userSearchingService;
    }

    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginDTO loginDTO) throws UserNotFoundException, InvalidCredentialsException {
        User user=userService.login(loginDTO);
        UserAuthenticationProvider userAuthenticationProvider=new UserAuthenticationProvider();
        UserDTO userDTO=new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                userAuthenticationProvider.createToken(user.getEmail())
        );

        return new ResponseEntity<>(userDTO,HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegistrationDTO registrationDTO) throws UserExistsException, UserNotFoundException {
        User user=userService.addUser(registrationDTO);
        userSearchingService.saveUser(
                new UserES(
                    Long.toString(user.getId()),
                    user.getFirstname(), user.getLastname(),new ArrayList<>()
                )
        );
        UserAuthenticationProvider userAuthenticationProvider = new UserAuthenticationProvider();
        return ResponseEntity.created(URI.create("/user/" + user.getId())).body(new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                userAuthenticationProvider.createToken(user.getEmail())
        ));
    }

}
