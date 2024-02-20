package com.example.platform.repo;

import com.example.platform.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepo extends JpaRepository<Post,Long> {
}
