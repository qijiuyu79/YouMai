package com.youmai.project.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论
 * Created by Administrator on 2018/9/1.
 */

public class Comment implements Serializable {
    private String id;
    //买家属性
    private int addCreditLevel;
    private String addHead;
    private String addNickname;
    private long createTime;
    private String evaluate;
    private String description;
    private List<String> imgList=new ArrayList<>();
    private double presentPrice;
    private int score;
    //买家属性
    private int remove_creditLevel;
    private String remove_nickname;
    private String remove_head;
    private List<String> comm_imgs=new ArrayList<>();
    private String replyContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAddCreditLevel() {
        return addCreditLevel;
    }

    public void setAddCreditLevel(int addCreditLevel) {
        this.addCreditLevel = addCreditLevel;
    }

    public String getAddHead() {
        return addHead;
    }

    public void setAddHead(String addHead) {
        this.addHead = addHead;
    }

    public String getAddNickname() {
        return addNickname;
    }

    public void setAddNickname(String addNickname) {
        this.addNickname = addNickname;
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

    public int getRemove_creditLevel() {
        return remove_creditLevel;
    }

    public void setRemove_creditLevel(int remove_creditLevel) {
        this.remove_creditLevel = remove_creditLevel;
    }

    public String getRemove_nickname() {
        return remove_nickname;
    }

    public void setRemove_nickname(String remove_nickname) {
        this.remove_nickname = remove_nickname;
    }

    public String getRemove_head() {
        return remove_head;
    }

    public void setRemove_head(String remove_head) {
        this.remove_head = remove_head;
    }

    public List<String> getComm_imgs() {
        return comm_imgs;
    }

    public void setComm_imgs(List<String> comm_imgs) {
        this.comm_imgs = comm_imgs;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
}
