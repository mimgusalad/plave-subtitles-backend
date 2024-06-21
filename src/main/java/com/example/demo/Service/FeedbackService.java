package com.example.demo.Service;

import com.example.demo.DTO.Feedback;
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
        HttpClient client = HttpClient.newHttpClient();
        String tabName = "Page1";
        String uri = "https://opensheet.elk.sh/" + sheetId + "/" + tabName;
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(uri)).GET().build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return res.body();
    }

    private ArrayList<Feedback> deserialize(String json){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Feedback>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}
