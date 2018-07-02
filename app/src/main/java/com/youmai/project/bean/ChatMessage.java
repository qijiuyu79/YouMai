package com.youmai.project.bean;

import java.io.Serializable;

/**
 * 聊天对象
 * Created by Administrator on 2017/11/8 0008.
 */

public class ChatMessage implements Serializable {

    /**
     * 聊天类型
     * 1：文字
     * 2：图片
     * 3:地图位置
     */
    private int type;

    private String text;

    private String imgPath;

    private double latitude;

    private double longitude;

    private String address;

    public ChatMessage(){};

    public ChatMessage(int type,String text){
        this.type=type;
        this.text=text;
    }

    public ChatMessage(int type,double latitude,double longitude,String address){
        this.type=type;
        this.latitude=latitude;
        this.longitude=longitude;
        this.address=address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
