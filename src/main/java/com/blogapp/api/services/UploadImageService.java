package com.blogapp.api.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface UploadImageService {
    private String uploadFile(File file, String fileName, String folderName) throws IOException {
        return null;
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        return null;
    }

    private String getExtension(String fileName) {
        return null;
    }

    List<String> upload(MultipartFile[] multipartFiles, String folderName);

    String upload(MultipartFile multipartFile, String folderName);
}
