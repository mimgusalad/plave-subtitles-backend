package com.example.demo.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubtitleDTO {
    private String number;
    private String startTime;
    private String endTime;
    private String text;
}
