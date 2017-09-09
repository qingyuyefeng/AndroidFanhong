package com.fanhong.cn.repair;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/8.
 */

class OpenDoorBean implements Serializable{
    private String name="";//姓名
    private String phone="";//电话
    private String far="";//距离
    private String exp="";//开锁经验
    private String logo="";//图片地址

    public OpenDoorBean() {
    }

    public OpenDoorBean(String name, String phone, String far, String exp, String logo) {
        this.name = name;
        this.phone = phone;
        this.far = far;
        this.exp = exp;
        this.logo = logo;
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

    public String getFar() {
        return far;
    }

    public void setFar(String far) {
        this.far = far;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String toString() {
        return "OpenDoorBean{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", far='" + far + '\'' +
                ", exp='" + exp + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
