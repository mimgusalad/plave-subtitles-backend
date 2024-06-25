package com.example.demo.Service;

import com.example.demo.DTO.Data;
import com.example.demo.DTO.Response;
import com.example.demo.Utils.DateConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.ArrayList;

import static com.example.demo.Service.FeedbackService.getString;

@Service
public class DashboardService {
    @Value("${google.spreadsheet.sheetId}")
    private String sheetId;
    @Value("${google.spreadsheet.tabName}")
    private String tabName;
    ArrayList<Data> database;
    @Autowired
    private CloudflareR2Service cloudflareR2Service;

    public String findData(String keyword) throws IOException, InterruptedException {
        if( database == null )
            getDatabase();

        for(Data data : database){
            DateConverter converter = new DateConverter();
            String videoId = data.getVideoId();
            String date = converter.convertDateFormat(data.getDate());
            String title = data.getTitle();
            if(keyword.equals(videoId) || keyword.equals(date))
                return String.format("%s %s", date, data.getTitle());
        }
        return null;
    }

    public ArrayList<Data> getDatabase() throws IOException, InterruptedException {
        String json = getDatabaseFromSheet();
        database = deserialize(json);
        return database;
    }

    private String getDatabaseFromSheet() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        String scriptUrl = "https://script.google.com/macros/s/AKfycbwhoo5Z0heiD3zW6pc3bLqjnt2NLPaPPEDCdX_YSfxwuyS4uW5yOYH3O2g1QDBYyX3m6A/exec";
        String urlWithParams = scriptUrl +"?sheetName=Database";
        return getString(client, urlWithParams);
    }

    private ArrayList<Data> deserialize(String json) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String[]> list = objectMapper.readValue(json, new TypeReference<ArrayList<String[]>>() {});
        ArrayList<Data> dataList = new ArrayList<>();
        for(String[] row : list){
            Data data = new Data();
            data.setDate(row[0].split("T")[0]);
            data.setVideoId(row[1]);
            data.setTitle(row[2]);
            data.setMembers(reformat(row[3]));
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

    public ResponseEntity<Response> deleteFile(String videoId, String langCode){
        String objectKey = "subtitle/"+videoId+"/"+langCode+".json";
        return cloudflareR2Service.deleteFile(objectKey);
    }
}
