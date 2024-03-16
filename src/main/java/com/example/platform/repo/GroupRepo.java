package com.example.platform.repo;

import com.example.platform.model.Group;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface GroupRepo extends JpaRepository<Group,Long> {
}
