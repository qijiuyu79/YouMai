package com.youmai.project.http;

import android.os.Handler;

import com.youmai.project.bean.DownLoad;
import com.youmai.project.bean.HttpBaseBean;
import com.youmai.project.bean.Login;
import com.youmai.project.bean.Report;
import com.youmai.project.bean.UserInfo;
import com.youmai.project.bean.Version;
import com.youmai.project.http.base.BaseRequst;
import com.youmai.project.http.base.Http;
import com.youmai.project.utils.LogUtils;

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
    public static void getLocationGoods(String type, int page, final int index, final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("type",type);
        map.put("min_distance","0");
        map.put("max_distance","15000");
        map.put("page",page+"");
        map.put("row","20");
        Http.getRetrofit().create(HttpApi.class).getLocationGoods(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, index, response.body().string());
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
                LogUtils.e("1111111111111111111111111111");
                try {
                    final String str=response.body().string();
                    LogUtils.e(str+"__________________");
                    sendMessage(handler, HandlerConstant.ADD_GOODS_SUCCESS, str);
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(okhttp3.Call call, IOException e) {
                LogUtils.e(e.getMessage()+"_________");
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);

            }

        });
    }


    /**
     * 获取我的商品
     * @param handler
     */
    public static void getMyGoodsList(int page,final int index,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("page",page+"");
        map.put("row","20");
        Http.getRetrofit().create(HttpApi.class).getMyGoodsList(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, index, response.body().string());
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
     * @param payment
     * @param goods_id
     * @param handler
     */
    public static void buy(String payment,String goods_id,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("payment",payment);
        map.put("goods_id",goods_id);
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


    /**
     * 查询版本
     * @return
     */
    public static void getVersion(final Handler handler) {
        Map<String, String> map = new HashMap<>();
        Http.getRetrofit().create(HttpApi.class).getVersion(map).enqueue(new Callback<Version>() {
            public void onResponse(Call<Version> call, Response<Version> response) {
                try {
                    sendMessage(handler, HandlerConstant.GET_VERSION_SUCCESS, response.body());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<Version> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }


    /**
     * 文件下载
     * @param handler
     */
    public static void download(final DownLoad downLoad, final Handler handler) {
        Http.dowload(downLoad.getDownPath(), downLoad.getSavePath(),handler, new okhttp3.Callback() {
            public void onFailure(okhttp3.Call call, IOException e) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if(response.isSuccessful()){
                    sendMessage(handler, HandlerConstant.DOWNLOAD_SUCCESS, downLoad);
                }
            }
        });
    }


    /**
     * 查询已支付订单
     * @param handler
     */
    public static void getPayOrderList(String stated, int page, final int index, final Handler handler) {
        Map<String, String> map = new HashMap<>();
        if(null!=stated){
            map.put("stated",stated);
        }
        map.put("page",page+"");
        map.put("row","20");
        Http.getRetrofit().create(HttpApi.class).getPayOrderList(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, index, response.body().string());
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
     * 根据关键字搜索
     * @param handler
     */
    public static void getGoodsByKey(String key,int page,final int index,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("key",key);
        map.put("page",page+"");
        map.put("row","20");
        Http.getRetrofit().create(HttpApi.class).getGoodsByKey(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, index, response.body().string());
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
     * 设置交易完成
     * @return
     */
    public static void setOrderComplete(String orderId,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("order_id",orderId);
        Http.getRetrofit().create(HttpApi.class).setOrderComplete(map).enqueue(new Callback<HttpBaseBean>() {
            public void onResponse(Call<HttpBaseBean> call, Response<HttpBaseBean> response) {
                try {
                    sendMessage(handler, HandlerConstant.SET_ORDER_COMPLETE_SUCCESS, response.body());
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
     * 设置交易取消
     * @return
     */
    public static void setOrderCancle(String orderId,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("order_id",orderId);
        Http.getRetrofit().create(HttpApi.class).setOrderCancle(map).enqueue(new Callback<HttpBaseBean>() {
            public void onResponse(Call<HttpBaseBean> call, Response<HttpBaseBean> response) {
                try {
                    sendMessage(handler, HandlerConstant.SET_ORDER_CANCLE_SUCCESS, response.body());
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
     * 查询附近店铺
     * @param handler
     */
    public static void getNearStore(final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("min_distance","0");
        map.put("max_distance","2000");
        Http.getRetrofit().create(HttpApi.class).getNearStore(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, HandlerConstant.GET_NEAR_STORE_SUCCESS, response.body().string());
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
     * 删除宝贝
     * @param goodsId
     * @param handler
     */
    public static void deleteBaby(String goodsId,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("goodsId",goodsId);
        Http.getRetrofit().create(HttpApi.class).deleteBaby(map).enqueue(new Callback<HttpBaseBean>() {
            public void onResponse(Call<HttpBaseBean> call, Response<HttpBaseBean> response) {
                try {
                    sendMessage(handler, HandlerConstant.DELETE_BABY_SUCCESS, response.body());
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
     * 根据storeId查询卖家商品
     * @param storeId
     * @param handler
     */
    public static void getGoodsByStoreId(int page,String storeId, final int index,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("storeId",storeId);
        map.put("page",page+"");
        map.put("row","20");
        Http.getRetrofit().create(HttpApi.class).getLocationGoods(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, index, response.body().string());
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
     * 查询卖家的订单
     * @param handler
     */
    public static void getMOrderList(String stated,int page, final int index,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        if(null!=stated){
            map.put("stated",stated);
        }
        map.put("page",page+"");
        map.put("row","20");
        Http.getRetrofit().create(HttpApi.class).getMOrderList(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, index, response.body().string());
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
     * 店铺签到
     * @param handler
     */
    public static void storeEvaluate(final Handler handler) {
        Map<String, String> map = new HashMap<>();
        Http.getRetrofit().create(HttpApi.class).storeEvaluate(map).enqueue(new Callback<HttpBaseBean>() {
            public void onResponse(Call<HttpBaseBean> call, Response<HttpBaseBean> response) {
                try {
                    sendMessage(handler, HandlerConstant.STORE_EVALUATE_SUCCESS, response.body());
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
     * 添加评论
     * @param handler
     */
    public static void addComment(String payOrderId, String score, String evaluate, List<File> list, final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("payOrderId",payOrderId);
        map.put("score",score);
        map.put("evaluate",evaluate);
        Http.upLoadFile(HttpConstant.ADD_COMMENT,"images", list, map, new okhttp3.Callback() {
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                try {
                    final String str=response.body().string();
                    LogUtils.e(str+"___________________");
                    sendMessage(handler, HandlerConstant.ADD_COMMENT_SUCCESS, str);
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
     * 查询评论列表
     * @param handler
     */
    public static void getCommentList(String storeId,int page,final int index,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("storeId",storeId);
        map.put("page",page+"");
        map.put("row","20");
        Http.getRetrofit().create(HttpApi.class).getCommentList(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, index, response.body().string());
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
     * 查询订单详情
     * @param handler
     */
    public static void getOrderDetails(String orderId,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("orderId",orderId);
        Http.getRetrofit().create(HttpApi.class).getOrderDetails(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, HandlerConstant.GET_ORDER_DETAILS_SUCCESS, response.body().string());
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
     * 转账
     * @param handler
     */
    public static void withdrawal(String amount,String payment,String account,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("amount",amount);
        map.put("payment",payment);
        map.put("account",account);
        Http.getRetrofit().create(HttpApi.class).withdrawal(map).enqueue(new Callback<HttpBaseBean>() {
            public void onResponse(Call<HttpBaseBean> call, Response<HttpBaseBean> response) {
                try {
                    sendMessage(handler, HandlerConstant.WITHDRAWAL_SUCCESS, response.body());
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
     * 查询附近商品数量
     * @param handler
     */
    public static void getLocationCount(final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("min_distance","0");
        map.put("max_distance","5000");
        Http.getRetrofit().create(HttpApi.class).getLocationCount(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, HandlerConstant.GET_LOCATION_COUNT_SUCCESS, response.body().string());
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
     * 根据商品id查询商品详情
     * @param goods_id
     * @param handler
     */
    public static void getGoodsDetails(String goods_id,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("goods_id",goods_id);
        Http.getRetrofit().create(HttpApi.class).getGoodsDetails(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, HandlerConstant.GET_GOODS_DETAILS_SUCCESS, response.body().string());
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
     * 获取举报类型
     * @return
     */
    public static void getReportList(final Handler handler) {
        Map<String, String> map = new HashMap<>();
        Http.getRetrofit().create(HttpApi.class).getReportList(map).enqueue(new Callback<Report>() {
            public void onResponse(Call<Report> call, Response<Report> response) {
                try {
                    sendMessage(handler, HandlerConstant.GET_REPORT_LIST_SUCCESS, response.body());
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
                }
            }

            public void onFailure(Call<Report> call, Throwable t) {
                sendMessage(handler, HandlerConstant.REQUST_ERROR, null);
            }
        });
    }


    /**
     * 举报商品
     * @param targetObjType
     * @param targetObjId
     * @param typeId
     * @param handler
     */
    public static void reportGoods(int targetObjType,String targetObjId,int typeId,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("targetObjType",targetObjType+"");
        map.put("targetObjId",targetObjId);
        map.put("typeId",typeId+"");
        Http.getRetrofit().create(HttpApi.class).reportGoods(map).enqueue(new Callback<HttpBaseBean>() {
            public void onResponse(Call<HttpBaseBean> call, Response<HttpBaseBean> response) {
                try {
                    sendMessage(handler, HandlerConstant.REPORT_GOODS_SUCCESS, response.body());
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
     * 根据storeId查询商铺信息
     * @param storeId
     * @param handler
     */
    public static void getStoreInfo(String storeId,final Handler handler) {
        Map<String, String> map = new HashMap<>();
        map.put("storeId",storeId);
        Http.getRetrofit().create(HttpApi.class).getStoreInfo(map).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    sendMessage(handler, HandlerConstant.GET_STORE_INFO_SUCCESS, response.body().string());
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
