package com.youmai.project.fragment.order;

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
import com.youmai.project.activity.order.OrderActivity;
import com.youmai.project.adapter.OrderAdapter;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.bean.TradingPlay;
import com.youmai.project.fragment.BaseFragment;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.view.DialogView;
import com.youmai.project.view.RefreshLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 推荐的fragment
 * Created by Administrator on 2018/1/3 0003.
 */

public class OrderFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener {

    private RefreshLayout swipeLayout;
    private ListView listView;
    private OrderAdapter orderAdapter;
    private List<GoodsBean> listBeanAll=new ArrayList<>();
    private boolean isTotal=false;
    //fragment是否可见
    private boolean isVisibleToUser=false;
    private DialogView dialogView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommended, container, false);
        swipeLayout=(RefreshLayout)view.findViewById(R.id.swipe_container);
        listView=(ListView)view.findViewById(R.id.list);
        listView.setDividerHeight(0);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,
                R.color.color_bule,
                R.color.color_bule2,
                R.color.color_bule3);
        swipeLayout.setOnRefreshListener(OrderFragment.this);
        swipeLayout.setOnLoadListener(OrderFragment.this);
        swipeLayout.post(new Thread(new Runnable() {
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        }));
        //查询订单列表
        getOrderList();
        return view;
    }


    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HandlerConstant.GET_PAY_ORDER_SUCCESS:
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
            final JSONObject jsonObject=new JSONObject(message);
            final JSONArray jsonArray=new JSONArray(jsonObject.getString("data"));
            List<GoodsBean> list=new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                JSONObject jsonObject2=new JSONObject(jsonObject1.getString("goods"));
                GoodsBean myGoods=new GoodsBean();
                myGoods.setAddress(jsonObject2.getString("address"));
                myGoods.setDescription(jsonObject2.getString("description"));
                myGoods.setId(jsonObject2.getString("id"));
                myGoods.setOriginalPrice(jsonObject2.getDouble("originalPrice"));
                myGoods.setPresentPrice(jsonObject2.getDouble("presentPrice"));
                List<String> imgList=new ArrayList<>();

                //解析图片
                final JSONArray jsonArray1=new JSONArray(jsonObject2.getString("images"));
                for (int j = 0; j < jsonArray1.length(); j++) {
                    imgList.add(jsonArray1.getString(j));
                }
                myGoods.setImgList(imgList);

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
            listBeanAll.addAll(list);
            if(null==orderAdapter){
                orderAdapter=new OrderAdapter(getActivity(),listBeanAll);
                listView.setAdapter(orderAdapter);
            }else{
                orderAdapter.notifyDataSetChanged();
            }
            orderAdapter.setCallBack(tradingPlay);
            if(list.size()<20){
                isTotal=true;
                swipeLayout.setFooter(isTotal);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private TradingPlay tradingPlay=new TradingPlay() {
        /**
         * 交易完成
         * @param orderId
         */
        public void complete(String orderId) {
            if(TextUtils.isEmpty(orderId)){
                return;
            }
            dialogView = new DialogView(dialogView, getActivity(), "确定完成交易吗？",
                    "确定", "取消", new View.OnClickListener() {
                public void onClick(View v) {
                    dialogView.dismiss();
                }
            }, null);
            dialogView.show();
        }

        /**
         * 交易取消
         * @param orderId
         */
        public void cancle(String orderId) {
            if(TextUtils.isEmpty(orderId)){
                return;
            }
            dialogView = new DialogView(dialogView, getActivity(), "确定取消交易吗？",
                    "确定", "取消", new View.OnClickListener() {
                public void onClick(View v) {
                    dialogView.dismiss();
                }
            }, null);
            dialogView.show();
        }

        /**
         * 联系卖家
         * @param phone
         */
        public void playPhon(String phone) {

        }
    };

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoad() {

    }


    /**
     * 查询订单列表
     */
    private void getOrderList(){
        if(isVisibleToUser && view!=null && listBeanAll.size()==0){
            swipeLayout.postDelayed(new Runnable() {
                public void run() {
                    listView.addHeaderView(new View(getActivity()));
                    HttpMethod.getPayOrderList(OrderActivity.keyList.get(OrderActivity.index),mHandler);
                }
            }, 0);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser=isVisibleToUser;
        //查询订单列表
        getOrderList();
    }
}
