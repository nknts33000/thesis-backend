package com.example.platform.repo;

import com.example.platform.model.Education;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface EducationRepo extends JpaRepository<Education,Long> {
}
