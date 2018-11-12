package com.youmai.project.http;

import com.youmai.project.bean.HttpBaseBean;
import com.youmai.project.bean.Login;
import com.youmai.project.bean.Report;
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


    /**
     * 设置交易完成
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.ORDER_COMPLETE)
    Call<HttpBaseBean> setOrderComplete(@FieldMap Map<String, String> map);


    /**
     * 设置交易取消
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.ORDER_CANCLE)
    Call<HttpBaseBean> setOrderCancle(@FieldMap Map<String, String> map);


    /**
     * 查询附近店铺
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.NEAR_STORE)
    Call<ResponseBody> getNearStore(@FieldMap Map<String, String> map);


    /**
     * 删除宝贝
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.DELETE_BABY)
    Call<HttpBaseBean> deleteBaby(@FieldMap Map<String, String> map);


    /**
     * 查询卖家的订单
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.M_ORDER_LIST)
    Call<ResponseBody> getMOrderList(@FieldMap Map<String, String> map);


    /**
     * 店铺签到
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.STORE_EVALUATE)
    Call<HttpBaseBean> storeEvaluate(@FieldMap Map<String, String> map);


    /**
     * 查询评论列表
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_COMMENT_LIST)
    Call<ResponseBody> getCommentList(@FieldMap Map<String, String> map);


    /**
     * 查询订单详情
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_ORDER_DETAILS)
    Call<ResponseBody> getOrderDetails(@FieldMap Map<String, String> map);


    /**
     * 转账
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.WITHDRAWAL)
    Call<HttpBaseBean> withdrawal(@FieldMap Map<String, String> map);


    /**
     * 查询附近宝贝数量
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.LOCATION_GOODS_COUNT)
    Call<ResponseBody> getLocationCount(@FieldMap Map<String, String> map);


    /**
     * 根据商品id查询商品详情
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_GOODS_DETAILS)
    Call<ResponseBody> getGoodsDetails(@FieldMap Map<String, String> map);


    /**
     * 获取举报类型
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_REPORT_LIST)
    Call<Report> getReportList(@FieldMap Map<String, String> map);


    /**
     * 举报商品
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.REPORT_GOODS)
    Call<HttpBaseBean> reportGoods(@FieldMap Map<String, String> map);



    /**
     * 根据storeId查询商铺信息
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_STORE_INFO)
    Call<ResponseBody> getStoreInfo(@FieldMap Map<String, String> map);



    /**
     * 删除订单
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.DELETE_ORDER)
    Call<HttpBaseBean> deleteOrder(@FieldMap Map<String, String> map);


    /**
     * 回复评论
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.Reply)
    Call<HttpBaseBean> reply(@FieldMap Map<String, String> map);

}
