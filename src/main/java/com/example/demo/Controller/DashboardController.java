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
        return dashboardService.getData();
    }

}
