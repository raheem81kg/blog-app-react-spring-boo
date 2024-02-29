package com.blogapp.api.services.impl;

import com.blogapp.api.services.UploadImageService;
import com.google.api.client.util.Value;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UploadImageServiceImpl implements UploadImageService {
    @Value("${google.storage.bucketName}")
    private String bucketName;

    @Value("${google.storage.adminJsonPath}")
    private String adminJsonPath;

    private String uploadFile(File file, String fileName, String folderName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, folderName + "/" + fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").setAcl(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))).build();

        InputStream inputStream = UploadImageServiceImpl.class.getClassLoader().getResourceAsStream("admin.json");
        if (inputStream == null) {
            throw new IOException("admin.json not found");
        }

        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        // Get the public URL without expiration (object is now publicly readable)
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, folderName + "/" + fileName);
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }

        return tempFile;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public List<String> upload(MultipartFile[] multipartFiles, String folderName) {
        List<String> fileUrls = new ArrayList<>();
        try {
            for (MultipartFile file : multipartFiles) {
                String fileName = UUID.randomUUID().toString().concat(this.getExtension(file.getOriginalFilename()));
                File tempFile = this.convertToFile(file, fileName);
                String URL = this.uploadFile(tempFile, fileName, folderName);
                fileUrls.add(URL);
                tempFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrls;
    }

    public String upload(MultipartFile multipartFile, String folderName) {
        try {
            String fileName = UUID.randomUUID().toString().concat(this.getExtension(multipartFile.getOriginalFilename()));
            File tempFile = this.convertToFile(multipartFile, fileName);
            String URL = this.uploadFile(tempFile, fileName, folderName);
            tempFile.delete();
            return URL;
        } catch (Exception e) {
            e.printStackTrace();
            return "Image couldn't upload, Something went wrong";
        }
    }
}

