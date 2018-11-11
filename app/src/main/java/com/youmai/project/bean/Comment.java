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
    private int s_creditLevel;
    private String s_nickname;
    private List<String> comm_imgs=new ArrayList<>();

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

    public int getS_creditLevel() {
        return s_creditLevel;
    }

    public void setS_creditLevel(int s_creditLevel) {
        this.s_creditLevel = s_creditLevel;
    }

    public String getS_nickname() {
        return s_nickname;
    }

    public void setS_nickname(String s_nickname) {
        this.s_nickname = s_nickname;
    }

    public List<String> getComm_imgs() {
        return comm_imgs;
    }

    public void setComm_imgs(List<String> comm_imgs) {
        this.comm_imgs = comm_imgs;
    }
}
