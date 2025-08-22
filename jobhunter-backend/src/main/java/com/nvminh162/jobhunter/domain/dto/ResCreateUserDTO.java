package com.nvminh162.jobhunter.domain.dto;

import java.time.Instant;

import com.nvminh162.jobhunter.domain.enumerate.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private ResUserCompanyDTO company;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResUserCompanyDTO {
        private long id;
        private String name;
    }
}
