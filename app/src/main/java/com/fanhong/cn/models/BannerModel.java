package com.fanhong.cn.models;

/**
 * Created by Administrator on 2017/7/20.
 */

public class BannerModel {

    private String imageUrl;
    private String link;

    public String getLink() {
        return link;
    }

    public BannerModel setLink(String tips) {
        link = tips;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BannerModel setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
}
