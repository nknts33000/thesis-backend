package com.example.platform.repo;

import com.example.platform.model.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface PostRepo extends JpaRepository<Post,Long> {
    @Query("select p.user from Post p where p.postId=?1")
    public List<User> getUserByPostId(long postId);

    Post findByPostId(long postId);


}
