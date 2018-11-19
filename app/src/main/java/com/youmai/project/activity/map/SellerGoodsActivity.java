package com.youmai.project.activity.map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.main.BuyGoodsActivity;
import com.youmai.project.activity.main.GoodDetailsActivity;
import com.youmai.project.activity.order.CommentListActivity;
import com.youmai.project.adapter.SellerGoodsAdapter;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.JsonUtils;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.view.RefreshLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * 卖家店铺商品
 */
public class SellerGoodsActivity extends BaseActivity  implements SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener {

    private TextView tvComment,tvNickName;
    private RefreshLayout swipeLayout;
    private ListView listView;
    private ImageView imgX1,imgX2,imgX3,imgX4,imgX5;
    private GoodsBean goodsBean;
    private List<GoodsBean> listAll=new ArrayList<>();
    private int page=1;
    private int row=20;
    private boolean isTotal=false;
    private SellerGoodsAdapter sellerGoodsAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_seller_goods);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FF4081);
        initView();
        //注册广播
        registerReceiver();
    }


    /**
     * 初始化
     */
    private void initView(){
        goodsBean= (GoodsBean) getIntent().getSerializableExtra("goodsBean");
        tvNickName=(TextView)findViewById(R.id.tv_asg_name);
        imgX1=(ImageView)findViewById(R.id.img_au_x1);
        imgX2=(ImageView)findViewById(R.id.img_au_x2);
        imgX3=(ImageView)findViewById(R.id.img_au_x3);
        imgX4=(ImageView)findViewById(R.id.img_au_x4);
        imgX5=(ImageView)findViewById(R.id.img_au_x5);
        tvComment=(TextView)findViewById(R.id.tv_ac_evaluation);
        swipeLayout=(RefreshLayout)findViewById(R.id.swipe_container);
        listView=(ListView)findViewById(R.id.list);
        listView.setDividerHeight(0);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,
                R.color.color_bule,
                R.color.color_bule2,
                R.color.color_bule3);
        swipeLayout.setOnRefreshListener(SellerGoodsActivity.this);
        swipeLayout.setOnLoadListener(SellerGoodsActivity.this);
        swipeLayout.post(new Thread(new Runnable() {
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        }));
        swipeLayout.postDelayed(new Runnable() {
            public void run() {
                listView.addHeaderView(new View(SellerGoodsActivity.this));
                getGoodsList(HandlerConstant.GET_STORE_INFO_SUCCESS);
            }
        }, 0);

        //进入评价列表
        tvComment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(mContext,CommentListActivity.class);
                intent.putExtra("nickName",goodsBean.getNickname());
                intent.putExtra("storeId",goodsBean.getStoreId());
                intent.putExtra("creditLevel",goodsBean.getCreditLevel());
                startActivity(intent);
            }
        });

        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SellerGoodsActivity.this.finish();
            }
        });
    }


    /**
     * 设置星级
     */
    private List<ImageView> imgList=new ArrayList<>();
    private void setXing(int index){
        imgList.add(imgX1);
        imgList.add(imgX2);
        imgList.add(imgX3);
        imgList.add(imgX4);
        imgList.add(imgX5);
        for (int i=0;i<imgList.size();i++){
            if(i<index){
                imgList.get(i).setImageDrawable(getResources().getDrawable(R.mipmap.yes_hselect_x));
            }else{
                imgList.get(i).setImageDrawable(getResources().getDrawable(R.mipmap.no_hselect_x));
            }
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message;
            switch (msg.what){
                case HandlerConstant.GET_STORE_INFO_SUCCESS:
                    message= (String) msg.obj;
                    listAll.clear();
                    refresh(message);
                    swipeLayout.setRefreshing(false);
                    break;
                case HandlerConstant.GET_STORE_INFO_SUCCESS2:
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
        List<GoodsBean> list= JsonUtils.getMapGoods(message);
        listAll.addAll(list);
        if(listAll.size()==0){
            return;
        }
        //设置昵称
        tvNickName.setText(listAll.get(0).getNickname());
        //设置星级
        setXing(listAll.get(0).getCreditLevel());
        //评论人数
        tvComment.setText(listAll.get(0).getCommentCount()+"人评论");

        if(null==sellerGoodsAdapter){
            sellerGoodsAdapter=new SellerGoodsAdapter(mContext,listAll);
            listView.setAdapter(sellerGoodsAdapter);
        }else{
            sellerGoodsAdapter.notifyDataSetChanged();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView=(TextView)view.findViewById(R.id.tv_sgi_address);
                if(null==textView.getTag()){
                    return;
                }
                GoodsBean goodsBean= (GoodsBean) textView.getTag();
                if(null==goodsBean){
                    return;
                }
                Intent intent=new Intent(mContext, GoodDetailsActivity.class);
                intent.putExtra("goodsBean",goodsBean);
                startActivity(intent);
            }
        });
        if(list.size()<20){
            isTotal=true;
            swipeLayout.setFooter(isTotal);
        }
    }


    /**
     * 查询商品列表
     * @param index
     */
    private void getGoodsList(int index){
        HttpMethod.getStoreInfo(goodsBean.getStoreId(),page,row,index,mHandler);
    }


    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {
            public void run() {
                page=1;
                isTotal=false;
                swipeLayout.setFooter(isTotal);
                getGoodsList(HandlerConstant.GET_STORE_INFO_SUCCESS);
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
                getGoodsList(HandlerConstant.GET_STORE_INFO_SUCCESS2);
            }
        }, 200);

    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BuyGoodsActivity.ACTION_GOODS_PAYSUCCESS);
        // 注册广播监听
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                //商品购买成功后的广播
                case BuyGoodsActivity.ACTION_GOODS_PAYSUCCESS:
                    final GoodsBean goodsBean= (GoodsBean) intent.getSerializableExtra("goodsBean");
                    if(null==goodsBean){
                        return;
                    }
                    for(int i=0,len=listAll.size();i<len;i++){
                        if(listAll.get(i).getId().equals(goodsBean.getId())){
                            listAll.remove(i);
                            break;
                        }
                    }
                    sellerGoodsAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        removeHandler(mHandler);
    }
}
