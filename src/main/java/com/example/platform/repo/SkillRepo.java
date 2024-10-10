package com.example.platform.repo;

import com.example.platform.model.Skill;
import org.hibernate.loader.ast.spi.SingleUniqueKeyEntityLoader;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface SkillRepo extends JpaRepository<Skill,Long> {
    @Query("select s from Skill s where s.skill_id=?1")
    Skill getSkillFromSkillId(long skill_id);

    @Query("select s from  Skill s where s.skill_name=?1 and s.profile.profile_id=?2")
    Skill getSkillFromSkillName(String skill_name,long profile_id);
}
