package com.example.platform.repo;

import com.example.platform.model.Comment;
import com.example.platform.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface CommentRepo extends JpaRepository<Comment,Long> {

    @Query("SELECT c from Comment c where c.post.post_id=?1")
    List<Comment> getCommentFromPost(Post post);
}
