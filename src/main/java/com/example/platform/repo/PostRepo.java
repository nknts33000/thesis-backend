package com.example.platform.repo;

import com.example.platform.model.Post;
import com.example.platform.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface PostRepo extends JpaRepository<Post,Long> {
    @Query("select p.user from Post p where p.post_id=?1")
    public List<User> getUserByPostId(long post_id);

    Post getPostById(long postId);
}
