package com.fanhong.cn.expressage;

/**
 * Created by Administrator on 2017/9/11.
 */

public class NetphoneModel {
    String name,phone;

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

    @Override
    public String toString() {
        return "NetphoneModel{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
