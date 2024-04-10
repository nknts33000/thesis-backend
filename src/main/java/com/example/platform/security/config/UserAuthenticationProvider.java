package com.example.platform.security.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.User;
import com.example.platform.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
//import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;


import java.util.Base64;
import java.util.Collections;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class UserAuthenticationProvider {

    //@Value("${security.jwt.token.secret-key:secret-value}")// see https://stackoverflow.com/questions/66608801/this-annotation-is-not-applicable-to-target-local-variable
    private String secretKey="${security.jwt.token.secret-key:secret-value}";

    //private SecretKeyConfig secretKeyConfig;
    private String secondKey;

    private UserService userService;

    @Autowired
    public UserAuthenticationProvider(UserService userService){//,SecretKeyConfig secretKeyConfig){
        this.userService=userService;
        //this.secretKeyConfig=secretKeyConfig;
    }

    //@PostConstruct
    public String SecretValue() {
        // Encoding the secret key using Base64
        //secretKey =
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String email){
        Date now=new Date();
        Date validity=new Date(now.getTime()+3_600_000);
        secondKey=SecretValue();

        return JWT.create()
                .withIssuer(email)
                .withSubject(email)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(Algorithm.HMAC256(secondKey));
    }



    public User getUserFromToken(String token) throws UserNotFoundException {
        secondKey=SecretValue();
        //System.out.println("in validate token: "+secondKey);
        JWTVerifier verifier=JWT.require(Algorithm.HMAC256(secondKey)).build();

        DecodedJWT decodedJWT= verifier.verify(token);
        System.out.println("email:"+decodedJWT.getSubject());
        User user = userService.getUser(decodedJWT.getSubject());

        return user;
    }

    public UsernamePasswordAuthenticationToken validateToken(String token) throws UserNotFoundException {
//        secondKey=SecretValue();
//
//        JWTVerifier verifier=JWT.require(Algorithm.HMAC256(secondKey)).build();
//
//        DecodedJWT decodedJWT= verifier.verify(token);
//        System.out.println("email:"+decodedJWT.getSubject());
        User user = getUserFromToken(token);//userService.getUser(decodedJWT.getSubject());

        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }


}