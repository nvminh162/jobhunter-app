package com.nvminh162.jobhunter.service;

import org.springframework.stereotype.Service;

import com.nvminh162.jobhunter.domain.Company;
import com.nvminh162.jobhunter.repository.CompanyRespository;

@Service
public class CompanyService {
    private final CompanyRespository companyRespository;

    public CompanyService(CompanyRespository companyRespository) {
        this.companyRespository = companyRespository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRespository.save(company);
    }
}
