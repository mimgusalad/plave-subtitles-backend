package com.example.demo.Controller;

import com.example.demo.DTO.Data;
import com.example.demo.Service.CloudflareR2Service;
import com.example.demo.Service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    DashboardService dashboardService;
    CloudflareR2Service cloudflareR2Service;

    @Autowired
    public DashboardController(DashboardService dashboardService, CloudflareR2Service cloudflareR2Service) {
        this.dashboardService = dashboardService;
        this.cloudflareR2Service = cloudflareR2Service;
    }

    @GetMapping
    public ArrayList<Data> dashboard() throws IOException, InterruptedException {
        return dashboardService.getDatabase();
    }

    @GetMapping("/subtitle")
    public ArrayList<String> listSubtitles(@RequestParam("videoId") String videoId){
        return cloudflareR2Service.listFiles(videoId);
    }

    @GetMapping("/search")
    public String findByKeyword(@RequestParam("keyword") String keyword) throws IOException, InterruptedException {
        return  dashboardService.findVideoID(keyword);
    }


}
