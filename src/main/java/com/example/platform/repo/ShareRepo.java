package com.example.platform.repo;

import com.example.platform.model.Share;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareRepo extends JpaRepository<Share,Long> {
}
