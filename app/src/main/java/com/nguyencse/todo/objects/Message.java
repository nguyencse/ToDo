package com.nguyencse.todo.objects;

import java.util.HashMap;

/**
 * Created by Putin on 4/30/2017.
 */

public class Message {
    private String uID;
    private String body;
    private String username;
    private String email;
    private String time;
    private String avatar;

    public Message() {
    }

    public Message(String uID, String body, String username, String email, String time, String avatar) {
        this.uID = uID;
        this.body = body;
        this.username = username;
        this.email = email;
        this.time = time;
        this.avatar = avatar;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uID", uID);
        map.put("body", body);
        map.put("username", username);
        map.put("email", email);
        map.put("time", time);
        map.put("avatar", avatar);
        return map;
    }
}
