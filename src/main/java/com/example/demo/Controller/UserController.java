package com.example.demo.Controller;

import com.example.demo.DTO.*;
import com.example.demo.Service.CloudflareR2Service;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public ResponseEntity<Response> login(@RequestBody Login login) {
       return userService.processLogin(login);
    }

    @GetMapping("/logout")
    public ResponseEntity<Response> logout() {
        return userService.processLogout();
    }

    @GetMapping("/user")
    public User getCurrentUser() throws IOException {
        return userService.getCurrentLoggedUser();
    }

    @PatchMapping("/profile")
    public ResponseEntity<Response> updateProfile(@RequestParam("image") MultipartFile file, @RequestParam("id") String userId) throws IOException {
        Profile profile = new Profile(file, userId);
        return cloudflareR2Service.uploadProfileImage(profile);
    }


}
