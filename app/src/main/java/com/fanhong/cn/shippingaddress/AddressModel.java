package com.fanhong.cn.shippingaddress;

/**
 * Created by Administrator on 2017/7/25.
 */

public class AddressModel {
    String name;
    String phone;
    String address;
    String cellId;
    String louId;
    int isDefault,adrid;

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getLouId() {
        return louId;
    }

    public void setLouId(String louId) {
        this.louId = louId;
    }

    public int getAdrid() {
        return adrid;
    }

    public void setAdrid(int adrid) {
        this.adrid = adrid;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return "AddressModel{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", isDefault=" + isDefault +
                ", adrid=" + adrid +
                '}';
    }
}
