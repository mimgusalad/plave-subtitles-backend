package com.example.demo.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.example.demo.DTO.Profile;
import com.example.demo.DTO.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class CloudflareR2Service {
    @Autowired
    private AmazonS3 s3;
    private final String bucketName;

    public CloudflareR2Service(@Value("${cloudflare.r2.bucketName}") String bucketName) {
        this.bucketName = bucketName;
    }

    public ResponseEntity<Response> uploadProfileImage(Profile profile) {
        try {
            String fileName = "profile/"+profile.getUserId()+".png";
            s3.putObject(bucketName, fileName, profile.getImage().getInputStream(), null);
            return new ResponseEntity<>(new Response(true, "Image uploaded successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void uploadFile(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            s3.putObject(bucketName, fileName, file.getInputStream(), null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public void uploadJsonFile(String filename, InputStream inputStream) {
        try{
            s3.putObject(bucketName, filename, inputStream, null);
        }catch(Exception e){
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public InputStream getFile(String fileName) {
        try {
            S3Object s3Object = s3.getObject(bucketName, fileName);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file", e);
        }
    }

    public byte[] getImage(String fileName) throws IOException {
        try {
            S3Object s3Object = s3.getObject(bucketName, fileName);
            return s3Object.getObjectContent().readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file", e);
        }
    }

    // 삭제하는거 만들어야할듯

}
