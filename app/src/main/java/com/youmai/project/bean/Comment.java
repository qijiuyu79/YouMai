package com.youmai.project.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论
 * Created by Administrator on 2018/9/1.
 */

public class Comment implements Serializable {
    private int creditLevel;
    private String head;
    private String nickname;
    private long createTime;
    private String evaluate;
    private String description;
    private List<String> imgList=new ArrayList<>();
    private double presentPrice;
    private int score;

    public int getCreditLevel() {
        return creditLevel;
    }

    public void setCreditLevel(int creditLevel) {
        this.creditLevel = creditLevel;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    public double getPresentPrice() {
        return presentPrice;
    }

    public void setPresentPrice(double presentPrice) {
        this.presentPrice = presentPrice;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
