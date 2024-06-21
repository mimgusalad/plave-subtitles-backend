package com.example.demo.Service;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.DTO.Response;
import com.example.demo.DTO.User;
import com.example.demo.DTO.UserEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;

@Service
public class UserService{
    private final HttpClient client = HttpClient.newHttpClient();
    private final CloudflareR2Service cloudflareR2Service;

    @Value("${google.spreadsheet.sheetId}")
    private String sheetId;

    private ArrayList<UserEntity> userEntityTable;
    private UserEntity currentUserEntity;

    public UserService(CloudflareR2Service cloudflareR2Service) {
        this.cloudflareR2Service = cloudflareR2Service;
    }

    private synchronized void initializeUserTable() throws IOException, InterruptedException {
        if(userEntityTable == null){
            String tabName = "UserTable";
            String uri = "https://opensheet.elk.sh/"+sheetId + "/" + tabName;
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(uri)).build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            userEntityTable = deserialize(res.body());
        }
    }

    private ArrayList<UserEntity> deserialize(String res){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<UserEntity>>() {}.getType();
        return gson.fromJson(res, listType);
    }

    public User getCurrentLoggedUser() throws IOException {
        User user = new User();
        if(currentUserEntity == null ){ return null;}
        user.setId(currentUserEntity.getId());
        user.setUsername(currentUserEntity.getUsername());
        user.setEmail(currentUserEntity.getEmail());
        String filename = "profile/"+user.getId()+".png";
        String encodedImage = Base64.getEncoder().encodeToString(cloudflareR2Service.getImage(filename));
        user.setImage(encodedImage);
        return user;
    }

    private ResponseEntity<Response> findUser(LoginDTO loginDTO) {
        try {
            initializeUserTable();
            for (UserEntity userEntity : userEntityTable) {
                if (userEntity.getId().equals(loginDTO.getId())) {
                    if(userEntity.getPassword().equals(loginDTO.getPassword())){
                        currentUserEntity = userEntity;
                        return new ResponseEntity<>(new Response(true, null), HttpStatus.OK);
                    }else{
                        return new ResponseEntity<>(new Response(false, "Wrong password"), HttpStatus.UNAUTHORIZED);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            return new ResponseEntity<>(new Response(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new Response(false, "User not found"), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Response> processLogin(LoginDTO loginDTO){
        return findUser(loginDTO);
    }

    public ResponseEntity<Response> processLogout(){
        currentUserEntity = null;
        return new ResponseEntity<>(new Response(true, null), HttpStatus.OK);
    }
}
