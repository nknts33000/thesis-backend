package com.example.platform.repo;

import com.example.platform.model.Connection;
import com.example.platform.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface ConnectionRepo extends JpaRepository<Connection,Long> {
    @Query("SELECT c FROM Connection c WHERE c.user1 = ?1 AND c.user2 = ?2")
    List<Connection> getConnetions(User user1,User user2);

    @Modifying
    @Query("UPDATE Connection c SET c.connection_status='Friends' WHERE c.user1=?1 AND c.user2=?2")
    void acceptRequest(User user1,User user2);

    @Modifying
    //@Query("UPDATE Connection c SET c.connection_status='Rejected' WHERE c.user1=?1 AND c.user2=?2")
    @Query("delete from Connection c WHERE c.user1=?1 AND c.user2=?2")
    void rejectRequest(User user1,User user2);
    @Modifying
    @Query("delete from Connection c WHERE c.user1=?1 AND c.user2=?2")
    void deleteFriend(User user1,User user2);
}
