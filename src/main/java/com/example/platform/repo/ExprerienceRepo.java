package com.example.platform.repo;

import com.example.platform.model.Experience;
import com.example.platform.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface ExprerienceRepo extends JpaRepository<Experience,Long> {

    @Query("SELECT e FROM Experience e where e.user=?1")
    List<Experience> getExperiencesOfUser(User user);

    @Query("select e from Experience e where e.experience_id=?1")
    Experience findExperienceByExperience_id(long experience_id);
}
