package com.fanhong.cn.models;

/**
 * Created by Administrator on 2017/7/4.
 */

public class HomeNews {
    private String newsImage;
    private String newsTitle;
    private String newsWhere;
    private String newsTime;

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    private String newsUrl;

    public String getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(String newsImage) {
        this.newsImage = newsImage;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsWhere() {
        return newsWhere;
    }

    public void setNewsWhere(String newsWhere) {
        this.newsWhere = newsWhere;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }
}