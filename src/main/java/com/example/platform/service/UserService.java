package com.example.platform.service;

import com.example.platform.dto.LoginDTO;
import com.example.platform.dto.RegistrationDTO;
import com.example.platform.exceptions.InvalidCredentialsException;
import com.example.platform.exceptions.UserExistsException;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.User;
import com.example.platform.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepo,PasswordEncoder passwordEncoder){

        this.userRepo=userRepo;
        this.passwordEncoder=passwordEncoder;
    }



    public User findUser(Long id){
        return userRepo.findUserById(id);
    }

    public List<User> findAllUsers(){
        return userRepo.findAll();
    }

    public User login(LoginDTO loginDTO) throws UserNotFoundException, InvalidCredentialsException {
        User user= userRepo.findByEmail(loginDTO.getEmail()).orElseThrow(
                UserNotFoundException::new
        );

        if(user.getPassword().equals(passwordEncoder.encode(CharBuffer.wrap(loginDTO.getPassword())))){
            return user;
        }
        else {
            throw new InvalidCredentialsException("wrong password");
        }

    }

    public void addUser(RegistrationDTO registrationDTO) throws UserExistsException {
        boolean userExists=userRepo.findByEmail(registrationDTO.getEmail()).isPresent();
        if(!userExists){
            User user=new User(
                    registrationDTO.getEmail()
                    ,passwordEncoder.encode(CharBuffer.wrap(registrationDTO.getPassword()))//registrationDTO.getPassword()
                    ,registrationDTO.getFirstname()
                    ,registrationDTO.getLastname()
                    ,registrationDTO.getLocation()
            );
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
            userRepo.updateUser(user.getLocation(),user.getFirstname(), user.getPassword(), user.getEmail(),user.getLastname());
        }
        else{
            throw new UserNotFoundException();
        }
    }

    public void updateUserName(String email,User user) throws UserNotFoundException {
        if(userRepo.findByEmail(email).isPresent()){
            userRepo.updateName(user.getFirstname(),user.getEmail());
        }
        else{
            throw new UserNotFoundException();
        }
    }

    public void updateLastName(String email,User user) throws UserNotFoundException {
        if(userRepo.findByEmail(email).isPresent()){
            userRepo.updateLastName(user.getLastname(),user.getEmail());
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

    /*
    * gets the entity from the front end with its id and new email inside.
    * runs an update query based on the id and
    * */

    public void updateUserEmail(Long id,User user) throws UserNotFoundException {
        Optional<User> updated_user =userRepo.findById(id);
        if(updated_user.isPresent()){
            userRepo.updateEmail(user.getEmail(),id);
        }
        else{
            throw new UserNotFoundException();
        }
    }

    public void deleteUserByEmail(User user){
        userRepo.deleteByEmail(user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByEmail(email);
        //verify with password here
        return user//.map()
                .orElseThrow(() -> new UsernameNotFoundException("user not found " + email));

    }


   /////

}