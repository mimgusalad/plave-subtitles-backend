package com.example.demo.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileDTO {
    MultipartFile image;
    int userId;
}
