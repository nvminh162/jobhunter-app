package com.nvminh162.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nvminh162.jobhunter.domain.Company;
import com.nvminh162.jobhunter.domain.Job;
import com.nvminh162.jobhunter.domain.Resume;
import com.nvminh162.jobhunter.domain.User;
import com.nvminh162.jobhunter.dto.ResResultPaginationDTO;
import com.nvminh162.jobhunter.dto.resume.ResCreateResumeDTO;
import com.nvminh162.jobhunter.dto.resume.ResFetchResumeDTO;
import com.nvminh162.jobhunter.dto.resume.ResUpdateResumeDTO;
import com.nvminh162.jobhunter.service.ResumeService;
import com.nvminh162.jobhunter.service.UserService;
import com.nvminh162.jobhunter.util.SecurityUtil;
import com.nvminh162.jobhunter.util.annotation.ApiMessage;
import com.nvminh162.jobhunter.util.error.IdInvalidException;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@RestController
@RequestMapping("/api/${api.version}")
public class ResumeController {
    @Autowired
    FilterBuilder filterBuilder;
    @Autowired
    FilterSpecificationConverter filterSpecificationConverter;

    private final ResumeService resumeService;
    private final UserService userService;

    public ResumeController(ResumeService resumeService, UserService userService) {
        this.resumeService = resumeService;
        this.userService = userService;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume) throws IdInvalidException {
        // Check id exists
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!isIdExist) {
            throw new IdInvalidException("User ID/ Job ID không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume resume) throws IdInvalidException {
        // check id exist
        Optional<Resume> optionalResume = this.resumeService.fetchById(resume.getId());
        if (optionalResume.isEmpty()) {
            throw new IdInvalidException("Resume ID: " + resume.getId() + " không tồn tại");
        }
        Resume reqResume = optionalResume.get();
        reqResume.setStatus(resume.getStatus());
        return ResponseEntity.ok().body(this.resumeService.update(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> optionalResume = this.resumeService.fetchById(id);
        if (optionalResume.isEmpty()) {
            throw new IdInvalidException("Resume ID: " + id + " không tồn tại");
        }
        this.resumeService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch a resume by id")
    public ResponseEntity<ResFetchResumeDTO> getMethodName(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> optionalResume = this.resumeService.fetchById(id);
        if (optionalResume.isEmpty()) {
            throw new IdInvalidException("Resume ID: " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.resumeService.getResume(optionalResume.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume with paginate")
    public ResponseEntity<ResResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> specification,
            Pageable pageable) {
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : null;
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && !companyJobs.isEmpty()) {
                    arrJobIds = companyJobs
                            .stream()
                            .map(companyJob -> companyJob.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(
                filterBuilder.field("job").in(filterBuilder.input(arrJobIds)).get());
        Specification<Resume> finalSpec = jobInSpec.and(specification);

        return ResponseEntity.ok().body(this.resumeService.fetchAllResume(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }
}
