package com.example.demo.Service;

import com.example.demo.DTO.SubtitleDTO;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;

@Service
public class FileService {
    private static StringBuilder sb;
    private static ArrayList<String> result;
    @Autowired
    CloudflareR2Service cloudflareR2Service;

    public String convert(MultipartFile file) throws IOException {
        srtToJson(file);
        InputStream inputStream = jsonToStream(result);
        String originalFilename = file.getOriginalFilename();
        String[] filename = originalFilename.split("\\.");
        String objectKey = filename[0] + ".json";
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

            SubtitleDTO jsonData = toJsonFormat(lines, timecodes);
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

    private static SubtitleDTO toJsonFormat(String[] lines, String[] timecodes){
        SubtitleDTO subtitleDTO = new SubtitleDTO();
        subtitleDTO.setNumber(lines[0]);
        subtitleDTO.setStartTime(srtFormatToSeconds(timecodes[0]));
        subtitleDTO.setEndTime(srtFormatToSeconds(timecodes[1]));
        subtitleDTO.setText(lines[2]);
        return subtitleDTO;
    }
}
