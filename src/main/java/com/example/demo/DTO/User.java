package com.example.demo.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private String id; // 프로필 사진 검색용
    private String username;
    private String email;
    private String image;
}
