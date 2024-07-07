package com.example.platform.repo;

import com.example.platform.model.Message;
import com.example.platform.model.User;
import jakarta.transaction.Transactional;
import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findBySenderId(long senderId);
    List<Message> findByReceiverId(long receiverId);
    List<Message> findBySenderIdAndReceiverId(long senderId, long receiverId);

    @Query("select m from Message m where (m.sender=?1 and m.receiver=?2) or (m.sender=?2 and m.receiver=?1)")
    List<Message> getAllMessages(User sender, User receiver);

    // Find all distinct users who have had conversations with the given user
    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.receiver = :user " +
            "UNION " +
            "SELECT DISTINCT m.receiver FROM Message m WHERE m.sender = :user")
    List<User> findDistinctConversationUsers(User user);
}
