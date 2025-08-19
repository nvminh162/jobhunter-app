package com.nvminh162.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.nvminh162.jobhunter.domain.Company;
import com.nvminh162.jobhunter.domain.dto.ResultPaginationDTO;
import com.nvminh162.jobhunter.service.CompanyService;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies") // <?> = any
    public ResponseEntity<?> createCompany(@Valid @RequestBody Company reqCompany) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleCreateCompany(reqCompany));
    }

    /* No Pagination */
    // @GetMapping("/companies")
    // public ResponseEntity<List<Company>> getCompanies() {
    //     List<Company> companies = this.companyService.handleGetAllCompanies();
    //     return ResponseEntity.ok(companies); // => mã 200 thay vì dùng cách trên
    // }

    /* Pagination return List */
    // @GetMapping("/companies")
    // public ResponseEntity<List<Company>> getCompanies(
    //         @RequestParam("current") Optional<String> currentOptional,
    //         @RequestParam("pageSize") Optional<String> pageSizeOptional) {
    //     // String
    //     String strCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
    //     String strPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
    //     // Convert String to Int
    //     int intCurrent = Integer.parseInt(strCurrent) - 1; // default lấy từ là 0, phía cần thực tế là 1
    //     int intPageSize = Integer.parseInt(strPageSize);
    //     Pageable pageable = PageRequest.of(intCurrent, intPageSize);

    //     /* Trả về List chưa format theo frontend yêu cầu */
    //     List<Company> companies = this.companyService.handleGetAllCompaniesListWithPagination(pageable);
    //     return ResponseEntity.ok(companies);
    // }

    /* Pagination return DTO - Unsorting */
    // @GetMapping("/companies")
    // public ResponseEntity<ResultPaginationDTO> getCompanies(
    //         @RequestParam("current") Optional<String> currentOptional,
    //         @RequestParam("pageSize") Optional<String> pageSizeOptional) {
    //     // String
    //     String strCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
    //     String strPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
    //     // Convert String to Int
    //     int intCurrent = Integer.parseInt(strCurrent) - 1; // default lấy từ là 0, phía user thực tế là trang 1
    //     int intPageSize = Integer.parseInt(strPageSize);
    //     Pageable pageable = PageRequest.of(intCurrent, intPageSize);

    //     /* Trả về DTO đã format theo frontend yêu cầu */
    //     ResultPaginationDTO companies = this.companyService.handleGetAllCompaniesDTOWithPagination(pageable);
    //     return ResponseEntity.ok(companies);
    // }

    /* Pagination return DTO - sorting */
    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getCompanies(@Filter Specification<Company> specification, Pageable pageable) {
        return ResponseEntity.ok(this.companyService.handleGetAllCompaniesDTOWithPaginationAndSorting(specification, pageable));
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company reqCompany) {
        Company updatedCompany = this.companyService.handleUpdateCompany(reqCompany);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") Long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok(null);
    }
}
