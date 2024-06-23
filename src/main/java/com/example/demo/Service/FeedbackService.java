package com.example.demo.Service;

import com.example.demo.DTO.Data;
import com.example.demo.DTO.Feedback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

@Service
public class FeedbackService {
    @Value("${google.spreadsheet.sheetId}")
    private String sheetId;

    public ArrayList<Feedback> getData() throws IOException, InterruptedException {
        String json = getDatabaseFromSheet();
        return deserialize(json);
    }

    private String getDatabaseFromSheet() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        String scriptUrl = "https://script.google.com/macros/s/AKfycbwhoo5Z0heiD3zW6pc3bLqjnt2NLPaPPEDCdX_YSfxwuyS4uW5yOYH3O2g1QDBYyX3m6A/exec";
        String urlWithParams = scriptUrl +"?sheetName=Feedback";
        return getString(client, urlWithParams);
    }

    static String getString(HttpClient client, String urlWithParams) throws InterruptedException {
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(urlWithParams)).GET().build();
        try{
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return res.body();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Feedback> deserialize(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String[]> list = objectMapper.readValue(json, new TypeReference<ArrayList<String[]>>() {});
        ArrayList<Feedback> fbList = new ArrayList<>();
        for(String[] row : list){
            Feedback fb = new Feedback();
            fb.setTimestamp(row[0]);
            fb.setVideoId(row[1]);
            fb.setTimecode(row[2]);
            fb.setMessage(row[3]);
            fbList.add(fb);

        }
        return fbList;
    }
}
