package com.example.demo.Controller;

import com.amazonaws.util.IOUtils;
import com.example.demo.DTO.Response;
import com.example.demo.Service.CloudflareR2Service;
import com.example.demo.Service.DashboardService;
import com.example.demo.Service.SrtFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file")
public class FileController {
    private final SrtFileService srtFileService;
    private final CloudflareR2Service cloudflareR2Service;
    private final DashboardService dashboardService;

    @Autowired
    public FileController(SrtFileService srtFileService, CloudflareR2Service cloudflareR2Service, DashboardService dashboardService) {
        this.srtFileService = srtFileService;
        this.cloudflareR2Service = cloudflareR2Service;
        this.dashboardService = dashboardService;
    }

    @PostMapping
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile[] file) throws IOException {
        for (MultipartFile multipartFile : file) {
            srtFileService.convert(multipartFile);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ByteArrayResource> getFile(@RequestParam("filename") String filename) throws IOException {
        InputStream inputStream = cloudflareR2Service.getFile(filename);
        ByteArrayResource resource = new ByteArrayResource(IOUtils.toByteArray(inputStream));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
    }

    @DeleteMapping
    public ResponseEntity<Response> deleteFile(@RequestParam("langCode") String langCode, @RequestParam("videoId") String videoId) throws IOException {
        return dashboardService.deleteFile(videoId, langCode);
    }
}
