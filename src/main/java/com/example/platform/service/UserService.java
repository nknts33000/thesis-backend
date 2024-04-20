package com.example.platform.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.platform.dto.*;
import com.example.platform.exceptions.CustomException;
import com.example.platform.exceptions.InvalidCredentialsException;
import com.example.platform.exceptions.UserExistsException;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.*;
import com.example.platform.repo.*;
import com.example.platform.security.config.SecretKeyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    //private String secretKey="${security.jwt.token.secret-key:secret-value}";
    private final UserRepo userRepo;
    private final PostRepo postRepo;
    private final SecretKeyConfig secretKeyConfig;
    private final ProfileRepo profileRepo;
    private final CommentRepo commentRepo;
    private final ConnectionRepo connectionRepo;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepo, PostRepo postRepo, SecretKeyConfig secretKeyConfig, ProfileRepo profileRepo, ConnectionRepo connectionRepo, PasswordEncoder passwordEncoder,CommentRepo commentRepo){
        this.secretKeyConfig = secretKeyConfig;
        this.commentRepo=commentRepo;
        this.profileRepo = profileRepo;
        this.connectionRepo = connectionRepo;
        System.out.println("in service: "+secretKeyConfig.SecretValue());
        this.userRepo=userRepo;
        this.postRepo=postRepo;
        this.passwordEncoder=passwordEncoder;
    }


    public User findUserById(Long id){
        return userRepo.findUserById(id);
    }

    public User findUserByEmail(String email) throws UserNotFoundException {
        User user= userRepo.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return user;
    }

    public List<User> findAllUsers(){
        return userRepo.findAll();
    }

    public User login(LoginDTO loginDTO) throws UserNotFoundException, InvalidCredentialsException {
        User user= userRepo.findByEmail(loginDTO.getEmail()).orElseThrow(
                UserNotFoundException::new
        );

        if(passwordEncoder.matches(loginDTO.getPassword(),user.getPassword())){
            return user;
        }
        else {
            throw new InvalidCredentialsException("wrong password");
        }

    }

    public void addUser(RegistrationDTO registrationDTO) throws UserExistsException, UserNotFoundException {

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
            createProfile(user);
        }
        else{
            throw new UserExistsException();
        }
    }

    public User getUser(String email) throws UserNotFoundException {
        return userRepo.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public void update(String email,RegistrationDTO registrationDTO) throws UserNotFoundException {
        if(userRepo.findByEmail(email).isPresent()){
            userRepo.updateUser(registrationDTO.getLocation(),registrationDTO.getFirstname()
                    , registrationDTO.getPassword(), registrationDTO.getEmail()
                    ,registrationDTO.getLastname());
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

    public void deleteUserByEmail(UserDTO userdto) throws UserNotFoundException {

        userRepo.deleteByEmail(userdto.getEmail());
        User user=getUser(userdto.getEmail());
        delete_profile(user);
    }

    public void upload_post(String content,User user){
        postRepo.save(new Post(content,user));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByEmail(email);
        //verify with password here
        return user//.map()
                .orElseThrow(() -> new UsernameNotFoundException("user not found " + email));

    }

    public void addPost(String token,String content) throws UserNotFoundException {

        User user=getUserFromToken(token);
        postRepo.save(
                new Post(content,user)
        );
    }

    public List<Post> getPostsOfFriends(String token) throws UserNotFoundException {
        User user= getUserFromToken(token);
        List<Long> friendIds = new ArrayList<>();
        friendIds.addAll(userRepo.findFriendsIdsByInitiatorId(user.getId()));
        friendIds.addAll(userRepo.findFriendsIdsByAcceptorId(user.getId()));

        return userRepo.findPostsOfFriends(friendIds);
    }


    public User getUserFromToken(String token) throws UserNotFoundException {
        String secondKey=secretKeyConfig.SecretValue();

        JWTVerifier verifier= JWT.require(Algorithm.HMAC256(secondKey)).build();

        DecodedJWT decodedJWT= verifier.verify(token);

        User user = getUser(decodedJWT.getSubject());

        return user;
    }

    public void createProfile(User user) throws UserNotFoundException {
        //User user=getUserFromToken(profileDTO.getToken());
        Optional<Profile> profile=profileRepo.findByUser(user);

        if(!profile.isPresent()){
          profileRepo.save(
                  new Profile(
                        user
                    )
            );
        }
    }

    public void update_profile(ProfileDTO profileDTO) throws UserNotFoundException, CustomException {
        User user=getUserFromToken(profileDTO.getToken());
        Profile profile=profileRepo.findByUser(user).orElseThrow(() -> new CustomException("profile doesn't exist"));
        profile.setHeadline(profileDTO.getHeadline());
        profile.setIndustry(profileDTO.getIndustry());
        profile.setPicture_url(profileDTO.getPicture_url());
        profile.setSummary(profileDTO.getSummary());
        profileRepo.save(profile);
    }

    public void delete_profile(User user){
        profileRepo.deleteProfileByUser(user);
    }

    public boolean connectionExists(User user1, User user2){
        List<Connection> connection1= connectionRepo.getConnetions(user1,user2);
        List<Connection> connection2= connectionRepo.getConnetions(user2,user1);
        if(connection1.isEmpty() && connection2.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean requestPending(User user1, User user2){
        List<Connection> connection1= connectionRepo.getConnetions(user1,user2);
        List<Connection> connection2= connectionRepo.getConnetions(user2,user1);
        if(!connection1.isEmpty()){
            Connection connection= connection1.getFirst();
            if(connection.getConnection_status().equals("Pending")){
                return true;
            }
            else{
                return false;
            }
        } else if (!connection2.isEmpty()) {
            Connection connection= connection2.getFirst();
            if(connection.getConnection_status().equals("Pending")){
                return true;
            }
            else{
                return false;
            }
        }
        else {
            return false;
        }
    }

    public void newFriendRequest(ConnectionDTO connectionDTO) throws UserNotFoundException, CustomException {

        User user1=getUserFromToken(connectionDTO.getToken());
        User user2=getUser(connectionDTO.getReceipient_email());
        boolean connectionExists=connectionExists(user1,user2);
        if(!connectionExists){
            Connection connection=new Connection(user1,user2,"Pending");
            connectionRepo.save(connection);
        }
        else{
            throw new CustomException("Connection exists");
        }
    }

    public void acceptFriendRequest(ConnectionDTO connectionDTO) throws UserNotFoundException {
        User user1=getUserFromToken(connectionDTO.getToken());
        User user2=getUser(connectionDTO.getReceipient_email());
        boolean isPending=requestPending(user1,user2);
        if(isPending){
            connectionRepo.acceptRequest(user1,user2);
        }
    }

    public void rejectRequest(ConnectionDTO connectionDTO) throws UserNotFoundException {
        User user1=getUserFromToken(connectionDTO.getToken());
        User user2=getUser(connectionDTO.getReceipient_email());
        boolean isPending=requestPending(user1,user2);
        if(isPending){
            connectionRepo.rejectRequest(user1,user2);
        }
    }

    public void deleteFriend(ConnectionDTO connectionDTO) throws UserNotFoundException {
        User user1=getUserFromToken(connectionDTO.getToken());
        User user2=getUser(connectionDTO.getReceipient_email());
        connectionRepo.deleteFriend(user1,user2);
    }

    public UserDTO getUserByPost(Post post) {
        List<User> result= postRepo.getUserByPostId(post.getPost_id());
        User user=result.getFirst();
        return new UserDTO(user.getId(), user.getEmail(), user.getFirstname(), user.getLastname(), null);
    }

    public Post getPostById(long post_id){
        Post post=postRepo.getPostById(post_id);
        return post;
    }

    public void addCommentToPost(String token, long post_id, String content) throws UserNotFoundException {
        User user=getUserFromToken(token);
        Post post= getPostById(post_id);
        commentRepo.save(new Comment(post,user,content));
    }

    public List<Comment> getComments(Post post){
        return commentRepo.getCommentFromPost(post);
    }
}