package com.example.demo.Controller;

import com.example.demo.DTO.*;
import com.example.demo.Service.CloudflareR2Service;
import com.example.demo.Service.UserService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@RestController
public class UserController {
    UserService userService;
    CloudflareR2Service cloudflareR2Service;

    @Autowired
    public UserController(UserService userService, CloudflareR2Service cloudflareR2Service) {
        this.userService = userService;
        this.cloudflareR2Service = cloudflareR2Service;
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginDTO loginDTO) {
       return userService.processLogin(loginDTO);
    }

    @GetMapping("currentUser")
    public User getCurrentUser() {
        return userService.getCurrentLoggedUser();
    }

    @PatchMapping("/profile")
    public ProfileDTO updateProfile(@RequestParam("file") MultipartFile file) throws IOException, InterruptedException {
        Gson gson = new Gson();
        ProfileDTO profileDTO = gson.fromJson(Arrays.toString(file.getBytes()), ProfileDTO.class);
        return profileDTO;
    }

}
