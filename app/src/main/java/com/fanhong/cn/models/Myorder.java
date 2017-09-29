package com.fanhong.cn.models;

/**
 * Created by Administrator on 2017/9/26.
 */

public class Myorder {
    String goodsPicture;
    String goodsName;
    String goodsPrice;
    String goodsTime;
    int status;


//    public Myorder(String goodsPicture, String goodsName, String goodsPrice, String goodsTime, int status) {
//        this.goodsPicture = goodsPicture;
//        this.goodsName = goodsName;
//        this.goodsPrice = goodsPrice;
//        this.goodsTime = goodsTime;
//        this.status = status;
//    }

    public String getGoodsPicture() {
        return goodsPicture;
    }

    public void setGoodsPicture(String goodsPicture) {
        this.goodsPicture = goodsPicture;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsTime() {
        return goodsTime;
    }

    public void setGoodsTime(String goodsTime) {
        this.goodsTime = goodsTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
