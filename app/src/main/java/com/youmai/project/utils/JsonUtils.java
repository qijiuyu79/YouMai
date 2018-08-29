package com.youmai.project.utils;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.bean.Store;
import com.youmai.project.view.CircleImageView;
import com.youmai.project.view.ExpandView;

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
               if(!jsonObject2.isNull("creditLevel")){
                   goodsBean.setCreditLevel(jsonObject2.getInt("creditLevel"));
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
                    if(!jsonObject3.isNull("creditLevel")){
                        myGoods.setCreditLevel(jsonObject3.getInt("creditLevel"));
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
                    if(!jsonObject3.isNull("creditLevel")){
                        myGoods.setCreditLevel(jsonObject3.getInt("creditLevel"));
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


    public static void getStoreList(String msg,List<Store> list){
       try {
           list.clear();
           final JSONObject jsonObject=new JSONObject(msg);
           final JSONArray jsonArray=new JSONArray(jsonObject.getString("data"));
           for (int i = 0; i < jsonArray.length(); i++) {
               JSONObject jsonObject1=jsonArray.getJSONObject(i);
               Store store=new Store();
               if(!jsonObject1.isNull("creditLevel")){
                   store.setCreditLevel(jsonObject1.getInt("creditLevel"));
               }
               if(!jsonObject1.isNull("head")){
                   store.setHead(jsonObject1.getString("head"));
               }
               if(!jsonObject1.isNull("id")){
                   store.setId(jsonObject1.getString("id"));
               }
               if(!jsonObject1.isNull("nickname")){
                   store.setNickname(jsonObject1.getString("nickname"));
               }

               //解析经纬度
               if(!jsonObject1.isNull("location")){
                   JSONObject jsonObject2=new JSONObject(jsonObject1.getString("location"));

                   JSONArray jsonArray2=new JSONArray(jsonObject2.getString("coordinates"));
                   for (int k = 0; k < jsonArray2.length(); k++) {
                       if(k==0){
                           store.setLongitude(jsonArray2.getDouble(k));
                       }else{
                           store.setLatitude(jsonArray2.getDouble(k));
                       }
                   }
               }
               list.add(store);
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }
}
