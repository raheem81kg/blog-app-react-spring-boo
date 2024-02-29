package com.blogapp.api.controllers;

import com.blogapp.api.services.UploadImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final UploadImageService uploadImageService;

    @Autowired
    public UploadController(UploadImageService uploadImageService) {
        this.uploadImageService = uploadImageService;
    }

    @PostMapping("/postImages")
    public ResponseEntity<List<String>> uploadPostImages(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> fileUrls = uploadImageService.upload(files, "coverPic");
            return ResponseEntity.ok(fileUrls);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    @PostMapping("/profilePic")
    public ResponseEntity<String> uploadProfilePic(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = uploadImageService.upload(file, "profilePic");
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    @PostMapping("/coverPic")
    public ResponseEntity<String> uploadCoverPic(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = uploadImageService.upload(file, "coverPic");
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
}
