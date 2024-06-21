package com.example.demo.Controller;

import com.example.demo.DTO.Feedback;
import com.example.demo.Service.FeedbackService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public ArrayList<Feedback> getFeedback() throws IOException, InterruptedException {
        return feedbackService.getData();
    }
}
