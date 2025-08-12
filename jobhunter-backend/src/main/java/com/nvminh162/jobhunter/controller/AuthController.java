package com.nvminh162.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nvminh162.jobhunter.domain.dto.LoginDTO;
import com.nvminh162.jobhunter.domain.dto.RestLoginDTO;
import com.nvminh162.jobhunter.util.SecurityUtil;

import jakarta.validation.Valid;

@RestController
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<RestLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // Nap input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //create a token
        String accessToken = this.securityUtil.createToken(authentication);
        RestLoginDTO restLoginDTO = new RestLoginDTO();
        restLoginDTO.setAccessToken(accessToken);

        // nạp thông tin (nếu xử lý thành công) vào SecurityContext
        // SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok().body(restLoginDTO);
    }
}
