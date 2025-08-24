package com.nvminh162.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import com.nvminh162.jobhunter.domain.Skill;
import com.nvminh162.jobhunter.dto.ResResultPaginationDTO;
import com.nvminh162.jobhunter.service.SkillService;
import com.nvminh162.jobhunter.util.annotation.ApiMessage;
import com.nvminh162.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/${api.version}")
public class SkillController {
    private SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Created new a skill")
    public ResponseEntity<Skill> createNewSkill(@Valid @RequestBody Skill reqSkill) throws IdInvalidException {
        boolean isSkillExist = this.skillService.isNameExist(reqSkill.getName());
        if (isSkillExist) {
            throw new IdInvalidException("Skill " + reqSkill.getName() + " đã tồn tại.");
        }
        return ResponseEntity.ok().body(this.skillService.handleCreateSkill(reqSkill));
    }

    @PutMapping("/skills")
    @ApiMessage("Updated a skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill reqSkill) throws IdInvalidException {
        // check id
        Skill findSkill = this.skillService.handleGetSkillById(reqSkill.getId());
        if (findSkill == null) {
            throw new IdInvalidException("ID Skill: " + reqSkill.getId() + " không tồn tại.");
        }

        // check name
        boolean isSkillExist = this.skillService.isNameExist(reqSkill.getName());
        if (isSkillExist) {
            throw new IdInvalidException("Tên Skill " + reqSkill.getName() + " đã tồn tại.");
        }

        findSkill.setName(reqSkill.getName());
        return ResponseEntity.ok().body(this.skillService.handleUpdateSkill(findSkill));
    }

    @GetMapping("/skills")
    public ResponseEntity<ResResultPaginationDTO> getAll(
            @Filter Specification<Skill> specification, Pageable pageable) {
        return ResponseEntity.ok().body(this.skillService.fetchAllSkills(specification, pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        // check id
        Skill findSkill = this.skillService.handleGetSkillById(id);
        if (findSkill == null) {
            throw new IdInvalidException("Skill ID: " + "" + " không tồn tại");
        }
        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.ok().body(null);
    }
}
