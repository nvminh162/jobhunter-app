package com.nvminh162.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/${api.version}")
public class HelloController {

    @GetMapping("/")
    public String getHelloWorld() {
        return "Hello World, API from nvminh162!";
    }
}
