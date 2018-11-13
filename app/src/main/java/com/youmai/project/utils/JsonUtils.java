package com.youmai.project.utils;

import android.text.TextUtils;
import com.youmai.project.bean.Comment;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.bean.Store;
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
               if(!jsonObject1.isNull("address")){
                   goodsBean.setAddress(jsonObject1.getString("address"));
               }
               if(!jsonObject1.isNull("description")){
                   goodsBean.setDescription(jsonObject1.getString("description"));
               }
               if(!jsonObject1.isNull("id")){
                   goodsBean.setId(jsonObject1.getString("id"));
               }
               if(!jsonObject1.isNull("originalPrice")){
                   goodsBean.setOriginalPrice(jsonObject1.getDouble("originalPrice"));
               }
               if(!jsonObject1.isNull("presentPrice")){
                   goodsBean.setPresentPrice(jsonObject1.getDouble("presentPrice"));
               }
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
               if(!jsonObject1.isNull("location")){
                   final JSONArray jsonArray2=new JSONArray(jsonObject1.getString("location"));
                   for (int k = 0; k < jsonArray2.length(); k++) {
                       if(k==0){
                           goodsBean.setLongitude(jsonArray2.getDouble(k));
                       }else{
                           goodsBean.setLatitude(jsonArray2.getDouble(k));
                       }
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
                GoodsBean myGoods=new GoodsBean();
                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                if(!jsonObject1.isNull("commented")){
                    myGoods.setCommented(jsonObject1.getBoolean("commented"));
                }
                if(!jsonObject1.isNull("qrCodeText")){
                    myGoods.setQrCodeText(jsonObject1.getString("qrCodeText"));
                }
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

                if(jsonObject1.isNull("goods")){
                    continue ;
                }
                JSONObject jsonObject2=new JSONObject(jsonObject1.getString("goods"));
                myGoods.setOrderId(jsonObject1.getString("id"));
                myGoods.setStated(jsonObject1.getInt("stated"));
                myGoods.setPayment(jsonObject1.getInt("payment"));
                myGoods.setAddress(jsonObject2.getString("address"));
                myGoods.setDescription(jsonObject2.getString("description"));
                myGoods.setId(jsonObject2.getString("id"));
                myGoods.setOriginalPrice(jsonObject2.getDouble("originalPrice"));
                myGoods.setPresentPrice(jsonObject2.getDouble("presentPrice"));

                //解析图片
                List<String> imgList=new ArrayList<>();
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
               store.setPosition(i);
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


    public static List<Comment> getCommentList(String msg){
        List<Comment> list=new ArrayList<>();
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
                Comment comment=new Comment();
                final JSONObject jsonObject1=jsonArray.getJSONObject(i);
                comment.setId(jsonObject1.getString("id"));
                comment.setCreateTime(jsonObject1.getLong("createTime"));
                comment.setEvaluate(jsonObject1.getString("evaluate"));
                //评论星级
                comment.setScore(jsonObject1.getInt("score"));
                //解析评论图片
                List<String> commImgs=new ArrayList<>();
                if(!jsonObject1.isNull("images")){
                    final JSONArray jsonArray1=new JSONArray(jsonObject1.getString("images"));
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        commImgs.add(jsonArray1.getString(j));
                    }
                    comment.setComm_imgs(commImgs);
                }

                JSONObject jsonObject3=new JSONObject(jsonObject1.getString("goods"));
                comment.setDescription(jsonObject3.getString("description"));

                //解析图片
                List<String> imgList=new ArrayList<>();
                if(!jsonObject3.isNull("images")){
                    final JSONArray jsonArray1=new JSONArray(jsonObject3.getString("images"));
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        imgList.add(jsonArray1.getString(j));
                    }
                    comment.setImgList(imgList);
                }
                comment.setPresentPrice(jsonObject3.getDouble("presentPrice"));

                //解析评论者信息
                final JSONObject jsonObject2=new JSONObject(jsonObject1.getString("author"));
                comment.setCreditLevel(jsonObject2.getInt("creditLevel"));
                if(!jsonObject2.isNull("head")){
                    comment.setHead(jsonObject2.getString("head"));
                }
                comment.setNickname(jsonObject2.getString("nickname"));

                //解析卖家信息
                if(!jsonObject3.isNull("seller")){
                    JSONObject jsonObject4=new JSONObject(jsonObject3.getString("seller"));
                    if(!jsonObject4.isNull("nickname")){
                        comment.setS_nickname(jsonObject4.getString("nickname"));
                    }
                    if(!jsonObject4.isNull("creditLevel")){
                        comment.setS_creditLevel(jsonObject4.getInt("creditLevel"));
                    }
                }

                //解析卖家回复内容
                if(!jsonObject1.isNull("reply")){
                    final JSONObject jsonObject5=new JSONObject(jsonObject1.getString("reply"));
                    comment.setReplyContent(jsonObject5.getString("content"));
                }
                list.add(comment);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }



    public static  GoodsBean getGoodsBean(String message){
        GoodsBean myGoods=new GoodsBean();
       try {
           final JSONObject jsonObject=new JSONObject(message);
           if(jsonObject.getInt("code")==200){
               final JSONObject jsonObject1=new JSONObject(jsonObject.getString("data"));
               myGoods.setAddress(jsonObject1.getString("address"));
               myGoods.setDescription(jsonObject1.getString("description"));
               myGoods.setId(jsonObject1.getString("id"));
               myGoods.setOriginalPrice(jsonObject1.getDouble("originalPrice"));
               myGoods.setPresentPrice(jsonObject1.getDouble("presentPrice"));
               //解析图片
               List<String> imgList=new ArrayList<>();
               JSONArray jsonArray1=new JSONArray(jsonObject1.getString("images"));
               for (int j = 0; j < jsonArray1.length(); j++) {
                   imgList.add(jsonArray1.getString(j));
               }
               myGoods.setImgList(imgList);

               //解析经纬度
               final JSONArray jsonArray2=new JSONArray(jsonObject1.getString("location"));
               for (int k = 0; k < jsonArray2.length(); k++) {
                   if(k==0){
                       myGoods.setLongitude(jsonArray2.getDouble(k));
                   }else{
                       myGoods.setLatitude(jsonArray2.getDouble(k));
                   }
               }
               //解析用户信息
               if(!jsonObject1.isNull("seller")){
                   JSONObject jsonObject2=new JSONObject(jsonObject1.getString("seller"));
                   if(!jsonObject2.isNull("head")){
                       myGoods.setHead(jsonObject2.getString("head"));
                   }
                   if(!jsonObject2.isNull("nickname")){
                       myGoods.setNickname(jsonObject2.getString("nickname"));
                   }
                   if(!jsonObject2.isNull("storeId")){
                       myGoods.setStoreId(jsonObject2.getString("storeId"));
                   }
                   if(!jsonObject2.isNull("creditLevel")){
                       myGoods.setCreditLevel(jsonObject2.getInt("creditLevel"));
                   }
               }
           }
       }catch (Exception e){
           e.printStackTrace();
       }
       return myGoods;
    }



    public static List<GoodsBean> getMyGoods(String message){
        List<GoodsBean> list=new ArrayList<>();
        if(TextUtils.isEmpty(message)){
            return list;
        }
       try {
           JSONObject jsonObject=new JSONObject(message);
           if(jsonObject.getInt("code")!=200){
               return list;
           }
           JSONArray jsonArray=new JSONArray(jsonObject.getString("data"));
           for (int i = 0; i < jsonArray.length(); i++) {
               GoodsBean myGoods=new GoodsBean();
               JSONObject jsonObject1=jsonArray.getJSONObject(i);
               myGoods.setAddress(jsonObject1.getString("address"));
               myGoods.setCreateTime(jsonObject1.getLong("createTime"));
               myGoods.setDescription(jsonObject1.getString("description"));
               myGoods.setId(jsonObject1.getString("id"));
               myGoods.setOriginalPrice(jsonObject1.getDouble("originalPrice"));
               myGoods.setPresentPrice(jsonObject1.getDouble("presentPrice"));
               myGoods.setStoreId(jsonObject1.getString("storeId"));

               //解析图片
               List<String> imgList=new ArrayList<>();
               JSONArray jsonArray1=new JSONArray(jsonObject1.getString("images"));
               for (int j = 0; j < jsonArray1.length(); j++) {
                   imgList.add(jsonArray1.getString(j));
               }
               myGoods.setImgList(imgList);

               //解析经纬度
               JSONObject jsonObject2=new JSONObject(jsonObject1.getString("location"));
               if(null!=jsonObject2){
                   JSONArray jsonArray2=new JSONArray(jsonObject2.getString("coordinates"));
                   for (int k = 0; k < jsonArray2.length(); k++) {
                       if(k==0){
                           myGoods.setLongitude(jsonArray2.getDouble(k));
                       }else{
                           myGoods.setLatitude(jsonArray2.getDouble(k));
                       }
                   }
               }
               list.add(myGoods);
           }
       }catch (Exception e){
           e.printStackTrace();
       }
       return  list;
    }


    public static List<GoodsBean> getMapGoods(String message){
        List<GoodsBean> list=new ArrayList<>();
        if(TextUtils.isEmpty(message)){
            return list;
        }
        try {
            JSONObject jsonObject=new JSONObject(message);
            if(jsonObject.getInt("code")!=200){
                return list;
            }
            JSONObject jsonObject0=new JSONObject(jsonObject.getString("data"));
            JSONArray jsonArray=new JSONArray(jsonObject0.getString("goodsList"));
            for (int i = 0; i < jsonArray.length(); i++) {
                GoodsBean myGoods=new GoodsBean();
                myGoods.setCommentCount(jsonObject0.getInt("commentCount"));
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                myGoods.setAddress(jsonObject1.getString("address"));
                myGoods.setDescription(jsonObject1.getString("description"));
                myGoods.setId(jsonObject1.getString("id"));
                myGoods.setOriginalPrice(jsonObject1.getDouble("originalPrice"));
                myGoods.setPresentPrice(jsonObject1.getDouble("presentPrice"));

                //解析图片
                List<String> imgList=new ArrayList<>();
                JSONArray jsonArray1=new JSONArray(jsonObject1.getString("images"));
                for (int j = 0; j < jsonArray1.length(); j++) {
                    imgList.add(jsonArray1.getString(j));
                }
                myGoods.setImgList(imgList);

                //解析经纬度
                final JSONArray jsonArray2=new JSONArray(jsonObject1.getString("location"));
                for (int k = 0; k < jsonArray2.length(); k++) {
                    if(k==0){
                        myGoods.setLongitude(jsonArray2.getDouble(k));
                    }else{
                        myGoods.setLatitude(jsonArray2.getDouble(k));
                    }
                }

                //解析用户信息
                if(!jsonObject1.isNull("seller")){
                    JSONObject jsonObject2=new JSONObject(jsonObject1.getString("seller"));
                    if(!jsonObject2.isNull("head")){
                        myGoods.setHead(jsonObject2.getString("head"));
                    }
                    if(!jsonObject2.isNull("nickname")){
                        myGoods.setNickname(jsonObject2.getString("nickname"));
                    }
                    if(!jsonObject2.isNull("storeId")){
                        myGoods.setStoreId(jsonObject2.getString("storeId"));
                    }
                    if(!jsonObject2.isNull("creditLevel")){
                        myGoods.setCreditLevel(jsonObject2.getInt("creditLevel"));
                    }
                }

                list.add(myGoods);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  list;
    }
}
