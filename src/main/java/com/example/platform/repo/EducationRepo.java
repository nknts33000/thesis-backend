package com.example.platform.repo;

import com.example.platform.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepo extends JpaRepository<Education,Long> {
}
