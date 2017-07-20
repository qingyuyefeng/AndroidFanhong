package com.fanhong.cn.bean;

/**
 * Created by Administrator on 2017/7/19.
 */

public class HousekeepingRecommendBean {
    private String imgUrl = "";
    private String title = "";
    private String price = "";
    private String count = "";
    private String reputation = "";

    public HousekeepingRecommendBean() {
    }

    public HousekeepingRecommendBean(String imgUrl, String title, String price, String count, String reputation) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.price = price;
        this.count = count;
        this.reputation = reputation;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getReputation() {
        return reputation;
    }

    public void setReputation(String reputation) {
        this.reputation = reputation;
    }
}
