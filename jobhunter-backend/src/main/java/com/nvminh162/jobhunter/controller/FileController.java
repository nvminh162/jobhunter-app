package com.nvminh162.jobhunter.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nvminh162.jobhunter.dto.file.ResUploadFileDTO;
import com.nvminh162.jobhunter.service.FileService;
import com.nvminh162.jobhunter.util.annotation.ApiMessage;
import com.nvminh162.jobhunter.util.error.StorageException;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

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
    public ResponseEntity<ResUploadFileDTO> upload(
            @RequestParam(name = "file", required = false) MultipartFile file, // This parameter is required
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {
        // skip validate (File is empty - File extensions - File size (max = 5MB))
        if (file == null || file.isEmpty()) {
            throw new StorageException("Chưa tải file, Vui lòng upload 1 file");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        // kiểm tra tên file có đứa hậu tố mở rộng trong List không => boolean
        boolean isValid = allowedExtensions
                .stream()
                .anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValid) {
            throw new StorageException("Không được phép tải file định dạng " + allowedExtensions.toString());
        }

        // create dir if not exist
        this.fileService.createDirectory(baseUri + folder);
        // storage file
        String uploadFile = this.fileService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/files")
    @ApiMessage("Download a file")
    public ResponseEntity<Resource> download(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder)
            throws StorageException, URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new StorageException("Miss tham số (fileName và folder)");
        }

        // check file exist (and not a dir)
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException("File name: " + fileName + " not found");
        }

        // download file
        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity
                .ok()
                // tự động set fileName khi download
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
