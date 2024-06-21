package com.example.demo.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public ArrayList<String> listFiles(String videoId){
        try{
            ArrayList<String> result = new ArrayList<>();
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
            listObjectsRequest.setBucketName(bucketName);
            listObjectsRequest.setPrefix("");
            ObjectListing objectListing = s3.listObjects(listObjectsRequest);
            String regex = "subtitle/.+/([a-z]{2})\\.json$";
            Pattern pattern = Pattern.compile(regex);

            for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()){
                String key = objectSummary.getKey();
                if(key.matches("subtitle/"+videoId+"/.*")){
                    Matcher matcher = pattern.matcher(key);
                    if(matcher.matches()){
                        String langCode = matcher.group(1);
                        result.add(langCode);
                    }
                }
            }
            return result;
        }catch(Exception e){
            throw new RuntimeException("Failed to list files", e);
        }
    }

    ResponseEntity<Response> deleteFile(String fileName) {
        try {
            s3.deleteObject(bucketName, fileName);
            return new ResponseEntity<>(new Response(true, "File deleted successfully"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new Response(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
