package com.fanhong.cn.models;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */

public class Homelife {
    private List<String> strings;
    private String title;
    private String place;
    private String time;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Homelife{" +
                "strings=" + strings +
                ", title='" + title + '\'' +
                ", place='" + place + '\'' +
                ", time='" + time + '\'' +
                ", url='" + url + '\'' +
                ", type=" + type +
                '}';
    }
}
