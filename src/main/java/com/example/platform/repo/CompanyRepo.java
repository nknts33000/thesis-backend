package com.example.platform.repo;

import com.example.platform.model.Company;
import com.example.platform.model.Post;
import com.example.platform.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CompanyRepo extends JpaRepository<Company, Long> {
    Company findCompanyByCompanyId(Long companyId);

    @Query("select p from Post p where p.company=?1")
    List<Post> findPostsOfCompany(Company company);

    @Query("select c.companyLogo from Company c where c.companyId = ?1")
    Optional<byte[]> findLogoById(long companyId);

    @Modifying
    @Query("update Company c SET c.companyLogo=?1 where c.companyId=?2")
    void updateComLogo(byte[] companyLogo,long companyId);
}
