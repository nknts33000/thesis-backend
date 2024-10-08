package com.example.platform.controller;

import com.example.platform.ElasticSearch.*;
import com.example.platform.dto.*;
import com.example.platform.exceptions.CustomException;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.*;
import com.example.platform.security.config.UserAuthenticationProvider;
import com.example.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    UserController(UserService userService){
       this.userService=userService;
    }

    @ResponseBody
    @PutMapping("/update")
    public void Update(@RequestBody RegistrationDTO registrationDTO) throws UserNotFoundException {

        userService.update(registrationDTO.getEmail(),registrationDTO);
    }

//    @ResponseBody
//    @PutMapping("/update/password")
//    public void UpdatePassword(@RequestBody User user) throws UserNotFoundException {
//        userService.updateUserPassword(user.getEmail(),user);
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
    @DeleteMapping("/reject_friend/{initiator_id}/{recipient_id}")
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
        List<Post> followingCompPosts=userService.getPostsOfFollowingCompanies(token);

        List<Share> friendsShares=userService.getShareOfFriends(token);
        List<Share> sharesOfCompanies=userService.getSharesOfFollowingCompanies(token);


        for(Post p:followingCompPosts){
            friendsPosts.add(p);
        }

        for(Share s:sharesOfCompanies){
            friendsShares.add(s);
        }
        // Create PostDTO objects with corresponding profiles
        Set<PostDTO> PostsWithUsersAndCompanies = userService.postsToPostDTO(friendsPosts);

        Set<PostDTO> SharesWithUsersAndCompanies = userService.sharesToPostDTO(friendsShares);

        PostsWithUsersAndCompanies.addAll(SharesWithUsersAndCompanies);

        return PostsWithUsersAndCompanies.stream().sorted(Comparator.comparing(PostDTO::getTimestamp).reversed()).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @ResponseBody
    @PostMapping("/addComment/{post_id}/{user_id}")
    public void addComment(@RequestBody Map<String,String> reqBody,@PathVariable long post_id,@PathVariable long user_id) throws UserNotFoundException {
        String content=reqBody.get("content");
        userService.addCommentToPost(user_id,post_id,content);
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
    public Set<PostDTO> getPostsOfUser(@PathVariable("id") long id){
        List<Post> profile_posts= userService.getPostsOfUser(id);
        List<Share> profile_shares=userService.getSharesOfUser(id);
        Set<PostDTO> userPosts= userService.postsToPostDTO(profile_posts);
        Set<PostDTO> userShares=userService.sharesToPostDTO(profile_shares);
        userPosts.addAll(userShares);

        System.out.println("posts:");
        System.out.println(userPosts);
        return userPosts.stream().sorted(Comparator.comparing(PostDTO::getTimestamp).reversed()).collect(Collectors.toCollection(LinkedHashSet::new));
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
    public Set<PostDTO> postsOfCompany(@PathVariable("companyId") long companyId){
        Set<PostDTO> posts=userService.postsOfCompany(companyId);
        Set<PostDTO> shares=userService.sharesOfCompany(companyId);
        posts.addAll(shares);
        return posts
                .stream()
                .sorted(Comparator.comparing(PostDTO::getTimestamp).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
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
        System.out.println("in the resume controller");
        try {
            Advert jobAdvertisementOpt = userService.getAdvertByAdvertId(advertId);
            User user =findUserById(user_id);
//            System.out.println("user="+user);
            try {
                if(user!=null){
                    System.out.println("in if statement");
                    Resume resume1=userService.saveResume(resume, jobAdvertisementOpt,user);
                    System.out.println("resume user:"+resume1.getUser());
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


    // Example method to fetch resumes by advertisement ID
    @GetMapping("/getResumes/{advertId}")
    public ResponseEntity<List<Resume>> getResumesByAdvertId(@PathVariable long advertId) {
        List<Resume> resumes = userService.getResumesOfAdvert(advertId);
        // Assuming filepath is stored relative to uploads directory
        resumes.forEach(resume -> {
            String filename = resume.getFilename(); // Get filename from resume object
            String fullUrl = "/uploads/" + filename; // Construct full URL
            resume.setFilepath(fullUrl); // Set full URL to the resume object
        });
        return ResponseEntity.ok(resumes);
    }


    @GetMapping("/getCompanyOfAdvert/{advertId}")
    public Company getCompanyOfAdvert(@PathVariable long advertId){
        return userService.getCompanyOfAdvert(advertId);
    }

    @GetMapping("/hasSubmittedResume/{advertId}/{user_id}")
    public boolean resumeSubmited(@PathVariable long advertId,@PathVariable long user_id){
        return userService.userApplied(advertId,user_id);
    }

    @GetMapping("/isAdmin/{id}/{companyId}")
    public boolean isAdmin(@PathVariable long id,@PathVariable long companyId){
        return userService.isAdminOrCreator(id,companyId);
    }

    @GetMapping("/resumes/{advertId}")
        public List<Resume> getResumesOfAdvert(@PathVariable long advertId){
        return userService.getResumesOfAdvert(advertId);
    }

    @PostMapping("/sendMessage/{sender_id}/{receiver_id}")
    public void sendMessage(@RequestBody Map<String,String> reqBody,@PathVariable long sender_id,@PathVariable long receiver_id) {
        String content= reqBody.get("content");
        System.out.println("content:"+content);
        userService.sendMessage(content,sender_id,receiver_id);
    }

    @GetMapping("/between/{senderId}/{receiverId}")
    public List<Message> getMessagesBetweenUsers(@PathVariable long senderId, @PathVariable long receiverId) {
        return userService.getMessagesBetweenUsers(senderId, receiverId);
    }

    @GetMapping("/getConvos/{user_id}")
    public Map<String,List<Message>> getConvosOfUser(@PathVariable long user_id){
        return userService.findConvosOfUser(user_id);
    }

    @GetMapping("/pendingRequests/{userId}")
    public List<Connection> getPendingFriendRequests(@PathVariable long userId) {
        return userService.getPendingFriendRequests(userId);
    }

    @GetMapping("/getCommentsOfPost/{post_id}")
    public List<CommentDTO> getCommentsOfPost(@PathVariable long post_id){
        return userService.getCommentDTOs(post_id);
    }


    @GetMapping("/download/{resumeId}")
    public ResponseEntity<Resource> downloadResume(@PathVariable long resumeId) {
        try {
            Resume resume = userService.findResumeById(resumeId)
                    .orElseThrow(() -> new RuntimeException("Resume not found"));

            Path filePath = Paths.get(resume.getFilepath()).normalize();
            UrlResource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                //headers.setContentType(MediaType.APPLICATION_PDF); // Set Content-Type as application/pdf
                headers.setContentDispositionFormData("attachment", resume.getFilename());

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @ResponseBody
    @PutMapping("/setMission/{companyId}")
    public void setMission(@PathVariable("companyId") long companyId,@RequestBody Map<String,String> requestBody){
        String mission=requestBody.get("mission");
        userService.setMission(mission,companyId);
    }

    @ResponseBody
    @PutMapping("/follow/{user_id}/{companyId}")
    public void followCompany(@PathVariable long user_id,@PathVariable long companyId){
        userService.followCompany(user_id,companyId);
    }

    @ResponseBody
    @PutMapping("/unfollow/{user_id}/{companyId}")
    public void unfollowCompany(@PathVariable long user_id,@PathVariable long companyId){
        userService.unfollowCompany(user_id,companyId);
    }

    @ResponseBody
    @GetMapping("/isFollower/{user_id}/{companyId}")
    public boolean isFollower(@PathVariable long user_id,@PathVariable long companyId){
        return userService.isFollower(user_id,companyId);
    }

    @ResponseBody
    @PostMapping("/like/{user_id}/{post_id}")
    public void like(@PathVariable long user_id,@PathVariable long post_id){
        userService.likeAPost(user_id,post_id);
    }

    @ResponseBody
    @DeleteMapping("/unlike/{user_id}/{post_id}")
    public void unlike(@PathVariable long user_id,@PathVariable long post_id){
        userService.unlikeAPost(user_id,post_id);
    }


    @ResponseBody
    @PostMapping("/checkLikes/{user_id}")
    public Map<Long, Boolean> checkLikes(@PathVariable long user_id, @RequestBody CheckLikesRequest checkLikesRequest) {
        List<Long> postIds = checkLikesRequest.getPostIds();
        System.out.println("longs"+postIds);
        return userService.getLikedPosts(user_id, postIds);
    }

    @ResponseBody
    @PostMapping("/share/{user_id}/{post_id}")
    public void sharePost(@PathVariable long user_id,@PathVariable long post_id,@RequestBody Map<String,String> reqBody){
        String description=reqBody.get("description");
        userService.sharePost(user_id,post_id,description);
    }

    @ResponseBody
    @PostMapping("/shareForCompany/{company_id}/{post_id}")
    public void shareCompany(@PathVariable long company_id,@PathVariable long post_id,@RequestBody Map<String,String> reqBody){
        String description=reqBody.get("description");
        userService.sharePostForCompany(company_id,post_id,description);
    }

    @ResponseBody
    @GetMapping("/getAllFriends/{id}")
    public Set<User> getAllFriends(@PathVariable long id){
        return userService.getAllFriends(id);
    }

    @ResponseBody
    @GetMapping("/getAllFollowings/{id}")
    public Set<Company> getAllFollowings(@PathVariable long id){
        Set<Company> companies=userService.getFollowings(id);
        System.out.println("followings"+companies);
        return companies;
    }

    @ResponseBody
    @PutMapping("/changeEmail/{id}")
    public String changeEmail(@PathVariable long id, @RequestBody Map<String,String> reqBody){
        String email=reqBody.get("email");
        System.out.println("new email:"+email);
        User user=userService.updateUserEmail(id,email);
        String token= userAuthenticationProvider.createToken(user.getEmail());
        System.out.println("new token:"+token);
        return token;
    }

    @ResponseBody
    @PutMapping("/changePassword/{id}")
    public void changePassword(@PathVariable long id,@RequestBody Map<String,String> reqBody){
        String password=reqBody.get("password");
        System.out.println("new password:"+password);
        userService.updatePassword(id,password);
    }

    @ResponseBody
    @PutMapping("/changeFirstname/{id}")
    public void changeFirstname(@PathVariable long id,@RequestBody Map<String,String> reqBody){
        String firstname=reqBody.get("firstName");
        System.out.println("new firstName:"+firstname);
        userService.updateFirstName(id,firstname);
    }

    @ResponseBody
    @PutMapping("/changeLastname/{id}")
    public void changeLastname(@PathVariable long id,@RequestBody Map<String,String> reqBody){
        String lastname=reqBody.get("lastName");
        System.out.println("new lastName:"+lastname);
        userService.updateLastName(id,lastname);
    }

    @ResponseBody
    @PutMapping("/changeLocation/{id}")
    public void changeLocation(@PathVariable long id,@RequestBody Map<String,String> reqBody){
        String location=reqBody.get("location");
        System.out.println("new location:"+location);
        userService.updateLocation(id,location);
    }

}
