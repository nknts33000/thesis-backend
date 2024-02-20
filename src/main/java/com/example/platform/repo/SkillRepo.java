package com.example.platform.repo;

import com.example.platform.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepo extends JpaRepository<Skill,Long> {
}
