package com.example.demo.Controller;

import com.amazonaws.util.IOUtils;
import com.example.demo.Service.CloudflareR2Service;
import com.example.demo.Service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    private final CloudflareR2Service cloudflareR2Service;
    @Autowired
    public FileController(FileService fileService, CloudflareR2Service cloudflareR2Service) {
        this.fileService = fileService;
        this.cloudflareR2Service = cloudflareR2Service;
    }

    @PostMapping
    public String uploadFile(@RequestParam("file") MultipartFile[] file) throws IOException {
        for (int i = 0; i < file.length; i++) {
            System.out.println(file[i].getOriginalFilename());
        }
        return fileService.convert(file[0]);
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        cloudflareR2Service.uploadFile(file);
        return file.getOriginalFilename();
    }

    @GetMapping
    public ResponseEntity<ByteArrayResource> getFile(@RequestParam("filename") String filename) throws IOException {
        InputStream inputStream = cloudflareR2Service.getFile(filename);
        ByteArrayResource resource = new ByteArrayResource(IOUtils.toByteArray(inputStream));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
    }

//    @GetMapping
//    public ResponseEntity<Resource> getFiles(@RequestParam("filename") String objectKey) {
//        InputStream inputStream = cloudflareR2Service.getFile(objectKey);
//        InputStreamResource resource = new InputStreamResource(inputStream);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + objectKey);
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }
}
