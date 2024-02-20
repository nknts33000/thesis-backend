package com.example.platform.repo;

import com.example.platform.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepo extends JpaRepository<Profile,Long> {
}
