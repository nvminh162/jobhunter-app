package com.nvminh162.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nvminh162.jobhunter.domain.Company;
import com.nvminh162.jobhunter.domain.dto.Meta;
import com.nvminh162.jobhunter.domain.dto.ResultPaginationDTO;
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

    /*
     * Page<T> findAll(Pageable pageable)
     * Để convert qua List<Company>
     * findAll(Pageable pageable).getContent();
     */
    // Trả về List chưa format theo frontend yêu cầu
    public List<Company> handleGetAllCompaniesListWithPagination(Pageable pageable) {
        return this.companyRespository.findAll(pageable).getContent();
    }
    // Trả về DTO đã format theo frontend yêu cầu
    public ResultPaginationDTO handleGetAllCompaniesDTOWithPagination(Pageable pageable) {
        Page<Company> companyPage = this.companyRespository.findAll(pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta mt = new Meta();
        // Get from frontend send request
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        // Get from dbs
        mt.setPages(companyPage.getTotalPages());
        mt.setTotal(companyPage.getTotalElements());
        resultPaginationDTO.setMeta(mt);
        resultPaginationDTO.setResult(companyPage.getContent());
        return resultPaginationDTO;
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> companyOptional = this.companyRespository.findById(company.getId());
        if (companyOptional.isPresent()) {
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
