package com.example.demo.Service;

import com.example.demo.DTO.Data;
import com.example.demo.DTO.RawData;
import com.example.demo.Utils.YoutubeExtractor;
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
public class DashboardService {
    @Value("${google.spreadsheet.sheetId}")
    private String sheetId;
    @Value("${google.spreadsheet.tabName}")
    private String tabName;
    ArrayList<Data> data;

    public ArrayList<Data> getData() throws IOException, InterruptedException {
        String json = getDatabaseFromSheet();
        data = deserialize(json);
        return data;
    }

    private String getDatabaseFromSheet() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String uri = "https://opensheet.elk.sh/" + sheetId + "/" + tabName;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
        return res.body();
    }

    private static ArrayList<Data> deserialize(String json) throws IOException, InterruptedException {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<RawData>>() {}.getType();
        ArrayList<RawData> list = gson.fromJson(json, listType);
        ArrayList<Data> dataList = new ArrayList<>();
        for (RawData rawData : list) {
            Data data = new Data();
            YoutubeExtractor yt = new YoutubeExtractor();
            data.setDate(rawData.getDate());
            data.setVideoId(rawData.getVideoId());
            data.setMembers(reformat(rawData.getMembers()));
            data.setThumbnail(rawData.getThumbnail().equals("y"));
            data.setTitle(yt.getTitle(data.getVideoId()));
            dataList.add(data);
        }
        return dataList;
    }

    private static ArrayList<String> reformat(String str){
        String[] arr = str.split(",");
        ArrayList<String> list = new ArrayList<>();
        for(String s : arr){
            list.add(s.trim());
        }
        return list;
    }
}
