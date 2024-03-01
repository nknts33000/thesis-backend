package com.example.platform.repo;

import com.example.platform.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
}
