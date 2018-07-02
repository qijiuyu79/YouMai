package com.youmai.project.bean;

/**
 * Created by Administrator on 2018/5/17 0017.
 */

public class Login extends HttpBaseBean {

    private LoginBean data;

    public LoginBean getData() {
        return data;
    }

    public void setData(LoginBean data) {
        this.data = data;
    }

    public static class LoginBean{
        private String access_token;
        private String auth_token;
        private String user_id;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getAuth_token() {
            return auth_token;
        }

        public void setAuth_token(String auth_token) {
            this.auth_token = auth_token;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }
}
