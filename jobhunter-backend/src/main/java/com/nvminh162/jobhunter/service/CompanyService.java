package com.nvminh162.jobhunter.service;

import java.util.List;
import java.util.Optional;

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

    public List<Company> handleGetAllCompanies() {
        return this.companyRespository.findAll();
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> companyOptional = this.companyRespository.findById(company.getId());
        if(companyOptional.isPresent()) {
            Company currentCompany = companyOptional.get();
            currentCompany.setName(company.getName());
            currentCompany.setDescription(company.getDescription());
            currentCompany.setAddress(company.getAddress());
            currentCompany.setLogo(company.getLogo());
            return this.companyRespository.save(currentCompany);
        }
        return null;
    }

    public void handleDeleteCompany(Long id) {
        this.companyRespository.deleteById(id);
    }
}
