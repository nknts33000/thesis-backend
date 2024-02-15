package com.example.platform.repo;

import com.example.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {

    User findUserByUsername(String username);
}
