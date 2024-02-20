package com.example.platform.repo;

import com.example.platform.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepo extends JpaRepository<Group,Long> {
}
