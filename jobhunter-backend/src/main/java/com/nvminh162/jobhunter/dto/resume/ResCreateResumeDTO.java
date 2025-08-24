package com.nvminh162.jobhunter.dto.resume;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateResumeDTO {
    private long id;
    private Instant createdAt;
    private String createdBy;
}
