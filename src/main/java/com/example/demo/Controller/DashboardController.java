package com.example.demo.Controller;

import com.example.demo.DTO.Data;
import com.example.demo.Service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    DashboardService dashboardService;

    @GetMapping
    public ArrayList<Data> dashboard() throws IOException, InterruptedException {
        return dashboardService.getData();
    }
}
