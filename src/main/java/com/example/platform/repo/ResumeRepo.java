package com.example.platform.repo;

import com.example.platform.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepo extends JpaRepository<Resume, Long> {
    List<Resume> findByAdvert_AdvertId(long advertId);

    Optional<Resume> findById(long id);
}