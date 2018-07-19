package com.youmai.project.http;

import android.os.Handler;

import com.youmai.project.bean.HttpBaseBean;
import com.youmai.project.bean.Login;
import com.youmai.project.bean.UserInfo;
import com.youmai.project.http.base.BaseRequst;
import com.youmai.project.http.base.Http;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2018/5/17 0017.
 */

public class HttpMethod extends BaseRequst {


    /**
     * 获取短信验证码
     * @param mobile
     * @param handler
     */
    public static void sendCode(String mobile,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile",mobile);
        Http.getRetrofit().create(HttpApi.class).sendCode(map).enqueue(new Callback<HttpBaseBean>() {
            public void onResponse(Call<HttpBaseBean> call, Response<HttpBaseBean> response) {
                try {
                    sendMessage(handler, HandlerConstant.SEND_CODE_SUCCESS, response.body());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<HttpBaseBean> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }


    /**
     * 登陆接口
     * @param mobile
     * @param code
     * @param handler
     */
    public static void login(String mobile,String code,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile",mobile);
        map.put("smscode",code);
        Http.getRetrofit().create(HttpApi.class).login(map).enqueue(new Callback<Login>() {
            public void onResponse(Call<Login> call, Response<Login> response) {
                try {
                    sendMessage(handler, HandlerConstant.LOGIN_SUCCESS, response.body());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<Login> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }


    /**
     * 获取商品分类
     * @param handler
     */
    public static void getGoodsType(final Handler handler) {
        Map<String, String> map = new HashMap<>();
        Http.getRetrofit().create(HttpApi.class).getGoodsType(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, HandlerConstant.GET_GOODS_TYPE_SUCCESS, response.body().string());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }


    /**
     * 根据分类获取附近的商品
     * @param type
     * @param handler
     */
    public static void getLocationGoods(String type,int page,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("type",type);
        map.put("min_distance","0");
        map.put("max_distance","5000");
        map.put("page",page+"");
        map.put("row","20");
        Http.getRetrofit().create(HttpApi.class).getLocationGoods(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, HandlerConstant.GET_LOCATION_GOODS_SUCCESS, response.body().string());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }


    /**
     * 获取最新的accessToken
     * @param auth_token
     * @param handler
     */
    public static void getAccessToken(String auth_token,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("auth_token", auth_token);
        Http.getRetrofit().create(HttpApi.class).getAccessToken(map).enqueue(new Callback<Login>() {
            public void onResponse(Call<Login> call, Response<Login> response) {
                try {
                    sendMessage(handler, HandlerConstant.GET_ACCESS_TOKEN_SUCCESS, response.body());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<Login> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }

    /**
     * 添加商品
     * @param content
     * @param oldMoney
     * @param newMoney
     * @param goodsType
     * @param address
     * @param list
     * @param handler
     */
    public static void addGoods(String content, String oldMoney, String newMoney, String goodsType,String address, List<File> list, final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("description",content);
        map.put("original_price",oldMoney);
        map.put("present_price",newMoney);
        map.put("type",goodsType);
        map.put("address",address);
        Http.upLoadFile(HttpConstant.ADD_GOODS,"images", list, map, new okhttp3.Callback() {
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                try {
                    sendMessage(handler, HandlerConstant.ADD_GOODS_SUCCESS, response.body().string());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(okhttp3.Call call, IOException e) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);

            }

        });
    }


    /**
     * 获取我的商品
     * @param handler
     */
    public static void getMyGoodsList(final Handler handler) {
        Map<String, String> map = new HashMap<>();
        Http.getRetrofit().create(HttpApi.class).getMyGoodsList(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, HandlerConstant.GET_MYGOODS_SUCCESS, response.body().string());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }


    /**
     * 用户实名认证
     * @param full_name
     * @param id_code
     * @param handler
     */
    public static void certification(String full_name,String id_code,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("full_name",full_name);
        map.put("id_code",id_code);
        Http.getRetrofit().create(HttpApi.class).certification(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, HandlerConstant.CERIFICATION_SUCCESS, response.body().string());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }


    /**
     * 设置用户头像
     * @param list
     * @param handler
     */
    public static void setUserPic(List<File> list,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        Http.upLoadFile(HttpConstant.UPLOAD_USERPIC,"file", list, map, new okhttp3.Callback() {
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                try {
                    sendMessage(handler, HandlerConstant.SET_USERPIC_SUCCESS, response.body().string());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(okhttp3.Call call, IOException e) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);

            }

        });
    }


    /**
     * 设置用户昵称
     * @param nickname
     * @param handler
     */
    public static void setNickName(String nickname,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("nickname",nickname);
        Http.getRetrofit().create(HttpApi.class).setNickName(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, HandlerConstant.SET_NICKNAME_SUCCESS, response.body().string());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }


    /**
     * 获取用户信息
     * @param handler
     */
    public static void getUserInfo(final Handler handler) {
        Map<String, String> map = new HashMap<>();
        Http.getRetrofit().create(HttpApi.class).getUserInfo(map).enqueue(new Callback<UserInfo>() {
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                try {
                    sendMessage(handler, HandlerConstant.GET_USERINFO_SUCCESS, response.body());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<UserInfo> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }


    /**
     * 充值
     * @param amount
     * @param handler
     */
    public static void recharge(String amount,String payment,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("amount",amount);
        map.put("payment",payment);
        Http.getRetrofit().create(HttpApi.class).recharge(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, HandlerConstant.RECHARGE_SUCCESS, response.body().string());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }


    /**
     * 购买商品
     * @param res_type
     * @param payment
     * @param res_id
     * @param handler
     */
    public static void buy(String res_type,String payment,String res_id,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("res_type",res_type);
        map.put("payment",payment);
        map.put("res_id",res_id);
        Http.getRetrofit().create(HttpApi.class).buy(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, HandlerConstant.BUY_SUCCESS, response.body().string());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }
}
