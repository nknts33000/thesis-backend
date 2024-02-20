package com.example.platform.repo;

import com.example.platform.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepo extends JpaRepository<Like,Long> {
}
