package com.nvminh162.jobhunter.domain.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResUpdateResumeDTO {
    private Instant updatedAt;
    private String updatedBy;
}
