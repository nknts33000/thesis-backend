package com.example.platform.repo;

import com.example.platform.model.Education;
import com.example.platform.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface EducationRepo extends JpaRepository<Education,Long> {

    @Query("select e from Education e where e.education_id=?1")
    Education findEducationByEducation_id(long education_id);

    @Query("select e from Education e where e.user=?1")
    List<Education> getEducationByUser(User user);
}
