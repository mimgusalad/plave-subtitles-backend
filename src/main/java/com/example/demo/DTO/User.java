package com.example.demo.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private int userId;
    private String id;
    private String username;
    private String email; // id+email
    private String password;
}
