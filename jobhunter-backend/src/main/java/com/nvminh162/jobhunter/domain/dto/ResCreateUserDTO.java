package com.nvminh162.jobhunter.domain.dto;

import java.time.Instant;

import com.nvminh162.jobhunter.util.enumerate.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createAt;
}
