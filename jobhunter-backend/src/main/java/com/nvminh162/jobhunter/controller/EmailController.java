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

    @GetMapping("/send-simple-email")
    @ApiMessage("Send test simple email")
    public String sendSimpleEmail() {
        emailService.sendSimpleEmail();
        return "OK";
    }

    @GetMapping("/send-email-sync")
    @ApiMessage("Send Email Sync")
    public String sendEmailSync() {
        emailService.sendEmailSync(
            "nvminh162@gmail.com",
            "Test send email",
            "<h1><b>Hello</b></h1>",
            false,
            true
        );
        return "OK";
    }

    @GetMapping("/send-email-from-template-sync")
    @ApiMessage("Send Email Sync")
    public String sendEmailFromTemplateSync() {
        emailService.sendEmailFromTemplateSync(
            "nvminh162@gmail.com", "Test send email with template", "jobs"); //test in src\main\resources\templates\test.html
        return "OK";
    }
}
