package com.youmai.project.fragment.order;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.youmai.project.R;
import com.youmai.project.activity.order.MOrderActivity;
import com.youmai.project.adapter.MOrderAdapter;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.fragment.BaseFragment;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.JsonUtils;
import com.youmai.project.view.RefreshLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * 店铺订单
 * Created by Administrator on 2018/1/3 0003.
 */

public class MOrderFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener {

    private RefreshLayout swipeLayout;
    private ListView listView;
    private MOrderAdapter mOrderAdapter;
    private List<GoodsBean> listBeanAll=new ArrayList<>();
    private List<String> keyList=new ArrayList<>();
    private boolean isTotal=false;
    //fragment是否可见
    private boolean isVisibleToUser=false;
    //页码
    private int page=1;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(keyList.size()==0){
            keyList.add(null);
            keyList.add("1");
            keyList.add("2");
            keyList.add("4");
        }
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
        swipeLayout.setOnRefreshListener(MOrderFragment.this);
        swipeLayout.setOnLoadListener(MOrderFragment.this);
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
            clearTask();
            String message;
            switch (msg.what){
                case HandlerConstant.GET_M_ORDER_SUCCESS:
                    message= (String) msg.obj;
                    listBeanAll.clear();
                    refresh(message);
                    swipeLayout.setRefreshing(false);
                    break;
                case HandlerConstant.GET_M_ORDER_SUCCESS2:
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
    List<GoodsBean> list;
    private void refresh(String message){
        list= JsonUtils.getGoods2(message);
        listBeanAll.addAll(list);
        if(null==mOrderAdapter){
            mOrderAdapter=new MOrderAdapter(mActivity,listBeanAll);
            listView.setAdapter(mOrderAdapter);
        }else{
            mOrderAdapter.notifyDataSetChanged();
        }
        if(list.size()<20){
            isTotal=true;
            swipeLayout.setFooter(isTotal);
        }
    }


    /**
     * 下拉刷新
     */
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {
            public void run() {
                page=1;
                isTotal=false;
                swipeLayout.setFooter(isTotal);
                getData(HandlerConstant.GET_M_ORDER_SUCCESS);
            }
        }, 200);
    }


    /**
     * 上啦加载更多
     */
    public void onLoad() {
        if(isTotal){
            swipeLayout.setLoading(false);
            return;
        }
        swipeLayout.postDelayed(new Runnable() {
            public void run() {
                page++;
                getData(HandlerConstant.GET_M_ORDER_SUCCESS2);
            }
        }, 200);

    }


    /**
     * 查询订单列表
     */
    private void getOrderList(){
        if(isVisibleToUser && view!=null && listBeanAll.size()==0){
            swipeLayout.postDelayed(new Runnable() {
                public void run() {
                    listView.addHeaderView(new View(mActivity));
                    getData(HandlerConstant.GET_M_ORDER_SUCCESS);
                }
            }, 0);
        }
    }

    private void getData(int handlerIndex){
        HttpMethod.getMOrderList(keyList.get(MOrderActivity.index),page,handlerIndex,mHandler);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser=isVisibleToUser;
        //查询订单列表
        getOrderList();
    }

    public void onDestroy() {
        super.onDestroy();
        removeHandler(mHandler);
    }
}
