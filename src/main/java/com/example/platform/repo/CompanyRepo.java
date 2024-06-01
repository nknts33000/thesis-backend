package com.example.platform.repo;

import com.example.platform.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepo extends JpaRepository<Company, Long> {
    Company findCompanyByCompanyId(Long companyId);


}
