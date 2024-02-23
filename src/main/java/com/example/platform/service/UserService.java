package com.example.platform.service;

import com.example.platform.exceptions.UserExistsException;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.User;
import com.example.platform.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo){
        this.userRepo=userRepo;
    }

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

    public void update(String email,User user) throws UserNotFoundException {
        if(userRepo.findByEmail(email).isPresent()){
            userRepo.updateUser(user.getLocation(),user.getName(), user.getPassword(), user.getEmail());
        }
        else{
            throw new UserNotFoundException();
        }
    }

    public void updateUserName(String email,User user) throws UserNotFoundException {
        if(userRepo.findByEmail(email).isPresent()){
            userRepo.updateName(user.getName(),user.getEmail());
        }
        else{
            throw new UserNotFoundException();
        }
    }

    public void updateUserLocation(String email,User user) throws UserNotFoundException {
        if(userRepo.findByEmail(email).isPresent()){
            userRepo.updateLocation(user.getLocation(),user.getEmail());
        }
        else{
            throw new UserNotFoundException();
        }
    }

    public void updateUserPassword(String email,User user) throws UserNotFoundException {
        if(userRepo.findByEmail(email).isPresent()){
            userRepo.updatePassword(user.getPassword(),user.getEmail());
        }
        else{
            throw new UserNotFoundException();
        }
    }

}