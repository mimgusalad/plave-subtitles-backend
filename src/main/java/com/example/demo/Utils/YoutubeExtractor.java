package com.example.demo.Utils;

import com.example.demo.DTO.YoutubeDTO;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class YoutubeExtractor {
    static HttpClient httpClient;
    public YoutubeExtractor() {
        httpClient = HttpClient.newHttpClient();
    }
    public String getTitle(String videoId) throws IOException, InterruptedException {
        Gson gson = new Gson();
        String res = getYoutubeData(videoId);
        YoutubeDTO ytDTO = gson.fromJson(res, YoutubeDTO.class);
        String originalTitle = ytDTO.getTitle();
        String[] title = originalTitle.split("\\|");
        return title[0];
    }
    private static String getYoutubeData(String videoId) throws IOException, InterruptedException {
        String youtubeUrl = "https://www.youtube.com/watch?v=" + videoId;
        String api = "https://noembed.com/embed?dataType=json&url=" + youtubeUrl;
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(api)).GET().build();
        HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        return res.body();
    }
}
