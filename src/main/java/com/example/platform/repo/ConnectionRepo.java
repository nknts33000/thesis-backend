package com.example.platform.repo;

import com.example.platform.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionRepo extends JpaRepository<Connection,Long> {
}
