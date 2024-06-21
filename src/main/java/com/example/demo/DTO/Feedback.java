package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Feedback {
    String Timestamp;
    String VideoId;
    String Timecode;
    String Message;
}
