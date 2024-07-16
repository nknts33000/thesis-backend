package com.example.platform.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.platform.ElasticSearch.CompanyES;
import com.example.platform.ElasticSearch.CompanyRepository;
import com.example.platform.dto.*;
import com.example.platform.exceptions.CustomException;
import com.example.platform.exceptions.InvalidCredentialsException;
import com.example.platform.exceptions.UserExistsException;
import com.example.platform.exceptions.UserNotFoundException;
import com.example.platform.model.*;
import com.example.platform.repo.*;
import com.example.platform.security.config.SecretKeyConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    //private String secretKey="${security.jwt.token.secret-key:secret-value}";
    private final UserRepo userRepo;
    private final PostRepo postRepo;
    private final SecretKeyConfig secretKeyConfig;
    private final ProfileRepo profileRepo;
    private final CommentRepo commentRepo;
    private final ConnectionRepo connectionRepo;

    private final CompanyRepo companyRepo;

    private final AdvertRepo advertRepo;

    private final ExprerienceRepo exprerienceRepo;

    private final EducationRepo educationRepo;
    private final CompanyRepository companyRepository;
    private PasswordEncoder passwordEncoder;

    private final ResumeRepo resumeRepository;

    private final MessageRepo messageRepo;

    private final LikeRepo likeRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public UserService(UserRepo userRepo, PostRepo postRepo, SecretKeyConfig secretKeyConfig,
                       ProfileRepo profileRepo, ConnectionRepo connectionRepo, PasswordEncoder passwordEncoder,
                       CommentRepo commentRepo,CompanyRepo companyRepo, AdvertRepo advertRepo,ExprerienceRepo exprerienceRepo,
                       EducationRepo educationRepo,CompanyRepository companyRepository,ResumeRepo resumeRepository,MessageRepo messageRepo,
                       LikeRepo likeRepo){
        this.secretKeyConfig = secretKeyConfig;
        this.commentRepo=commentRepo;
        this.profileRepo = profileRepo;
        this.connectionRepo = connectionRepo;
        this.userRepo=userRepo;
        this.postRepo=postRepo;
        this.companyRepo=companyRepo;
        this.passwordEncoder=passwordEncoder;
        this.advertRepo=advertRepo;
        this.exprerienceRepo=exprerienceRepo;
        this.educationRepo=educationRepo;
        this.companyRepository=companyRepository;
        this.resumeRepository=resumeRepository;
        this.messageRepo=messageRepo;
        this.likeRepo=likeRepo;
    }


    public User findUserById(Long id){
        return userRepo.findUserById(id);
    }

    public Company findCompanyById(Long id){
        return companyRepo.findCompanyByCompanyId(id);
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

    public User addUser(RegistrationDTO registrationDTO) throws UserExistsException, UserNotFoundException {

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
            return user;
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

    public List<Post> getPostsOfFollowingCompanies(String token) throws UserNotFoundException {
        User user= getUserFromToken(token);
        List<Post> postsOfFollowingCompanies = new ArrayList<>();
        List<Company> followingCompanies=user.getFollowing();
        for (Company c:followingCompanies){
            for(Post p:c.getPosts()){
                postsOfFollowingCompanies.add(p);
            }
        }
        return postsOfFollowingCompanies;
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
            Connection connection= connection1.get(0);
            if(connection.getConnection_status().equals("Pending")){
                return true;
            }
            else{
                return false;
            }
        } else if (!connection2.isEmpty()) {
            Connection connection= connection2.get(0);
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

    public Connection newFriendRequest(long initiator_id, long recipient_id) throws UserNotFoundException, CustomException {

        User user1=findUserById(initiator_id);
        User user2=findUserById(recipient_id);
        boolean connectionExists=connectionExists(user1,user2);
        System.out.println("conn exists:"+connectionExists);
        if(!connectionExists){
            Connection connection=new Connection(user1,user2,"Pending");
            connectionRepo.save(connection);

            return findExistingConnection(initiator_id,recipient_id);
        }
        else{
            throw new CustomException("Connection exists");
        }
    }

    public Connection acceptFriendRequest(long initiator_id, long recipient_id) throws UserNotFoundException, CustomException {
        try{

            Connection c=findExistingConnection(initiator_id,recipient_id);
            c.setConnection_status("Friends");
            connectionRepo.save(c);
            System.out.print("conn status: "+c.getConnection_status()+"\n");
            return c;
        }
        catch(Exception e) {
            throw new CustomException("no connection");
        }

    }

    public void rejectRequest(long initiator_id,long recipient_id) throws UserNotFoundException {
//        User user1=getUserFromToken(connectionDTO.getToken());
//        User user2=getUser(connectionDTO.getReceipient_email());
        User user1=findUserById(initiator_id);
        User user2=findUserById(recipient_id);
        boolean isPending=requestPending(user1,user2);
        if(isPending){
            connectionRepo.rejectRequest(user1,user2);
        }
    }

    public void cancelRequest(long initiator_id,long recipient_id){
        User user1=findUserById(initiator_id);
        User user2=findUserById(recipient_id);
        connectionRepo.cancelRequest(user1,user2);
    }

    public void deleteFriend(long initiator_id,long recipient_id) throws UserNotFoundException {
//        User user1=getUserFromToken(connectionDTO.getToken());
//        User user2=getUser(connectionDTO.getReceipient_email());
        User user1=findUserById(initiator_id);
        User user2=findUserById(recipient_id);
        connectionRepo.deleteFriend(user1,user2);
    }

    public User getUserByPost(Post post) {
//        List<User> result= postRepo.getUserByPostId(post.getPostId());
//        User user=result.getFirst();

        return post.getUser();
        //return new UserDTO(user.getId(), user.getEmail(), user.getFirstname(), user.getLastname(), null);
    }

    public Post getPostById(long postId){
        Post post=postRepo.findByPostId(postId);
        return post;
    }

    public void addCommentToPost(long user_id, long post_id, String content) throws UserNotFoundException {
        User user=findUserById(user_id);
        Post post= getPostById(post_id);
        commentRepo.save(new Comment(post,user,content));
    }

    public List<Comment> getComments(long postId){
        Post post=getPostById(postId);
        return commentRepo.getCommentFromPost(post);
    }

    public List<CommentDTO> getCommentDTOs(long post_id){
        List<Comment> comments= getComments(post_id).stream()
                .sorted(Comparator.comparing(Comment::getComment_date).reversed())
                .toList();
        List<CommentDTO> commentdtos=new ArrayList<>();
        for(Comment c:comments){
            commentdtos.add(
                    new CommentDTO(c.getUser(), c)
            );
        }
        return commentdtos;
    }

    public void createCompany(String token,String mission,String name) throws UserNotFoundException {
        User creator =getUserFromToken(token);
        Company company=companyRepo.save(
                new Company(name,mission,creator)
        );
        companyRepository.save(
                new CompanyES(
                    Long.toString(company.getCompanyId()),
                    company.getName()
                )
        );

    }

    public List<Company> getCompanies(String token) throws UserNotFoundException {
        User user = getUserFromToken(token);
        return user.getCompanies();
    }

    public Advert findAdvertByAdvertId(long advertId){
        return advertRepo.findAdvertByAdvertId(advertId);
    }

    public Advert addAdvert(Map<String,String> requestBody){
        Company company= companyRepo.findCompanyByCompanyId(Long.valueOf(requestBody.get("company")));
        Advert advert = advertRepo.save(
                new Advert(
                        requestBody.get("jobTitle"),
                        requestBody.get("jobSummary"),
                        requestBody.get("location"),
                        requestBody.get("contactInformation"),
                        company
                )
        );

        return advert;
    }

    public Profile getProfileOfUser(long id) throws UserNotFoundException {
        User user= findUserById(id);
        Profile profile=profileRepo.findByUser(user).orElseThrow(UserNotFoundException::new);
        return profile;
    }

    public List<Experience> getExperiencesOfUser(long id) throws UserNotFoundException {
        User user= findUserById(id);
        List<Experience> experiences=exprerienceRepo.getExperiencesOfUser(user);
        return experiences;
    }

    public List<Education> getEducationOfUser(long id){
        User user=findUserById(id);
        List<Education> education=educationRepo.getEducationByUser(user);
        for(Education ed:education){
            System.out.println("start date:"+ed.getStart_date());
            System.out.println("start date:"+ed.getEnd_date());
        }

        return education;
    };


    public void addExperience(long id, Map<String, String> requestBody) throws ParseException {
        User user = findUserById(id);

        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Parse the start date and end date strings into Date objects
        Date startDate = dateFormat.parse(requestBody.get("start_date"));
        Date endDate = dateFormat.parse(requestBody.get("end_date"));

        // Save the Experience object
        exprerienceRepo.save(
                new Experience(
                        requestBody.get("company_name"),
                        requestBody.get("title"),
                        requestBody.get("location"),
                        startDate,
                        endDate,
                        user
                )
        );
    }

    public void updateExperience(long id,Map<String,String> requestBody) throws ParseException {
        User user=findUserById(id);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Parse the start date and end date strings into Date objects
        Date startDate = dateFormat.parse(requestBody.get("start_date"));
        Date endDate = dateFormat.parse(requestBody.get("end_date"));
        exprerienceRepo.save(
                new Experience(
                        Long.parseLong(requestBody.get("experience_id")),
                        requestBody.get("company_name"),
                        requestBody.get("title"),
                        requestBody.get("location"),
                        startDate,
                        endDate,
                        user
                )
        );
    }

    public void deleteExp(long experienceId) {
        exprerienceRepo.deleteById(experienceId);
    }

    public void addEdu(long id,Map<String,String> requestBody) throws ParseException{
        User user=findUserById(id);
        Education education=new Education(
                requestBody.get("school_name"),requestBody.get("degree"),
                requestBody.get("field_of_study"),new SimpleDateFormat("yyyy-MM-dd").parse(requestBody.get("start_date")),
                new SimpleDateFormat("yyyy-MM-dd").parse(requestBody.get("end_date")),user
        );
        educationRepo.save(
          education
        );

    }
    public void updateEdu(long id, Map<String, String> requestBody) throws ParseException {
        User user=findUserById(id);
        educationRepo.save(
                new Education(
                        Long.parseLong(requestBody.get("education_id")),requestBody.get("school_name"),requestBody.get("degree"),
                        requestBody.get("field_of_study"),new SimpleDateFormat("yyyy-MM-dd").parse(requestBody.get("start_date")),
                        new SimpleDateFormat("yyyy-MM-dd").parse(requestBody.get("end_date")),user
                )
        );
    }

    public void deleteEdu(long education_id){
        educationRepo.deleteById(education_id);
    }

    public List<Post> getPostsOfUser(long id){
        User user=findUserById(id);
        return user.getPosts();
    }

    public void uploadProfPic(byte[] profilePicture,long profile_id){
        profileRepo.updateProfPic(profilePicture,profile_id);
    }

    public byte[] getProfilePicture(long id) {
        User user=findUserById(id);
        return profileRepo.findProfilePictureById(user).orElse(null);
    }

    public void setSummary(String summary,long profile_id){
        profileRepo.setSummary(summary,profile_id);
    }

    public void setMission(String mission,long companyId){
        Company company=findCompanyById(companyId);
        company.setMission(mission);
        companyRepo.save(company);

    }

    public Set<PostDTO> postsOfCompany(long companyId){
        Company company=companyRepo.findCompanyByCompanyId(companyId);
        List<Post> posts= companyRepo.findPostsOfCompany(company);
        return postsToPostDTO(posts);
    }

    public void updateComLogo(byte[] fileBytes, long companyId){
        companyRepo.updateComLogo(fileBytes,companyId);
    }

    public byte[] getCompanyLogo(long companyId) {
        return companyRepo.findLogoById(companyId).orElse(null);
        //profileRepo.findProfilePictureById(user).orElse(null);
    }
    public void addCompanyPost(long companyId,String content,String token) throws UserNotFoundException, CustomException {
        Company company=findCompanyById(companyId);
        User user= getUserFromToken(token);

        if(company.getAdmins().contains(user) || company.getCreator().equals(user)){
            postRepo.save(new Post(
                    content,company
            ));
        }
        else{
            throw new CustomException("You're not an admin of this company's page.");
        }

    }

    public Connection findExistingConnection(long id1,long id2){
        User user1 =findUserById(id1);
        User user2=findUserById(id2);
        List<Connection> connectionList=connectionRepo.getConnetions(user1,user2);
        List<Connection> connectionList2=connectionRepo.getConnetions(user2,user1);
        if(!connectionList.isEmpty()){
            return connectionList.get(0);
        } else if (!connectionList2.isEmpty()) {
            return connectionList2.get(0);
        }
        else {
            Connection c=new Connection();
            c.setConnection_status("no_connection");
            return c;
        }



    }

    public Advert getAdvertByAdvertId(long advertId) {
        return advertRepo.findAdvertByAdvertId(advertId);
    }

    public Resume saveResume(MultipartFile file, Advert jobAdvertisement, User user) throws IOException {
        // Ensure the upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Create a unique filename
        String filename = jobAdvertisement.getAdvertId() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);

        // Detailed logging
        System.out.println("Upload Directory: " + uploadPath.toString());
        System.out.println("File Path: " + filePath.toString());

        // Save file to disk with detailed error logging
        try (InputStream inputStream = file.getInputStream()) {
            if (file.isEmpty() || inputStream.available() <= 0) {
                throw new IOException("Uploaded file is empty or invalid: " + file.getOriginalFilename());
            }
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File saved successfully: " + filePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed to save file: " + filename, e);
        }

        // Save resume metadata
        Resume resume = new Resume();
        resume.setFilename(filename);
        resume.setFilepath(filePath.toString());
        resume.setJobAdvertisement(jobAdvertisement);
        resume.setUser(user);

        // Add user to applicants
        jobAdvertisement.getApplicants().add(user);
        user.getApplications().add(jobAdvertisement);

        // Save changes to the database
        advertRepo.save(jobAdvertisement); // Save the updated Advert
        userRepo.save(user); // Save the updated User

        return resumeRepository.save(resume);
    }



    public List<Resume> getResumesByJobAdvertisement(long jobAdvertisementId) {
        Advert advert=findAdvertByAdvertId(jobAdvertisementId);
        return advert.getResumes();
        //return resumeRepository.findByAdvert_AdvertId(jobAdvertisementId);
    }

    public Company getCompanyOfAdvert(long advertId){
        Advert advert=findAdvertByAdvertId(advertId);
        return advert.getCompany();
    }

    public boolean userApplied(long advertId,long user_id){
        User user=findUserById(user_id);
        Advert advert=findAdvertByAdvertId(advertId);
        if(advert.getApplicants().contains(user)) return true;
        else return false;
    }

    public boolean isAdminOrCreator(long id,long companyId){
        User user=findUserById(id);
        Company company=findCompanyById(companyId);
        if (company.getCreator().equals(user)) return true;
        else return false;
    }

    public List<Resume> getResumesOfAdvert(long advertId) {
        Advert advert=findAdvertByAdvertId(advertId);
        return advert.getResumes();
    }


    public void sendMessage(String content,long sender_id,long receiver_id) {
        User sender=findUserById(sender_id);
        User receiver=findUserById(receiver_id);
        messageRepo.save(
                new Message(sender,receiver,content)
        );
    }

    public List<Message> getMessagesBetweenUsers(long senderId, long receiverId) {

        // Combine the lists

        System.out.println("sender:" + senderId);
        System.out.println("receiver:"+receiverId);
        User sender=findUserById(senderId);
        User receiver=findUserById(receiverId);
        List<Message> combinedList = messageRepo.getAllMessages(sender,receiver);//new ArrayList<>(firstList);
        //combinedList.addAll(secondList);

        // Sort the combined list by timestamp
        combinedList.sort(Comparator.comparing(Message::getTimestamp));

        System.out.println(combinedList);
        return combinedList;
    }



    public Map<String, List<Message>> findConvosOfUser(long user_id) {
        User current_user = findUserById(user_id);
        List<User> usersOfConvos = messageRepo.findDistinctConversationUsers(current_user); // find all the users this one has had conversations with
        Map<String, List<Message>> convos = new HashMap<>();

        for (User user : usersOfConvos) {
            List<Message> messages = getMessagesBetweenUsers(user_id, user.getId());
            convos.put(convertUserToJson(user), messages);
        }

        convos = convos.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    // Debugging: Print out the users and timestamps being compared
                    System.out.println("Comparing " + entry1.getKey() + " and " + entry2.getKey());
                    if (entry1.getValue().isEmpty() || entry2.getValue().isEmpty()) {
                        // Handle empty message lists gracefully
                        return entry1.getValue().isEmpty() ? 1 : -1;
                    }

                    LocalDateTime lastTimestamp1 = entry1.getValue().get(entry1.getValue().size() - 1).getTimestamp();
                    LocalDateTime lastTimestamp2 = entry2.getValue().get(entry2.getValue().size() - 1).getTimestamp();

                    // Debugging: Print out the timestamps being compared
                    System.out.println("Timestamps: " + lastTimestamp1 + " vs " + lastTimestamp2);

                    return lastTimestamp2.compareTo(lastTimestamp1);
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new // Keeps the sorted order
                ));

        // Debugging: Print out the final sorted map
        System.out.println("Sorted Conversations: ");
        convos.forEach((key, value) -> System.out.println(key + ": " + value.get(value.size() - 1).getContent()));

        return convos;
    }


    // Helper method to convert User to JSON
    private String convertUserToJson(User user) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public List<Connection> getPendingFriendRequests(long userId) {
        User user = findUserById(userId);
        return connectionRepo.getPendingRequestsOfUser2(user);
    }

    public Set<PostDTO> postsToPostDTO(List<Post> posts){
        return posts.stream()
                .sorted(Comparator.comparing(Post::getPost_date).reversed())
                .map(post -> {
//                    User user = getUserByPost(post);
                    List<Comment> comments= getComments(post.getPostId()).stream()
                            .sorted(Comparator.comparing(Comment::getComment_date).reversed())
                            .toList();
                    List<CommentDTO> commentdtos=new ArrayList<>();
                    for(Comment c:comments){
                        commentdtos.add(
                                new CommentDTO(c.getUser(), c)
                        );
                    }

                    return new PostDTO(post, post.getUser(), post.getCompany(), commentdtos);
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Optional<Resume> findResumeById(long id){
        return resumeRepository.findById(id);
    }

    public void followCompany(long user_id,long companyId){
        User user=findUserById(user_id);
        Company company=findCompanyById(companyId);
        user.getFollowing().add(company);
        company.getFollowers().add(user);
        userRepo.save(user);
        companyRepo.save(company);
    }

    public void unfollowCompany(long user_id,long companyId){
        User user=findUserById(user_id);
        Company company=findCompanyById(companyId);
        user.getFollowing().remove(company);
        company.getFollowers().remove(user);
        userRepo.save(user);
        companyRepo.save(company);
    }

    public boolean isFollower(long user_id,long companyId){
        User user=findUserById(user_id);
        Company company=findCompanyById(companyId);
        return company.getFollowers().contains(user);
    }

    public void likeAPost(long userId, long postId) {
        User user=findUserById(userId);
        Post post=getPostById(postId);
        likeRepo.save(
                new Like(user,post)
        );
    }

    public void unlikeAPost(long userId, long postId) {
        User user=findUserById(userId);
        Post post=getPostById(postId);
//        Like like=new Like();
        Optional<Like> like=likeRepo.findLike(user,post);
        if (like.isPresent()) likeRepo.delete(like.get());
    }

//    public Set<PostDTO> getLikedPosts(long user_id,List<Long> postIds){
//        User user=findUserById(user_id);
//        List<Post> posts=new ArrayList<>();
//        for (long postId:postIds){
//            Post p= getPostById(postId);
//            Optional<Like> current_like=likeRepo.fetchLikes(user,p);
//            if(current_like.isPresent()) posts.add(p);
//        }
//
//        return postsToPostDTO(posts);
//    }

    public Map<Long, Boolean> getLikedPosts(long user_id, List<Long> postIds) {
        User user = findUserById(user_id);
        Map<Long, Boolean> likedStatus = new HashMap<>();
        for (Long postId : postIds) {
            Post p = getPostById(postId);
            Optional<Like> current_like = likeRepo.findLike(user, p);
            likedStatus.put(postId, current_like.isPresent());
        }
        return likedStatus;
    }
}