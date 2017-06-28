package com.nguyencse.todo.objects;

import java.util.HashMap;

/**
 * Created by Putin on 5/2/2017.
 */

public class User {
    private String id;
    private String username;
    private String email;
    private String avatar;

    public User() {
    }

    public User(String id, String username, String email, String avatar) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("username", username);
        map.put("email", email);
        map.put("avatar", avatar);
        return map;
    }
}
