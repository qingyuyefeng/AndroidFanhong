package com.fanhong.cn.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/28.
 */

public class Cellmodel {
    String cellName;
    String details;
//    List<Keymodel> list = new ArrayList<>();
//
//    public List<Keymodel> getList() {
//        return list;
//    }
//
//    public void setList(List<Keymodel> list) {
//        this.list = list;
//    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Cellmodel{" +
                "cellName='" + cellName + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
