package com.nvminh162.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.nvminh162.jobhunter.domain.Company;

@Repository
public interface CompanyRespository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {}
