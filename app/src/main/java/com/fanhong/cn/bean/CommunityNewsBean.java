package com.fanhong.cn.bean;

/**
 * Created by Administrator on 2017/6/30.
 */

public class CommunityNewsBean {
    private String news_photo = "";
    private String news_flag = "";
    private String tv_news_title = "";
    private String tv_news_from = "";
    private String tv_news_time = "";
    private String newsId = "";
    public static int TYPE_NEWS = 1;
    public static int TYPE_INFORM = 2;
    public static int TYPE_NOTICE = 3;
    public static int TYPE_ACTIVE = 4;

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public CommunityNewsBean() {
    }

    public CommunityNewsBean(String news_photo, String news_flag, String tv_news_title, String tv_news_from, String tv_news_time) {
        this.news_photo = news_photo;
        this.news_flag = news_flag;
        this.tv_news_title = tv_news_title;
        this.tv_news_from = tv_news_from;
        this.tv_news_time = tv_news_time;
    }

    public String getNews_photo() {
        return news_photo;
    }

    public void setNews_photo(String news_photo) {
        this.news_photo = news_photo;
    }

    public String getNews_flag() {
        return news_flag;
    }

    public void setNews_flag(String news_flag) {
        this.news_flag = news_flag;
    }

    public void setNews_flag(int type) {
        if (type == TYPE_NEWS)
            this.news_flag = "新闻";
        if (type == TYPE_INFORM)
            this.news_flag = "通知";
        if (type == TYPE_NOTICE)
            this.news_flag = "公告";
        if (type == TYPE_ACTIVE)
            this.news_flag = "活动";
    }

    public String getTv_news_title() {
        return tv_news_title;
    }

    public void setTv_news_title(String tv_news_title) {
        this.tv_news_title = tv_news_title;
    }

    public String getTv_news_from() {
        return tv_news_from;
    }

    public void setTv_news_from(String tv_news_from) {
        this.tv_news_from = tv_news_from;
    }

    public String getTv_news_time() {
        return tv_news_time;
    }

    public void setTv_news_time(String tv_news_time) {
        this.tv_news_time = tv_news_time;
    }
}
