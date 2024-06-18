package com.example.demo.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class CloudflareR2Service {
    @Autowired
    private AmazonS3 s3;
    private final String bucketName;

    public CloudflareR2Service(@Value("${cloudflare.r2.bucketName}") String bucketName) {
        this.bucketName = bucketName;
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

}
