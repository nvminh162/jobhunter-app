package com.nvminh162.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nvminh162.jobhunter.domain.Job;
import com.nvminh162.jobhunter.dto.ResResultPaginationDTO;
import com.nvminh162.jobhunter.dto.job.ResCreateJobDTO;
import com.nvminh162.jobhunter.dto.job.ResUpdateJobDTO;
import com.nvminh162.jobhunter.service.JobService;
import com.nvminh162.jobhunter.util.annotation.ApiMessage;
import com.nvminh162.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/${api.version}")
public class JobController {
    private JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Created a job")
    public ResponseEntity<ResCreateJobDTO> createNewJob(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreateJob(job));
    }

    @PutMapping("jobs")
    @ApiMessage("Updated a job")
    public ResponseEntity<ResUpdateJobDTO> updateAJob(@Valid@RequestBody Job job) throws IdInvalidException {
        Job currentJob = this.jobService.handleGetJobById(job.getId());
        if(currentJob == null) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.handleUpdateJob(job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Deleted a job")
    public ResponseEntity<Void> deleteAJob(@PathVariable("id") long id) throws IdInvalidException {
        Job currentJob = this.jobService.handleGetJobById(id);
        if(currentJob == null) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get job by id")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) throws IdInvalidException {
        Job currentJob = this.jobService.handleGetJobById(id);
        if(currentJob == null) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.ok().body(currentJob);
    }

    @GetMapping("/jobs")
    public ResponseEntity<ResResultPaginationDTO> getAllJobs(
            @Filter Specification<Job> specification, Pageable pageable) {
        return ResponseEntity.ok().body(this.jobService.fetchAllJobs(specification, pageable));
    }
}
