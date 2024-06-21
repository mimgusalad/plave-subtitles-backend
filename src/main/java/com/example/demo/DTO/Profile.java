package com.example.demo.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class Profile {
    MultipartFile image;
    String userId;

    public Profile(MultipartFile image, String userId) {
        this.image = image;
        this.userId = userId;
    }
}
