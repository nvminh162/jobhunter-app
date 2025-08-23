package com.nvminh162.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nvminh162.jobhunter.domain.Job;
import com.nvminh162.jobhunter.domain.Resume;
import com.nvminh162.jobhunter.domain.User;
import com.nvminh162.jobhunter.domain.dto.ResCreateResumeDTO;
import com.nvminh162.jobhunter.domain.dto.ResFetchResumeDTO;
import com.nvminh162.jobhunter.domain.dto.ResResultPaginationDTO;
import com.nvminh162.jobhunter.domain.dto.ResUpdateResumeDTO;
import com.nvminh162.jobhunter.repository.JobRepository;
import com.nvminh162.jobhunter.repository.ResumeRepository;
import com.nvminh162.jobhunter.repository.UserRepository;

@Service
public class ResumeService {
    private ResumeRepository resumeRepository;
    private UserRepository userRepository;
    private JobRepository jobRepository;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository,
            JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        // check user by ID
        if (resume.getUser() == null) {
            return false;
        }
        Optional<User> optionalUser = this.userRepository.findById(resume.getUser().getId());
        if (optionalUser.isEmpty()) {
            return false;
        }

        // check job by id
        if (resume.getJob() == null) {
            return false;
        }
        Optional<Job> optionalJob = this.jobRepository.findById(resume.getJob().getId());
        if (optionalJob.isEmpty()) {
            return false;
        }

        return true;
    }

    public ResCreateResumeDTO create(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        return res;
    }

    public Optional<Resume> fetchById(long id) {
        return this.resumeRepository.findById(id);
    }

    public ResUpdateResumeDTO update(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    public void delete(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResFetchResumeDTO getResume(Resume resume) {
        ResFetchResumeDTO res = new ResFetchResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreateBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        res.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        res.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        return res;
    }

    public ResResultPaginationDTO fetchAllResume(Specification<Resume> specification, Pageable pageable) {
        Page<Resume> page = this.resumeRepository.findAll(specification, pageable);
        ResResultPaginationDTO resultPaginationDTO = new ResResultPaginationDTO();
        ResResultPaginationDTO.Meta mt = new ResResultPaginationDTO.Meta();
        // Get from frontend send request
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        // Get from dbs
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        resultPaginationDTO.setMeta(mt);

        // remove senstive data
        List<ResFetchResumeDTO> listResume = page.getContent()
            .stream().map(item -> this.getResume(item))
            .collect(Collectors.toList());

        resultPaginationDTO.setResult(listResume);
        return resultPaginationDTO;
    }
}
