package com.example.platform.repo;

import com.example.platform.model.Like;
import com.example.platform.model.Post;
import com.example.platform.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface LikeRepo extends JpaRepository<Like,Long> {

    @Query("select l from Like l where l.user=?1 and l.post=?2")
    Optional<Like> findLike(User user, Post p);


}
