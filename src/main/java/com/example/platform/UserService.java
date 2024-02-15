package com.example.platform;

import com.example.platform.model.User;
import com.example.platform.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {
    private UserRepo userRepo;

    public void insertUser(User user){
        userRepo.save(user);
    }

    public List<User> findAllUsers(){
        return userRepo.findAll();
    }

    public User findUserByUsername(String username){
       return userRepo.findUserByUsername(username);
    }

    public void updateUser(User user){
        userRepo.save(user);
    }

    public void deleteUserById(long id){
        userRepo.deleteById(id);
    }
}
