package com.example.demo.Service;

import com.example.demo.DTO.Data;
import com.example.demo.DTO.SubEntity;
import com.example.demo.DTO.Subtitle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

@Service
public class SubtitleService {
    private static StringBuilder sb;
    private static ArrayList<String> result;
    CloudflareR2Service cloudflareR2Service;
    DashboardService dashboardService;

    @Autowired
    public SubtitleService(CloudflareR2Service cloudflareR2Service, DashboardService dashboardService) {
        this.cloudflareR2Service = cloudflareR2Service;
        this.dashboardService = dashboardService;
    }

    public void insert(String videoId) throws IOException, InterruptedException {
        Data data = dashboardService.findData(videoId);
        SubEntity subEntity = new SubEntity();
        subEntity.setVideoId(data.getVideoId());
        subEntity.setMembers(data.getMembers().toString());
        subEntity.setThumbnail("https://i.ytimg.com/vi/" + data.getVideoId() + "/mqdefault.jpg");
        subEntity.setSheetName("SubTable");

        String scriptUrl = "https://script.google.com/macros/s/AKfycbyuxiqiuN3I36xRWimqBC4iNmmljB_6MoibVT3EYNDlYx0FUcP0A6jW457w9v_0R04Rsg/exec";
        // Convert SubEntity object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(subEntity);
        System.out.println(requestBody);

        // Create HttpClient instance
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        // Build the POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(scriptUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Send the POST request and handle the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status and print response body
        if (response.statusCode() == 200) {
            System.out.println("Successfully sent data to Google Apps Script.");
        } else {
            System.out.println("Failed to send data. Response code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        }
    }

    public InputStream getSubtitle(String videoId, String langCode){
        String filename = "subtitle/"+videoId+"/"+langCode+".json";
        InputStream inputStream = cloudflareR2Service.getFile(filename);
        return inputStream;

    }

    public String convert(MultipartFile file) throws IOException {
        srtToJson(file);
        InputStream inputStream = jsonToStream(result);
        String originalFilename = file.getOriginalFilename();
        String[] filename = originalFilename.split("\\.");
        String[] videoKey = filename[0].split("_");
        String lang = videoKey[1];
        String objectKey = "subtitle/"+videoKey[0]+"/"+lang+".json";
        System.out.println(objectKey);
        cloudflareR2Service.uploadJsonFile(objectKey, inputStream);
        return "success";
    }

    private static void srtToJson(MultipartFile file) throws IOException {
        readFile(file);
        parseContent();
    }

    private static InputStream jsonToStream(ArrayList<String> jsonContent){
        String jsonContentString = jsonContent.toString();
        return new ByteArrayInputStream(jsonContentString.getBytes());
    }

    private static void parseContent() {
        String[] srtBlocks = sb.toString().split("\n\n");
        Gson gson = new Gson();
        result = new ArrayList<>(srtBlocks.length);
        for(String srtBlock : srtBlocks) {
            String[] lines = srtBlock.split("\n");
            if(lines.length < 3) continue;
            String[] timecodes = lines[1].split(" --> ");
            if(timecodes.length < 2) continue;

            Subtitle jsonData = toJsonFormat(lines, timecodes);
            String json = gson.toJson(jsonData);
            result.add(json);
        }
    }
    private static void readFile(MultipartFile file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "utf-8"));
        sb = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            if ( line.isEmpty() ) sb.append("\n");
            else sb.append(line).append("\n");
        }
    }

    private static String srtFormatToSeconds(String timecode){
        String[] timeComponents = timecode.split("[:,]");
        float hour = Float.parseFloat(timeComponents[0]);
        float minute = Float.parseFloat(timeComponents[1]);
        float second = Float.parseFloat(timeComponents[2]);
        float millisecond = Float.parseFloat(timeComponents[3]);

        float totalSeconds = hour * 3600 + minute * 60 + second+millisecond/1000;
        return String.format("%.2f", totalSeconds);
    }

    private static Subtitle toJsonFormat(String[] lines, String[] timecodes){
        Subtitle subtitle = new Subtitle();
        subtitle.setNumber(lines[0]);
        subtitle.setStartTime(srtFormatToSeconds(timecodes[0]));
        subtitle.setEndTime(srtFormatToSeconds(timecodes[1]));
        subtitle.setText(lines[2]);
        return subtitle;
    }
}
