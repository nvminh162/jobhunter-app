package com.nvminh162.jobhunter.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nvminh162.jobhunter.service.FileService;

@RestController
@RequestMapping("/api/${api.version}")
public class FileController {

    @Value("${nvminh162.upload-file.base-uri}")
    private String baseUri;

    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    public String upload(
            @RequestParam("file") MultipartFile file, // This parameter is required
            @RequestParam("folder") String folder) throws URISyntaxException, IOException {
        // skip validate
        // create dir if not exist
        this.fileService.createDirectory(baseUri + folder);
        // storage file
        this.fileService.store(file, folder);
        return file.getOriginalFilename() + " - " + folder;
    }
}
