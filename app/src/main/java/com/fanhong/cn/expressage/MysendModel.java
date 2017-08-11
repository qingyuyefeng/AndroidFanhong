package com.fanhong.cn.expressage;

/**
 * Created by Administrator on 2017/8/10.
 */

public class MysendModel {
    String trackingNumber,
            sendCity,
            receiveCity,
            sendName,
            receiveName;

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
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
                "trackingNumber='" + trackingNumber + '\'' +
                ", sendCity='" + sendCity + '\'' +
                ", receiveCity='" + receiveCity + '\'' +
                ", sendName='" + sendName + '\'' +
                ", receiveName='" + receiveName + '\'' +
                '}';
    }
}
