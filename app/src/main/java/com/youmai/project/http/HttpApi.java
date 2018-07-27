package com.youmai.project.http;

import com.youmai.project.bean.HttpBaseBean;
import com.youmai.project.bean.Login;
import com.youmai.project.bean.UserInfo;
import com.youmai.project.bean.Version;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2018/5/17 0017.
 */

public interface HttpApi {

    /**
     * 获取短信验证码
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.SEND_CODE)
    Call<HttpBaseBean> sendCode(@FieldMap Map<String, String> map);


    /**
     * 登陆接口
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.LOGIN)
    Call<Login> login(@FieldMap Map<String, String> map);


    /**
     * 获取商品分类
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_GOODS_TYPE)
    Call<ResponseBody> getGoodsType(@FieldMap Map<String, String> map);


    /**
     * 获取附近的推荐商品
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_LOCATION_GOODS)
    Call<ResponseBody> getLocationGoods(@FieldMap Map<String, String> map);


    /**
     * 获取最新的accessToken
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_ACCESS_TOKEN)
    Call<Login> getAccessToken(@FieldMap Map<String, String> map);


    /**
     * 获取我的商品
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_MYGOODS_LIST)
    Call<ResponseBody> getMyGoodsList(@FieldMap Map<String, String> map);


    /**
     * 实名认证
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.CERTIFICATION_USER)
    Call<ResponseBody> certification(@FieldMap Map<String, String> map);


    /**
     * 设置昵称
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.SET_NICKNAME)
    Call<ResponseBody> setNickName(@FieldMap Map<String, String> map);


    /**
     * 获取用户信息
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_USERINFO)
    Call<UserInfo> getUserInfo(@FieldMap Map<String, String> map);


    /**
     * 充值
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.RECHARGE)
    Call<ResponseBody> recharge(@FieldMap Map<String, String> map);


    /**
     * 购买商品
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.BUY)
    Call<ResponseBody> buy(@FieldMap Map<String, String> map);



    /**
     * 查询版本
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_VERSION)
    Call<Version> getVersion(@FieldMap Map<String, String> map);


    /**
     * 查询已支付订单
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.PAY_SUCCESS_ORDER)
    Call<ResponseBody> getPayOrderList(@FieldMap Map<String, String> map);


    /**
     * 根据关键字搜索
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.SEARCH_GOODS_BY_KEY)
    Call<ResponseBody> getGoodsByKey(@FieldMap Map<String, String> map);

}
