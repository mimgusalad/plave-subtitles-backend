package com.example.demo.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private int id;
    private String account;
    private String name;
    private String email; // id+email
    private String password;
}
