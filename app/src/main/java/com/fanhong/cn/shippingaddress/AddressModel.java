package com.fanhong.cn.shippingaddress;

/**
 * Created by Administrator on 2017/7/25.
 */

public class AddressModel {
    String name,phone,address;
    int isDefault,adrid;

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
