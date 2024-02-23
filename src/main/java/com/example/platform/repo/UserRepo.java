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

    @Modifying
    @Query("update User u set u.location = ?1,u.name=?2,u.password=?3 where u.email = ?4")
    void updateUser(String location,String name,String password,String email);

    @Modifying
    @Query("update User u set u.location = ?1 where u.email = ?2")
    void updateLocation(String location,String email);

    @Modifying
    @Query("update User u set u.name = ?1 where u.email = ?2")
    void updateName(String name,String email);

    @Modifying
    @Query("update User u set u.password = ?1 where u.email = ?2")
    void updatePassword(String password,String email);
}
