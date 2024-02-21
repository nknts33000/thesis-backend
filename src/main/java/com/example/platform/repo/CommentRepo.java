package com.example.platform.repo;

import com.example.platform.model.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface CommentRepo extends JpaRepository<Comment,Long> {
}
