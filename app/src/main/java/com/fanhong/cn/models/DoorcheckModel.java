package com.fanhong.cn.models;

/**
 * Created by Administrator on 2017/6/19.
 */

public class DoorcheckModel {
    String cellName;
    String louNumber;

    public String getLouNumber() {
        return louNumber;
    }

    public void setLouNumber(String louNumber) {
        this.louNumber = louNumber;
    }

    String miyue;
    int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public String getMiyue() {
        return miyue;
    }

    public void setMiyue(String miyue) {
        this.miyue = miyue;
    }
}
