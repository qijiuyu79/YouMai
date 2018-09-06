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
import android.widget.TextView;

import com.youmai.project.R;
import com.youmai.project.activity.center.AddShopActivity;
import com.youmai.project.activity.center.CenterActivity;
import com.youmai.project.activity.main.BuyGoodsActivity;
import com.youmai.project.activity.main.GoodDetailsActivity;
import com.youmai.project.activity.main.MainActivity;
import com.youmai.project.activity.user.LoginActivity;
import com.youmai.project.adapter.RecommendedAdapter;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.JsonUtils;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.Util;
import com.youmai.project.view.ClickLinearLayout;
import com.youmai.project.view.RefreshLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * 推荐的fragment
 * Created by Administrator on 2018/1/3 0003.
 */

public class RecommendedFragment extends BaseFragment  implements SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener,AdapterView.OnItemClickListener {

    private ClickLinearLayout linAddShop;
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
        linAddShop=(ClickLinearLayout)view.findViewById(R.id.lin_addShop);
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
        List<GoodsBean> list=JsonUtils.getGoods(message);
        listBeanAll.addAll(list);
        if(null==recommendedAdapter){
            recommendedAdapter=new RecommendedAdapter(mActivity,listBeanAll);
            listView.setAdapter(recommendedAdapter);
        }else{
            recommendedAdapter.notifyDataSetChanged();
        }
        if(list.size()<20){
            isTotal=true;
            swipeLayout.setFooter(isTotal);
        }

        //判断是否展示添加商品按钮
        if(listBeanAll.size()>0){
            linAddShop.setVisibility(View.GONE);
        }else{
            linAddShop.setVisibility(View.VISIBLE);
            linAddShop.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(!Util.isLogin()){
                        setClass(LoginActivity.class);
                        return;
                    }
                    setClass(AddShopActivity.class);
                }
            });
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
        TextView textView=(TextView)view.findViewById(R.id.tv_ri_content);
        if(null==textView.getTag()){
            return;
        }
        GoodsBean goodsBean= (GoodsBean) textView.getTag();
        if(null==goodsBean){
            return;
        }
        Intent intent=new Intent(mActivity, GoodDetailsActivity.class);
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
                    listView.addHeaderView(new View(mActivity));
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
        myIntentFilter.addAction(CenterActivity.ACTION_GOODS_DELETE_SUCCESS);
        // 注册广播监听
        mActivity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //购买成功或删除商品的广播
            if(action.equals(BuyGoodsActivity.ACTION_GOODS_PAYSUCCESS) || action.equals(CenterActivity.ACTION_GOODS_DELETE_SUCCESS)){
                final Bundle bundle=intent.getExtras();
                if(null==bundle){
                    return;
                }
                final GoodsBean goodsBean= (GoodsBean) bundle.getSerializable("goodsBean");
                if(null==goodsBean){
                    return;
                }
                for(int i=0,len=listBeanAll.size();i<len;i++){
                    if(listBeanAll.get(i).getId().equals(goodsBean.getId())){
                        listBeanAll.remove(i);
                        break;
                    }
                }
                if(null!=recommendedAdapter){
                    recommendedAdapter.notifyDataSetChanged();
                }
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
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }

}
