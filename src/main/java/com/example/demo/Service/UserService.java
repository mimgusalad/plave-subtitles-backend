package com.example.demo.Service;

import com.example.demo.DTO.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

@Service
public class UserService {
    private final HttpClient client = HttpClient.newHttpClient();

    @Value("${google.spreadsheet.sheetId}")
    private String sheetId;

    private ArrayList<User> userTable;

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

    public User findUser(String account) {
        try {
            initializeUserTable();
            for (User user : userTable) {
                if (user.getAccount().equals(account)) {
                    return user;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            return userDetails.getUsername();
        } else {
            return principal.toString();
        }
    }



}
