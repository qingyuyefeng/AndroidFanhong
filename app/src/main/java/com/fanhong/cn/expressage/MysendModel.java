package com.fanhong.cn.expressage;

/**
 * Created by Administrator on 2017/8/10.
 */

public class MysendModel {
    String  sendCity,
            receiveCity,
            sendName,
            receiveName;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSendCity() {
        return sendCity;
    }

    public void setSendCity(String sendCity) {
        this.sendCity = sendCity;
    }

    public String getReceiveCity() {
        return receiveCity;
    }

    public void setReceiveCity(String receiveCity) {
        this.receiveCity = receiveCity;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    @Override
    public String toString() {
        return "MysendModel{" +
                "sendCity='" + sendCity + '\'' +
                ", receiveCity='" + receiveCity + '\'' +
                ", sendName='" + sendName + '\'' +
                ", receiveName='" + receiveName + '\'' +
                ", id=" + id +
                '}';
    }
}
