package com.youmai.project.utils;

import android.text.TextUtils;

import com.youmai.project.bean.GoodsBean;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析json数据
 * Created by Administrator on 2017/3/13 0013.
 */

public class JsonUtils {


   public static List<GoodsBean> getGoods(String msg){
       List<GoodsBean> list=new ArrayList<>();
       if(TextUtils.isEmpty(msg)){
           return list;
       }
       try {
           final JSONObject jsonObject=new JSONObject(msg);
           if(jsonObject.getInt("code")!=200){
               return list;
           }
           final JSONArray jsonArray=new JSONArray(jsonObject.getString("data"));
           for (int i = 0; i < jsonArray.length(); i++) {
               JSONObject jsonObject1=jsonArray.getJSONObject(i);
               GoodsBean goodsBean=new GoodsBean();
               goodsBean.setAddress(jsonObject1.getString("address"));
               goodsBean.setDescription(jsonObject1.getString("description"));
               goodsBean.setId(jsonObject1.getString("id"));
               goodsBean.setOriginalPrice(jsonObject1.getDouble("originalPrice"));
               goodsBean.setPresentPrice(jsonObject1.getDouble("presentPrice"));
               List<String> imgList=new ArrayList<>();

               //解析图片
               if(!jsonObject1.isNull("images")){
                   final JSONArray jsonArray1=new JSONArray(jsonObject1.getString("images"));
                   for (int j = 0; j < jsonArray1.length(); j++) {
                       imgList.add(jsonArray1.getString(j));
                   }
                   goodsBean.setImgList(imgList);
               }

               //解析经纬度
               final JSONArray jsonArray2=new JSONArray(jsonObject1.getString("location"));
               for (int k = 0; k < jsonArray2.length(); k++) {
                   if(k==0){
                       goodsBean.setLongitude(jsonArray2.getDouble(k));
                   }else{
                       goodsBean.setLatitude(jsonArray2.getDouble(k));
                   }
               }

               //解析用户信息
               if(jsonObject1.isNull("seller")){
                   continue ;
               }
               JSONObject jsonObject2=new JSONObject(jsonObject1.getString("seller"));
               if(!jsonObject2.isNull("head")){
                   goodsBean.setHead(jsonObject2.getString("head"));
               }
               if(!jsonObject2.isNull("nickname")){
                   goodsBean.setNickname(jsonObject2.getString("nickname"));
               }
               if(!jsonObject2.isNull("storeId")){
                   goodsBean.setStoreId(jsonObject2.getString("storeId"));
               }
               list.add(goodsBean);
           }
       }catch (Exception e){
           e.printStackTrace();
       }
       return list;
   }


    public static List<GoodsBean> getGoods2(String msg){
        List<GoodsBean> list=new ArrayList<>();
        if(TextUtils.isEmpty(msg)){
            return list;
        }
        try {
            final JSONObject jsonObject=new JSONObject(msg);
            if(jsonObject.getInt("code")!=200){
                return list;
            }
            final JSONArray jsonArray=new JSONArray(jsonObject.getString("data"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                GoodsBean myGoods=new GoodsBean();

                //解析用户信息
                if(!jsonObject1.isNull("seller")){
                    JSONObject jsonObject3=new JSONObject(jsonObject1.getString("seller"));
                    if(!jsonObject3.isNull("head")){
                        myGoods.setHead(jsonObject3.getString("head"));
                    }
                    if(!jsonObject3.isNull("nickname")){
                        myGoods.setNickname(jsonObject3.getString("nickname"));
                    }
                    if(!jsonObject3.isNull("mobile")){
                        myGoods.setMobile(jsonObject3.getString("mobile"));
                    }
                }

                //解析用户信息
                if(!jsonObject1.isNull("buyer")){
                    JSONObject jsonObject3=new JSONObject(jsonObject1.getString("buyer"));
                    if(!jsonObject3.isNull("head")){
                        myGoods.setHead(jsonObject3.getString("head"));
                    }
                    if(!jsonObject3.isNull("nickname")){
                        myGoods.setNickname(jsonObject3.getString("nickname"));
                    }
                    if(!jsonObject3.isNull("mobile")){
                        myGoods.setMobile(jsonObject3.getString("mobile"));
                    }
                }

                JSONObject jsonObject2=new JSONObject(jsonObject1.getString("goods"));
                myGoods.setOrderId(jsonObject1.getString("id"));
                myGoods.setStated(jsonObject1.getInt("stated"));
                myGoods.setAddress(jsonObject2.getString("address"));
                myGoods.setDescription(jsonObject2.getString("description"));
                myGoods.setId(jsonObject2.getString("id"));
                myGoods.setOriginalPrice(jsonObject2.getDouble("originalPrice"));
                myGoods.setPresentPrice(jsonObject2.getDouble("presentPrice"));
                List<String> imgList=new ArrayList<>();

                //解析图片
                if(!jsonObject2.isNull("images")){
                    final JSONArray jsonArray1=new JSONArray(jsonObject2.getString("images"));
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        imgList.add(jsonArray1.getString(j));
                    }
                    myGoods.setImgList(imgList);
                }

                //解析经纬度
                final JSONArray jsonArray2=new JSONArray(jsonObject2.getString("location"));
                for (int k = 0; k < jsonArray2.length(); k++) {
                    if(k==0){
                        myGoods.setLongitude(jsonArray2.getDouble(k));
                    }else{
                        myGoods.setLatitude(jsonArray2.getDouble(k));
                    }
                }
                list.add(myGoods);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
