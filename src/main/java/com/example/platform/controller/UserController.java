package com.example.platform.controller;

import com.example.platform.dto.*;
import com.example.platform.exceptions.CustomException;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.Post;
import com.example.platform.model.User;
import com.example.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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
    public void deleteUser(@RequestBody UserDTO userdto) throws UserNotFoundException {

        userService.deleteUserByEmail(userdto);
    }

    @ResponseBody
    @PostMapping("/post")
    public void upload_post(@RequestBody PostDTO postDTO) throws UserNotFoundException {
        userService.addPost(postDTO);
    }

    @ResponseBody
    @PutMapping("/profile/update")
    public void profile_update(@RequestBody ProfileDTO profileDTO) throws UserNotFoundException, CustomException {
        userService.update_profile(profileDTO);
    }

    @ResponseBody
    @PostMapping("/add_friend")
    public void addFriend(@RequestBody ConnectionDTO connectionDTO) throws UserNotFoundException, CustomException {
        userService.newFriendRequest(connectionDTO);
    }

    @ResponseBody
    @PutMapping("/accept_friend")
    public void acceptFriend(@RequestBody ConnectionDTO connectionDTO) throws UserNotFoundException {
        userService.acceptFriendRequest(connectionDTO);
    }

    @ResponseBody
    @PutMapping("/reject_friend")
    public void rejectRequest(@RequestBody ConnectionDTO connectionDTO) throws UserNotFoundException {
        userService.rejectRequest(connectionDTO);
    }

    @ResponseBody
    @DeleteMapping("/delete_friend")
    public void deleteFriend(@RequestBody ConnectionDTO connectionDTO) throws UserNotFoundException {
        userService.deleteFriend(connectionDTO);
    }

    @ResponseBody
    @GetMapping("/get_friends_posts")
    public List<Post> getFriendsPosts(@RequestBody String token) throws UserNotFoundException {
        List<Post> friendsPosts=userService.getPostsOfFriends(token);
        Set<Post> sortedPosts = friendsPosts.stream()
                .sorted(Comparator.comparing(Post::getPost_date))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        //List<Post> postList = new ArrayList<>(friendsPosts);
        //Collections.sort(friendsPosts, Comparator.comparing(Post::getPost_date));
        return friendsPosts;
    }
}
