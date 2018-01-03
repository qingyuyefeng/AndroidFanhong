package com.fanhong.cn.models;

/**
 * Created by Administrator on 2017/12/27.
 */

public class Keymodel {
    String loudongName,key;
    int status;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLoudongName() {
        return loudongName;
    }

    public void setLoudongName(String loudongName) {
        this.loudongName = loudongName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Keymodel{" +
                "loudongName='" + loudongName + '\'' +
                ", key='" + key + '\'' +
                ", status=" + status +
                '}'+"\n";
    }
}
