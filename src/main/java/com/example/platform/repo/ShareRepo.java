package com.example.platform.repo;

import com.example.platform.model.Company;
import com.example.platform.model.Share;
import com.example.platform.model.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface ShareRepo extends JpaRepository<Share,Long> {

    List<Share> findShareByCompany(Company company);

    List<Share> findShareByUser(User user);
}
