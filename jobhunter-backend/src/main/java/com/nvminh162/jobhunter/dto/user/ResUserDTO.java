package com.nvminh162.jobhunter.dto.user;

import java.time.Instant;

import com.nvminh162.jobhunter.domain.enumerate.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private long id;
    private String email;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
    private Instant createdAt;
    private ResUserCompanyDTO company;
    private ResRoleUserDTO role;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResUserCompanyDTO {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResRoleUserDTO {
        private long id;
        private String name;
    }
}
