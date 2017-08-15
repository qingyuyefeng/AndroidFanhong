package com.fanhong.cn.verification;

import android.os.Parcelable;

import java.io.Serializable;

import static com.baidu.location.h.j.P;

/**
 * Created by Administrator on 2017/8/14.
 */

public class CarOrderForm implements Serializable {
    private String id = "";
    private String name = "";//车主姓名
    private String phone = "";//联系电话
    private String licence = "";//车牌号
    private String type = "";//车辆类型
    private String engine = "";//发动机号（后四位）
    private String idCard = "";//身份证号
    private String address = "";//取车地址

    public CarOrderForm() {
    }

    public CarOrderForm(String id, String name, String phone, String licence, String type, String engine, String idcard, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.licence = licence;
        this.type = type;
        this.engine = engine;
        this.idCard = idcard;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
