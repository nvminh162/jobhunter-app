package com.nvminh162.jobhunter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*
 * uri: là URL để truy cập file (vd: file:D:/...)
 * path: sử dụng file trên máy tính
 */

@Service
public class FileService {

    @Value("${nvminh162.upload-file.base-uri}")
    private String baseUri;

    // create a folder if not exists
    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        // convert URI to Path
        Path path = Paths.get(uri);
        // Create new file
        File tmpDir = new File(path.toString());
        // nếu folder chưa tồn tại thì tạo mới
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + tmpDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS");
        }
    }

    // Save file to folder
    public String store(MultipartFile file, String folder) throws URISyntaxException, IOException {
        // create unique filename
        String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        URI uri = new URI(baseUri + folder + "/" + finalName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path,
                    StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }

    public long getFileLength(String fileName, String folder) throws URISyntaxException {
        URI uri = new URI(baseUri + folder + "/" + fileName);
        // convert URI to Path
        Path path = Paths.get(uri);
        // Create new file
        File tmpDir = new File(path.toString());

        // file không tồn tại, hoặc file là 1 dir => return 0
        if (!tmpDir.exists() || tmpDir.isDirectory()) {
            return 0;
        }
        return tmpDir.length();
    }

    public InputStreamResource getResource(String fileName, String folder)
            throws URISyntaxException, FileNotFoundException {
        URI uri = new URI(baseUri + folder + "/" + fileName);
        // convert URI to Path
        Path path = Paths.get(uri);
        // Create new file
        File file = new File(path.toString());
        return new InputStreamResource(new FileInputStream(file));
    }
}
