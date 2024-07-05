package com.example.platform.repo;

import com.example.platform.model.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findBySenderId(long senderId);
    List<Message> findByReceiverId(long receiverId);
    List<Message> findBySenderIdAndReceiverId(long senderId, long receiverId);
}
