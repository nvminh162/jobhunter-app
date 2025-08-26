package com.nvminh162.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nvminh162.jobhunter.service.EmailService;
import com.nvminh162.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/${api.version}")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    @ApiMessage("Send test simple email")
    public String sendSimpleEmail() {
        emailService.sendSimpleEmail();
        return "OK";
    }
}
