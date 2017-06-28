package com.nguyencse.todo.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Putin on 4/13/2017.
 */

public class Task {
    private String key;
    private String date;
    private long time;
    private String name;
    private String detail;
    private String status;

    public Task() {
    }

    public Task(String key, String date, long time, String name, String detail, String status) {
        this.key = key;
        this.date = date;
        this.time = time;
        this.name = name;
        this.detail = detail;
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("date", date);
        result.put("time", time);
        result.put("name", name);
        result.put("detail", detail);
        result.put("status", status);
        return result;
    }
}