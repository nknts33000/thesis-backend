package com.example.platform.repo;

import com.example.platform.model.Company;
import com.example.platform.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyRepo extends JpaRepository<Company, Long> {
    Company findCompanyByCompanyId(Long companyId);

    @Query("select p from Post p where p.company=?1")
    List<Post> findPostsOfCompany(Company company);
}
