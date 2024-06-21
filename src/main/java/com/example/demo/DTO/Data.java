package com.example.demo.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Data {
    String date;
    String videoId;
    List<String> members;
    String title;
    List<String> subtitles;
}
