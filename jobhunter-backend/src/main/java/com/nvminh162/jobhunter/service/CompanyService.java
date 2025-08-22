package com.nvminh162.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nvminh162.jobhunter.domain.Company;
import com.nvminh162.jobhunter.domain.User;
import com.nvminh162.jobhunter.domain.dto.ResResultPaginationDTO;
import com.nvminh162.jobhunter.repository.CompanyRespository;
import com.nvminh162.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRespository companyRespository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRespository companyRespository, UserRepository userRepository) {
        this.companyRespository = companyRespository;
        this.userRepository = userRepository;
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
    // Trả về List chưa format theo frontend yêu cầu - Unsorting
    public List<Company> handleGetAllCompaniesListWithPagination(Pageable pageable) {
        return this.companyRespository.findAll(pageable).getContent();
    }
    // Trả về DTO đã format theo frontend yêu cầu
    public ResResultPaginationDTO handleGetAllCompaniesDTOWithPagination(Pageable pageable) {
        Page<Company> companyPage = this.companyRespository.findAll(pageable);
        ResResultPaginationDTO resultPaginationDTO = new ResResultPaginationDTO();
        ResResultPaginationDTO.Meta mt = new ResResultPaginationDTO.Meta();
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

    // Trả về DTO đã format theo frontend yêu cầu - Sorting
    public ResResultPaginationDTO handleGetAllCompaniesDTOWithPaginationAndSorting(Specification<Company> specification, Pageable pageable) {
        Page<Company> companyPage = this.companyRespository.findAll(specification, pageable);
        ResResultPaginationDTO resultPaginationDTO = new ResResultPaginationDTO();
        ResResultPaginationDTO.Meta mt = new ResResultPaginationDTO.Meta();
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
        Optional<Company> optionalCompany = this.companyRespository.findById(id);
        if(optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            // Fetch all user belong to this company
            List<User> users = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(users);
        }

        this.companyRespository.deleteById(id);
    }
}
