package com.example.demo.Service;

import com.example.demo.DTO.User;
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
public class UserService{
    private final HttpClient client = HttpClient.newHttpClient();

    @Value("${google.spreadsheet.sheetId}")
    private String sheetId;

    private ArrayList<User> userTable;
    private User currentUser;

    private synchronized void initializeUserTable() throws IOException, InterruptedException {
        if(userTable == null){
            String tabName = "UserTable";
            String uri = "https://opensheet.elk.sh/"+sheetId + "/" + tabName;
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(uri)).build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            userTable = deserialize(res.body());
        }
    }

    private ArrayList<User> deserialize(String res){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<User>>() {}.getType();
        return gson.fromJson(res, listType);
    }

    public User findUser(String id, String password) {
        try {
            initializeUserTable();
            for (User user : userTable) {
                if (user.getId().equals(id)) {
                    if(user.getPassword().equals(password)){
                        currentUser = user;
                        return user;
                    }else{
                        throw new Error("Wrong password");
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public User findUser(String id) {
        try {
            initializeUserTable();
            for (User user : userTable) {
                if (user.getId().equals(id)) {
                    return user;
                }else{
                    throw new Error("User not exists");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
