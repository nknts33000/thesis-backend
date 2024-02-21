package com.example.platform.service;

import com.example.platform.exceptions.UserExistsException;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.User;
import com.example.platform.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserRepo userRepo;

    public void addUser(User user) throws UserExistsException {
        boolean userExists=userRepo.findByEmail(user.getEmail()).isPresent();
        if(!userExists){
            userRepo.save(user);
        }
        else{
            throw new UserExistsException();
        }
    }

    public User getUser(String email) throws UserNotFoundException {
        return userRepo.findByEmail(email).orElseThrow(UserNotFoundException::new);

    }

    public void updateUser(String email,User user){
        userRepo.save(user);
    }
}

