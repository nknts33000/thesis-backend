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

    /*@Modifying
    @Query("update User u set u.firstname = ?1, u.lastname = ?2 where u.email = ?3")
    void setUserInfoById(String firstname, String lastname, Integer userId);*/
}
