package com.fanhong.cn.bean;

/**
 * Created by Administrator on 2017/6/30.
 */

public class CommunityMessageBean {
    private String headUrl = "";
    private String userName = "";
    private String message = "";
    private long msgTime = 0;
    private int type = TYPE_LEFT;
    public final static int TYPE_LEFT = 0;
    public final static int TYPE_RIGHT = 1;

    public CommunityMessageBean(String headUrl, String userName, String message, long msgTime, int type) {
        this.headUrl = headUrl;
        this.userName = userName;
        this.message = message;
        this.msgTime = msgTime;
        this.type = type;
    }

    public CommunityMessageBean(String userName, String message, int type) {
        headUrl = "http://www.ffpic.com/files/png/03008duihk/ffpic13061258679810.png";
        this.userName = userName;
        this.message = message;
        msgTime = System.currentTimeMillis();
        this.type = type;
    }

    public CommunityMessageBean() {
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
