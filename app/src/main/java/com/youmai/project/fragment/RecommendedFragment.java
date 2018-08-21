package com.youmai.project.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.youmai.project.R;
import com.youmai.project.activity.main.BuyGoodsActivity;
import com.youmai.project.activity.main.GoodDetailsActivity;
import com.youmai.project.activity.main.MainActivity;
import com.youmai.project.adapter.RecommendedAdapter;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.view.RefreshLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 推荐的fragment
 * Created by Administrator on 2018/1/3 0003.
 */

public class RecommendedFragment extends BaseFragment  implements SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener,AdapterView.OnItemClickListener {

    private RefreshLayout swipeLayout;
    private ListView listView;
    private RecommendedAdapter recommendedAdapter;
    private List<GoodsBean> listBeanAll=new ArrayList<>();
    private int page=1;
    private boolean isTotal=false;
    //fragment是否可见
    private boolean isVisibleToUser=false;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    View view=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommended, container, false);
        swipeLayout=(RefreshLayout)view.findViewById(R.id.swipe_container);
        listView=(ListView)view.findViewById(R.id.list);
        listView.setDividerHeight(0);
        listView.setOnItemClickListener(this);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,
                R.color.color_bule,
                R.color.color_bule2,
                R.color.color_bule3);
        swipeLayout.setOnRefreshListener(RecommendedFragment.this);
        swipeLayout.setOnLoadListener(RecommendedFragment.this);
        swipeLayout.post(new Thread(new Runnable() {
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        }));
        //注册广播
        registerReceiver();
        //查询数据
        loadData();
        return view;
    }

    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message;
            switch (msg.what){
                case HandlerConstant.GET_LOCATION_GOODS_SUCCESS:
                     message= (String) msg.obj;
                     listBeanAll.clear();
                     refresh(message);
                     swipeLayout.setRefreshing(false);
                     break;
                case HandlerConstant.GET_LOCATION_GOODS_SUCCESS2:
                     message= (String) msg.obj;
                     refresh(message);
                     swipeLayout.setLoading(false);
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
            final JSONObject jsonObject=new JSONObject(message);
            final JSONArray jsonArray=new JSONArray(jsonObject.getString("data"));
            List<GoodsBean> list=new ArrayList<>();
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
            listBeanAll.addAll(list);
            if(null==recommendedAdapter){
                recommendedAdapter=new RecommendedAdapter(getActivity(),listBeanAll);
                listView.setAdapter(recommendedAdapter);
            }else{
                recommendedAdapter.notifyDataSetChanged();
            }
            if(list.size()<20){
                isTotal=true;
                swipeLayout.setFooter(isTotal);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * item点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtils.e("position="+position);
        GoodsBean goodsBean=listBeanAll.get(--position);
        if(null==goodsBean){
            return;
        }
        Intent intent=new Intent(getActivity(), GoodDetailsActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("goodsBean",goodsBean);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {
            public void run() {
                page=1;
                isTotal=false;
                swipeLayout.setFooter(isTotal);
                getDataList(HandlerConstant.GET_LOCATION_GOODS_SUCCESS);
            }
        }, 200);
    }

    @Override
    public void onLoad() {
        if(isTotal){
            swipeLayout.setLoading(false);
            return;
        }
        swipeLayout.postDelayed(new Runnable() {
            public void run() {
                page++;
                getDataList(HandlerConstant.GET_LOCATION_GOODS_SUCCESS2);
            }
        }, 200);

    }


    /**
     * 查询数据
     */
    private void loadData(){
        if(isVisibleToUser && view!=null && listBeanAll.size()==0){
            swipeLayout.postDelayed(new Runnable() {
                public void run() {
                    listView.addHeaderView(new View(getActivity()));
                    getDataList(HandlerConstant.GET_LOCATION_GOODS_SUCCESS);
                }
            }, 0);
        }
    }


    /**
     * 查询商品列表
     * @param index
     */
    private void getDataList(int index){
        HttpMethod.getLocationGoods(MainActivity.keyList.get(MainActivity.index),page,index,mHandler);
    }


    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BuyGoodsActivity.ACTION_GOODS_PAYSUCCESS);
        // 注册广播监听
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                //商品购买成功后的广播
                case BuyGoodsActivity.ACTION_GOODS_PAYSUCCESS:
                     final String goodId=intent.getStringExtra("goodId");
                     if(TextUtils.isEmpty(goodId)){
                         return;
                     }
                     for(int i=0,len=listBeanAll.size();i<len;i++){
                         if(listBeanAll.get(i).getId().equals(goodId)){
                             listBeanAll.remove(i);
                             break;
                         }
                     }
                    recommendedAdapter.notifyDataSetChanged();
                     break;
            }
        }
    };


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser=isVisibleToUser;
        //查询数据
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

}
