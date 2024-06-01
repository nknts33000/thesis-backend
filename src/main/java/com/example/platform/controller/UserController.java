package com.example.platform.controller;

import com.example.platform.dto.*;
import com.example.platform.exceptions.CustomException;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.*;
import com.example.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

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

//    @ResponseBody
//    @DeleteMapping("/delete")
//    public void deleteUser(@RequestBody UserDTO userdto) throws UserNotFoundException {
//
//        userService.deleteUserByEmail(userdto);
//    }

    @ResponseBody
    @PostMapping("/post")
    public void upload_post(@RequestBody Map<String, String> requestBody) throws UserNotFoundException {
        String token=requestBody.get("token");
        String content=requestBody.get("content");
        userService.addPost(token,content);
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
    @PostMapping("/get_friends_posts")
    public Set<PostDTO> getFriendsPosts(@RequestBody Map<String, String> requestBody) throws UserNotFoundException {
        String token = requestBody.get("token");
        System.out.println(token);
        List<Post> friendsPosts = userService.getPostsOfFriends(token);

        // Fetch profiles for all users in the friend's posts
        Map<User, Profile> userProfiles = friendsPosts.stream()
                .map(Post::getUser)
                .distinct() // Ensure unique users
                .collect(Collectors.toMap(
                        user -> user,
                        user -> user.getProfile() // Access the profile directly from the user entity
                ));

        // Create PostDTO objects with corresponding profiles
        Set<PostDTO> sortedPostsWithUsers = friendsPosts.stream()
                .sorted(Comparator.comparing(Post::getPost_date).reversed())
                .map(post -> {
                    User user = userService.getUserByPost(post);
                    Profile profile = userProfiles.get(userService.findUserById(user.getId()));
                    List<Comment> comments= userService.getComments(post.getPostId()).stream()
                            .sorted(Comparator.comparing(Comment::getComment_date).reversed())
                            .toList();
                    List<CommentDTO> commentdtos=new ArrayList<>();
                    for(Comment c:comments){
                        commentdtos.add(
                                new CommentDTO(
                                        c.getUser().getFirstname(),
                                        c.getUser().getLastname(),
                                        c.getContent(),
                                        c.getUser().getProfile().getPicture_url())
                        );
                    }

                    return new PostDTO(
                            post,
                            user.getFirstname(),
                            user.getLastname(),
                            profile.getPicture_url(),
                            commentdtos);
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return sortedPostsWithUsers;
    }

    @ResponseBody
    @PostMapping("/addComment")
    public void addComment(@RequestBody Map<String,String> requestBody) throws UserNotFoundException {
        String token=requestBody.get("token");
        long post_id= parseLong(requestBody.get("post_id"));
        String content= requestBody.get("content");
        userService.addCommentToPost(token,post_id,content);
    }

    @ResponseBody
    @GetMapping("/getComments/{postId}")
    public  List<Comment> getCommentsByPost(@PathVariable("postId") long postId){
        System.out.println("postId="+postId);
        List<Comment> comments=userService.getComments(postId);

        for(Comment c:comments){
            System.out.println("comment user id:"+c.getUser().getId());
        }

        return comments.stream()
                .sorted(Comparator.comparing(Comment::getComment_date).reversed())
                .collect(Collectors.toList());
    }


    @ResponseBody
    @PostMapping("/createCompany")
    public void createCompany(@RequestBody Map<String,String> requestBody) throws UserNotFoundException {
        userService.createCompany(requestBody.get("token"),requestBody.get("mission"),requestBody.get("name"));
    }

    @ResponseBody
    @GetMapping("/getCompanies/{token}")
    public List<Company> getCompaniesOfUser(@PathVariable("token") String token) throws UserNotFoundException {
        return userService.getCompanies(token);
    }

    @ResponseBody
    @PostMapping("/createAdvert")
    public void createAdvert(@RequestBody Map<String,String> requestBody){
        userService.addAdvert(requestBody);
        System.out.println("company id:" + requestBody.get("company"));
    }
//
//    @ResponseBody
//    @GetMapping("/profile/{token}")
//    public Profile getProfile(@PathVariable("token") String token) throws UserNotFoundException {
//        return userService.getProfileOfUser(token);
//    }
//
//    @ResponseBody
//    @GetMapping("/experiences/{token}")
//    public List<Experience> getExperiences(@PathVariable("token") String token) throws UserNotFoundException {
//        return userService.getExperiencesOfUser(token);
//    }


    @ResponseBody
    @PostMapping("/addExperience/{id}")
    public void addExperience(@RequestBody Map<String,String> requestBody,@PathVariable("id") long id) throws ParseException {
        System.out.println(requestBody);
        userService.addExperience(id,requestBody);
    }


    @ResponseBody
    @GetMapping("/getUser/{token}")
    public User getUser(@PathVariable("token") String token) throws UserNotFoundException {
        return userService.getUserFromToken(token);
    }
}
