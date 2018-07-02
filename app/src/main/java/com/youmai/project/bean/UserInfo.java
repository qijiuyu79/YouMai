package com.youmai.project.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/1 0001.
 */

public class UserInfo extends HttpBaseBean {

    private UserInfoBean data;

    public UserInfoBean getData() {
        return data;
    }

    public void setData(UserInfoBean data) {
        this.data = data;
    }

    public static class UserInfoBean implements Serializable{
        private String head;
        private String nickname;
        private String user_id;

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

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }
}
