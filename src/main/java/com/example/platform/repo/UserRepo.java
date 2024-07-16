package com.example.platform.repo;

import com.example.platform.model.Post;
import com.example.platform.model.Profile;
import com.example.platform.model.Share;
import com.example.platform.model.User;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface UserRepo extends JpaRepository<User,Long> {
    public Optional<User> findByEmail(String email);

    public User findUserById(Long id);

    @Modifying
    @Query("update User u set u.location = ?1,u.firstname=?2,u.lastname=?5,u.password=?3 where u.email = ?4")
    void updateUser(String location,String name,String password,String email,String lastname);

    @Modifying
    @Query("update User u set u.location = ?1 where u.email = ?2")
    void updateLocation(String location,String email);

    @Modifying
    @Query("update User u set u.firstname = ?1 where u.email = ?2")
    void updateName(String firstname,String email);

    @Modifying
    @Query("update User u set u.lastname = ?1 where u.email = ?2")
    void updateLastName(String lastname,String email);

    @Modifying
    @Query("update User u set u.password = ?1 where u.email = ?2")
    void updatePassword(String password,String email);

    @Modifying
    @Query("update User u set u.email = ?1 where u.id = ?2")
    void updateEmail(String new_email,Long id);

    void deleteByEmail(String email);

    @Query("select c.user2.id from Connection c where c.user1.id = :userId and c.connection_status='Friends'")
    List<Long> findFriendsIdsByInitiatorId(@Param("userId") Long userId);

    @Query("select c.user1.id from Connection c where c.user2.id = :userId and c.connection_status='Friends'")
    List<Long> findFriendsIdsByAcceptorId(@Param("userId") Long userId);

    @Query("select p from Post p where p.user.id in :friendIds")
    List<Post> findPostsOfFriends(@Param("friendIds") List<Long> friendIds);

    @Query("select s from Share s where s.user.id in :friendIds")
    List<Share> findSharesOfFriends(@Param("friendIds") List<Long> friendIds);

    @Query("select p from Profile p where p.user.id in :friendIds")
    List<Profile> findProfileByUserId();


}
