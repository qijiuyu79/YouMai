package com.youmai.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.youmai.project.R;
import com.youmai.project.adapter.MyGoodsAdapter;
import com.youmai.project.adapter.RecommendedAdapter;
import com.youmai.project.bean.MainBean;
import com.youmai.project.bean.MyGoods;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.view.RefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐的fragment
 * Created by Administrator on 2018/1/3 0003.
 */

public class MyGoodsFragment extends BaseFragment  implements SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener {

    private RefreshLayout swipeLayout;
    private ListView listView;
    private MyGoodsAdapter myGoodsAdapter;
    private List<MyGoods> listAll=new ArrayList<>();
    private int page=1;
    private boolean isTotal=false;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommended, container, false);
        swipeLayout=(RefreshLayout)view.findViewById(R.id.swipe_container);
        listView=(ListView)view.findViewById(R.id.list);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,
                R.color.color_bule,
                R.color.color_bule2,
                R.color.color_bule3);
        swipeLayout.setOnRefreshListener(MyGoodsFragment.this);
        swipeLayout.setOnLoadListener(MyGoodsFragment.this);
        swipeLayout.post(new Thread(new Runnable() {
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        }));
        swipeLayout.postDelayed(new Runnable() {
            public void run() {
                listView.addHeaderView(new View(getActivity()));
                getMyGoodsList();
            }
        }, 0);
        return view;
    }


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HandlerConstant.GET_MYGOODS_SUCCESS:
                     final String message= (String) msg.obj;
                     refresh(message);
                     swipeLayout.setRefreshing(false);
                    break;
                case HandlerConstant.REQUST_ERROR:
                     showMsg(getString(R.string.http_error));
                    break ;
                default:
                    break;
            }
        }
    };


    /**
     * 解析并刷新数据
     */
    private void refresh(String message){
        if(TextUtils.isEmpty(message)){
            return;
        }
        try {
            JSONObject jsonObject=new JSONObject(message);
            JSONArray jsonArray=new JSONArray(jsonObject.getString("data"));
            List<MyGoods> list=new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                 MyGoods myGoods=new MyGoods();
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
                JSONObject jsonObject2=new JSONObject(jsonObject1.getString("location"));
                if(null==jsonObject2){
                    return;
                }
                JSONArray jsonArray2=new JSONArray(jsonObject2.getString("coordinates"));
                for (int k = 0; k < jsonArray2.length(); k++) {
                     if(k==0){
                         myGoods.setLongitude(jsonArray2.getDouble(k));
                     }else{
                         myGoods.setLatitude(jsonArray2.getDouble(k));
                     }
                }
                list.add(myGoods);
            }
            listAll.addAll(list);
            if(null==myGoodsAdapter){
                myGoodsAdapter=new MyGoodsAdapter(getActivity(),listAll);
                listView.setAdapter(myGoodsAdapter);
            }else{
                myGoodsAdapter.notifyDataSetChanged();
            }
            if(list.size()<10){
                isTotal=true;
                swipeLayout.setFooter(isTotal);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 获取我的商品
     */
    private void getMyGoodsList(){
        HttpMethod.getMyGoodsList(mHandler);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoad() {

    }


    public void addGoods(MyGoods myGoods){
        listAll.add(0,myGoods);
        if(null==myGoodsAdapter){
            myGoodsAdapter=new MyGoodsAdapter(getActivity(),listAll);
            listView.setAdapter(myGoodsAdapter);
        }else{
            myGoodsAdapter.notifyDataSetChanged();
        }
    }
}
