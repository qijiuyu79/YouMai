package com.youmai.project.fragment.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.youmai.project.bean.HttpBaseBean;
import com.youmai.project.callback.TradingPlay;
import com.youmai.project.fragment.BaseFragment;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.JsonUtils;
import com.youmai.project.view.DialogView;
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
    private List<GoodsBean> listComplete=new ArrayList<>();
    private List<GoodsBean> listCancle=new ArrayList<>();
    private List<GoodsBean> listWait=new ArrayList<>();
    private List<String> keyList=new ArrayList<>();
    private boolean isTotal=false;
    //fragment是否可见
    private boolean isVisibleToUser=false;
    //页码
    private int page=1;
    //此刻是第几个fragment
    private int fragmentIndex;
    private GoodsBean goodsBean;
    private DialogView dialogView;
    //交易取消广播
    private final static String ACTION_MYGOODS_CANCEL_SUCCESS = "net.youmai.adminapp.action.mygoods.cancel.success";
    //删除订单广播
    private final static String ACTION_DELETE_MORDER_SUCCESS = "net.youmai.adminapp.action.delete_morder.success";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(keyList.size()==0){
            keyList.add(null);
            keyList.add("1");
            keyList.add("2");
            keyList.add("4");
        }
        //注册广播
        registerReceiver();
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
                    setDataByIndex(1);
                    refresh(message);
                    swipeLayout.setRefreshing(false);
                    break;
                case HandlerConstant.GET_M_ORDER_SUCCESS2:
                    message= (String) msg.obj;
                    refresh(message);
                    swipeLayout.setLoading(false);
                    break;
                //交易取消
                case HandlerConstant.SET_ORDER_CANCLE_SUCCESS:
                //删除订单
                case HandlerConstant.DELETE_ORDER_SUCCESS:
                    HttpBaseBean httpBaseBean= (HttpBaseBean) msg.obj;
                    if(null==httpBaseBean){
                        return;
                    }
                    if(!httpBaseBean.isSussess()){
                        showMsg(httpBaseBean.getMsg());
                        return;
                    }
                    Intent intent=new Intent();
                    intent.putExtra("goodsBean",goodsBean);
                    switch (msg.what){
                        case HandlerConstant.SET_ORDER_CANCLE_SUCCESS:
                             intent.setAction(ACTION_MYGOODS_CANCEL_SUCCESS);
                             break;
                        case HandlerConstant.DELETE_ORDER_SUCCESS:
                             intent.setAction(ACTION_DELETE_MORDER_SUCCESS);
                            break;
                        default:
                            break;
                    }
                    mActivity.sendBroadcast(intent);
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
        setDataByIndex(0);
        if(null==mOrderAdapter){
            setDataByIndex(2);
            listView.setAdapter(mOrderAdapter);
        }else{
            mOrderAdapter.notifyDataSetChanged();
        }
        mOrderAdapter.setCallBack(tradingPlay);
        if(list.size()<20){
            isTotal=true;
            swipeLayout.setFooter(isTotal);
        }
    }


    private void setDataByIndex(int type){
        switch (fragmentIndex){
            case 0:
                if(type==0){
                    listBeanAll.addAll(list);
                }else if(type==1){
                    listBeanAll.clear();
                }else if(type==2){
                    mOrderAdapter=new MOrderAdapter(mActivity,listBeanAll);
                }
                break;
            case 1:
                if(type==0){
                    listWait.addAll(list);
                }else if(type==1){
                    listWait.clear();
                }else if(type==2){
                    mOrderAdapter=new MOrderAdapter(mActivity,listWait);
                }
                break;
            case 2:
                if(type==0){
                    listComplete.addAll(list);
                }else if(type==1){
                    listComplete.clear();
                }else if(type==2){
                    mOrderAdapter=new MOrderAdapter(mActivity,listComplete);
                }
                break;
            case 3:
                if(type==0){
                    listCancle.addAll(list);
                }else if(type==1){
                    listCancle.clear();
                }else if(type==2){
                    mOrderAdapter=new MOrderAdapter(mActivity,listCancle);
                }
                break;
            default:
                break;
        }
    }


    private TradingPlay tradingPlay=new TradingPlay() {
        public void complete(final GoodsBean goodsBean) {
        }
        /**
         * 交易取消/删除订单
         * @param goodsBean
         */
        public void cancle(final GoodsBean goodsBean) {
            MOrderFragment.this.goodsBean=goodsBean;
            switch (goodsBean.getStated()){
                //取消交易
                case 1:
                     dialogView = new DialogView(dialogView, mActivity, "确定取消交易吗？",
                            "确定", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            showProgress("设置中...");
                            HttpMethod.setOrderCancle(goodsBean.getOrderId(),mHandler);
                        }
                     }, null);
                     dialogView.show();
                     break;
                //删除订单
                case 4:
                    dialogView = new DialogView(dialogView, mActivity, "确定删除该订单数据？",
                            "确定", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            showProgress("删除订单中...");
                            HttpMethod.deleteOrder(goodsBean.getOrderId(),mHandler);
                        }
                    }, null);
                    dialogView.show();
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION_MYGOODS_CANCEL_SUCCESS);
        myIntentFilter.addAction(ACTION_DELETE_MORDER_SUCCESS);
        // 注册广播监听
        mActivity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            goodsBean= (GoodsBean) intent.getSerializableExtra("goodsBean");
            if(null==goodsBean){
                return;
            }
            final String action = intent.getAction();
            switch (action){
                //交易取消
                case ACTION_MYGOODS_CANCEL_SUCCESS:
                     if(listCancle.size()>0){
                        goodsBean.setStated(4);
                        listCancle.add(0,goodsBean);
                     }

                     for(int i=0;i<listBeanAll.size();i++){
                        if(goodsBean.getOrderId().equals(listBeanAll.get(i).getOrderId())){
                            listBeanAll.get(i).setStated(4);
                            break;
                        }
                     }
                     if(null!=mOrderAdapter){
                         mOrderAdapter.notifyDataSetChanged();
                     }
                    break;
                //删除订单
                case ACTION_DELETE_MORDER_SUCCESS:
                     for(int i=0;i<listBeanAll.size();i++){
                        if(goodsBean.getOrderId().equals(listBeanAll.get(i).getOrderId())){
                            listBeanAll.remove(i);
                            break;
                        }
                     }
                     for(int i=0;i<listCancle.size();i++){
                        if(goodsBean.getOrderId().equals(listCancle.get(i).getOrderId())){
                            listCancle.remove(i);
                            break;
                        }
                     }
                    if(null!=mOrderAdapter){
                        mOrderAdapter.notifyDataSetChanged();
                    }
                     break;
                default:
                    break;
            }
        }
    };


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
        if(isVisibleToUser && view!=null){
            swipeLayout.postDelayed(new Runnable() {
                public void run() {
                    fragmentIndex= MOrderActivity.index;
                    if(fragmentIndex==0 && listBeanAll.size()>0){
                        return;

                    }else if(fragmentIndex==1 && listWait.size()>0){
                        return;

                    }else if(fragmentIndex==2 && listComplete.size()>0){
                        return;

                    }else if(fragmentIndex==3 && listCancle.size()>0){
                        return;
                    }
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
        mActivity.unregisterReceiver(mBroadcastReceiver);
        removeHandler(mHandler);
    }
}
