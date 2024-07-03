package com.example.platform.controller;

import com.example.platform.ElasticSearch.*;
import com.example.platform.dto.*;
import com.example.platform.exceptions.CustomException;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.*;
import com.example.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private AdvertService advertService;
    @Autowired
    private UserSearchingService userSearchingService;
    @Autowired
    private CompanyService companyService;

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
    @PostMapping("/add_friend/{initiator_id}/{recipient_id}")
    public Connection addFriend(@PathVariable("initiator_id") long initiator_id,
            @PathVariable("recipient_id") long recipient_id) throws UserNotFoundException, CustomException {
        System.out.println("new friend request");
        return userService.newFriendRequest(initiator_id,recipient_id);
    }

    @ResponseBody
    @PutMapping("/accept_friend/{initiator_id}/{recipient_id}")
    public Connection acceptFriend(@PathVariable("initiator_id") long initiator_id,
                                   @PathVariable("recipient_id") long recipient_id) throws UserNotFoundException, CustomException {
        System.out.println("accept_friend controller");
        return userService.acceptFriendRequest(initiator_id,recipient_id);
    }

    @ResponseBody
    @DeleteMapping("/cancel_request/{initiator_id}/{recipient_id}")
    public void cancel_request(@PathVariable("initiator_id") long initiator_id,
                               @PathVariable("recipient_id") long recipient_id){
        userService.cancelRequest(initiator_id,recipient_id);
    }

    @ResponseBody
    @PutMapping("/reject_friend/{initiator_id}/{recipient_id}")
    public void rejectRequest(@PathVariable("initiator_id") long initiator_id,
                              @PathVariable("recipient_id") long recipient_id) throws UserNotFoundException {
        userService.rejectRequest(initiator_id,recipient_id);
    }

    @ResponseBody
    @DeleteMapping("/delete_friend/{initiator_id}/{recipient_id}")
    public void deleteFriend(@PathVariable("initiator_id") long initiator_id,
                             @PathVariable("recipient_id") long recipient_id) throws UserNotFoundException {
        userService.deleteFriend(initiator_id,recipient_id);
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
    @GetMapping("/searchAdverts/{query}")
    public List<Advert> searchAdverts(@PathVariable("query") String query) throws UserNotFoundException {
        System.out.println("query:" + query);
        List<AdvertES> advert_docs = advertService.searchByJobSummary(query);
        List<Advert> adverts = new ArrayList<>();
        for (AdvertES advertES : advert_docs) {
            if (advertES != null) { // Check for null before processing
                Advert advert = userService.findAdvertByAdvertId(Long.parseLong(advertES.getId()));
                if (advert != null) { // Check for null advert as well
                    adverts.add(advert);
                }
            }
        }
        return adverts;
    }

    @ResponseBody
    @GetMapping("/getAdvert/{advertId}")
    public Advert getAdvert(@PathVariable("advertId") long advertId){
        return userService.getAdvertByAdvertId(advertId);
    }

    @ResponseBody
    @PostMapping("/createAdvert")
    public void createAdvert(@RequestBody Map<String,String> requestBody){
        Advert advert=userService.addAdvert(requestBody);
        advertService.saveAdvert(
                new AdvertES(
                    Long.toString(advert.getAdvertId()),
                        advert.getJobTitle(),
                        advert.getJobSummary(),
                        advert.getLocation(),
                        advert.getCompany().getName()
                )
        );
    }

    @ResponseBody
    @PostMapping("/addExperience/{id}")
    public void addExperience(@RequestBody Map<String,String> requestBody,@PathVariable("id") long id) throws ParseException {
        System.out.println(requestBody);
        userService.addExperience(id,requestBody);
    }

    @ResponseBody
    @PutMapping("/updateExperience/{id}")
    public void updateExperience(@RequestBody Map<String,String> requestBody,@PathVariable("id") long id) throws ParseException {
        System.out.println(requestBody);
        userService.updateExperience(id,requestBody);
    }

    @ResponseBody
    @DeleteMapping("/deleteExperience/{experience_id}")
    public void deleteExperience(@PathVariable("experience_id") long experience_id){
        userService.deleteExp(experience_id);
    }

    @ResponseBody
    @PostMapping("/addEducation/{id}")
    public void addEducation(@RequestBody Map<String,String> requestBody,@PathVariable("id") long id) throws ParseException {
        userService.addEdu(id,requestBody);
    }

    @ResponseBody
    @PutMapping("/updateEdu/{id}")
    public void updateEducation(@RequestBody Map<String,String> requestBody,@PathVariable("id") long id) throws ParseException {
        userService.updateEdu(id,requestBody);
    }

    @ResponseBody
    @DeleteMapping("/deleteEducation/{education_id}")
    public void deleteEducation(@PathVariable("education_id") long education_id){

        userService.deleteEdu(education_id);
    }

    @ResponseBody
    @GetMapping("/getUser/{token}")
    public User getUser(@PathVariable("token") String token) throws UserNotFoundException {
        User user=userService.getUserFromToken(token);
        System.out.println("token:"+token);
        System.out.println("user:"+user.getEmail());
        return user;
    }

    @ResponseBody
    @GetMapping("/getProfile/{id}")
    public Profile getProfile(@PathVariable("id") long id) throws UserNotFoundException {
        Profile profile=userService.getProfileOfUser(id);
        return profile;
    }

    @ResponseBody
    @GetMapping("/getExperiences/{id}")
    public List<Experience> getExperiences(@PathVariable("id") long id) throws UserNotFoundException {
        List<Experience> experiences=userService.getExperiencesOfUser(id);
        return experiences;
    }

    @ResponseBody
    @GetMapping("/getEducation/{id}")
    public List<Education> getEducation(@PathVariable("id") long id) throws UserNotFoundException {
        List<Education> education=userService.getEducationOfUser(id);
        return education;
    }

    @ResponseBody
    @GetMapping("/getPosts/{id}")
    public List<Post> getPostsOfUser(@PathVariable("id") long id){
        return userService.getPostsOfUser(id);
    }

    @ResponseBody
    @PutMapping("/updateProfPic/{profile_id}")
    public void updatePic(@RequestParam("file") MultipartFile file,@PathVariable("profile_id") long profile_id) throws IOException {
        byte[] fileBytes = file.getBytes();
        userService.uploadProfPic(fileBytes,profile_id);
    }

    @ResponseBody
    @GetMapping("/profilePic/{id}")
    public ResponseEntity<byte[]> getProfilePic(@PathVariable("id") long id) {
        byte[] image = userService.getProfilePicture(id);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @ResponseBody
    @PutMapping("/setSummary/{profile_id}")
    public void setSummary(@PathVariable("profile_id") long profile_id,@RequestBody Map<String,String> requestBody){
        String summary=requestBody.get("summary");
        userService.setSummary(summary,profile_id);
    }

    @ResponseBody
    @GetMapping("/searchUsers/{query}")
    public List<User> searchUsers(@PathVariable("query") String query){
        List<UserES> usersES= userSearchingService.search(query);
        List<User> users= new ArrayList<>();
        for(UserES userES:usersES){
            User user= userService.findUserById(Long.parseLong(userES.getId()));
            users.add(user);
        }
        return users;
    }

    @ResponseBody
    @GetMapping("/searchCompanies/{query}")
    public List<Company> searchCompanies(@PathVariable("query") String query){
        List<CompanyES> companiesES= companyService.search(query);
        List<Company> companies= new ArrayList<>();
        for(CompanyES companyES:companiesES){
            Company company=userService.findCompanyById(Long.parseLong(companyES.getId()));
            companies.add(company);
        }
        return companies;
    }

    @ResponseBody
    @GetMapping("/getCompany/{companyId}")
    public Company getCompanyById(@PathVariable("companyId") long companyId){
        Company company=userService.findCompanyById(companyId);
        return company;
    }

    @ResponseBody
    @GetMapping("/getOwner/{companyId}")
    public User getOwnerByCompanyId(@PathVariable("companyId") long companyId){
        Company company=userService.findCompanyById(companyId);
        User user = findUserById(company.getCreator().getId());
        return user;
    }

    @ResponseBody
    @GetMapping("/findUser/{id}")
    public User findUserById(@PathVariable("id") long id){
        User user=userService.findUserById(id);
        return user;
    }

    @ResponseBody
    @GetMapping("/getPostsOfCompany/{companyId}")
    public List<Post> postsOfCompany(@PathVariable("companyId") long companyId){
        List<Post> posts=userService.postsOfCompany(companyId);
        return posts.stream()
                .sorted(Comparator.comparing(Post::getPost_date).reversed())
                .collect(Collectors.toList());
    }

    @ResponseBody
    @PutMapping("/updateCompanyPic/{companyId}")
    public void updateComPic(@RequestParam("file") MultipartFile file,@PathVariable("companyId") long companyId) throws IOException {
        byte[] fileBytes = file.getBytes();
        userService.updateComLogo(fileBytes,companyId);
    }

    @ResponseBody
    @GetMapping("/getCompanyLogo/{companyId}")
    public byte[] getCompanyLogo(@PathVariable("companyId") long companyId){
        return userService.getCompanyLogo(companyId);
    }

    @ResponseBody
    @PostMapping("/addCompanyPost/{companyId}")
    public void addCompPost(@PathVariable("companyId") long companyId,@RequestBody Map<String,String> reqBody) throws UserNotFoundException, CustomException {
        String token=reqBody.get("token");
        System.out.println("token:"+token);
        String content=reqBody.get("content");
        System.out.println("content:"+content);
        userService.addCompanyPost(companyId,content,token);
    }

    @ResponseBody
    @GetMapping("/getConnection/{user_id}/{id}")
    public Connection getConnection(@PathVariable("user_id") long user_id,@PathVariable("id") long id){
        return userService.findExistingConnection(user_id,id);
    }

    @PostMapping("/submitResume/{advertId}/{user_id}")
    public ResponseEntity<String> submitResume(@PathVariable long advertId,@PathVariable long user_id, @RequestParam("resume") MultipartFile resume) {
        try {
            Advert jobAdvertisementOpt = userService.getAdvertByAdvertId(advertId);
            User user =findUserById(user_id);
            try {
                if(user!=null){
                    userService.saveResume(resume, jobAdvertisementOpt,user);
                    return ResponseEntity.ok("Resume submitted successfully!");
                }
                else return ResponseEntity.status(404).body("User not found");
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Failed to submit resume.");
            }

        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getResumes/{advertId}")
    public ResponseEntity<List<Resume>> getResumes(@PathVariable("advertId") long advertId) {
        List<Resume> resumes = userService.getResumesByJobAdvertisement(advertId);
        System.out.println(resumes);
        return ResponseEntity.ok(resumes);
    }

}
