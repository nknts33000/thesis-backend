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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private final CompanyRepo companyRepo;

    private final AdvertRepo advertRepo;

    private final ExprerienceRepo exprerienceRepo;

    private final EducationRepo educationRepo;
    private final CompanyRepository companyRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepo, PostRepo postRepo, SecretKeyConfig secretKeyConfig,
                       ProfileRepo profileRepo, ConnectionRepo connectionRepo, PasswordEncoder passwordEncoder,
                       CommentRepo commentRepo,CompanyRepo companyRepo, AdvertRepo advertRepo,ExprerienceRepo exprerienceRepo,
                       EducationRepo educationRepo,CompanyRepository companyRepository){
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

//        User user1=getUserFromToken(connectionDTO.getToken());
//        User user2=getUser(connectionDTO.getReceipient_email());
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
//        try{
//            User user1=findUserById(initiator_id);
//            User user2=findUserById(recipient_id);
//            connectionRepo.acceptRequest(user1,user2);
//            Connection c=findExistingConnection(initiator_id,recipient_id);
//            System.out.print("conn status: "+c.getConnection_status()+"\n");
//            return c;
//        }
//        catch(Exception e) {
//            throw new CustomException("no connection");
//        }
        try{
            //User user1=findUserById(initiator_id);
           // User user2=findUserById(recipient_id);
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

    public void addCommentToPost(String token, long post_id, String content) throws UserNotFoundException {
        User user=getUserFromToken(token);
        System.out.println("post id:"+post_id);
        Post post= getPostById(post_id);
        commentRepo.save(new Comment(post,user,content));
    }

    public List<Comment> getComments(long postId){
        Post post=getPostById(postId);
        return commentRepo.getCommentFromPost(post);
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

    public List<Post> postsOfCompany(long companyId){
        Company company=companyRepo.findCompanyByCompanyId(companyId);
        List<Post> posts= companyRepo.findPostsOfCompany(company);
        return posts;
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
                    content,user,company
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
}