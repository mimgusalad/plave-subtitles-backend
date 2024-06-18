package com.example.demo.Controller;

import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    UserService userService;

    @PostMapping
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        userService.validateUser(username, password);
        return "redirect:/";
    }
}
