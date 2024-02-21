package com.example.platform.repo;

import com.example.platform.model.Like;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface LikeRepo extends JpaRepository<Like,Long> {
}
