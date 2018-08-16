package com.youmai.project.activity.center;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.adapter.MyGoodsAdapter;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.view.RefreshLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加tag页面
 */
public class CenterActivity extends BaseActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener {

    private RefreshLayout swipeLayout;
    private ListView listView;
    private MyGoodsAdapter myGoodsAdapter;
    private List<GoodsBean> listAll=new ArrayList<>();
    private int page=1;
    private boolean isTotal=false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);
        initView();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        swipeLayout=(RefreshLayout)findViewById(R.id.swipe_container);
        listView=(ListView)findViewById(R.id.list);
        listView.setDividerHeight(0);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,
                R.color.color_bule,
                R.color.color_bule2,
                R.color.color_bule3);
        swipeLayout.setOnRefreshListener(CenterActivity.this);
        swipeLayout.setOnLoadListener(CenterActivity.this);
        swipeLayout.post(new Thread(new Runnable() {
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        }));
        swipeLayout.postDelayed(new Runnable() {
            public void run() {
                listView.addHeaderView(new View(CenterActivity.this));
                getMyGoodsList();
            }
        }, 0);
        findViewById(R.id.tv_ac_add).setOnClickListener(this);
        findViewById(R.id.tv_ac_order).setOnClickListener(this);
        findViewById(R.id.lin_ac_jiaoyi).setOnClickListener(this);
        findViewById(R.id.lin_ac_complete).setOnClickListener(this);
        findViewById(R.id.lin_ac_cancle).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //添加宝贝
            case R.id.tv_ac_add:
                 Intent intent=new Intent(CenterActivity.this,AddShopActivity.class);
                 startActivityForResult(intent,1);
                 break;
            //全部订单
            case R.id.tv_ac_order:
                 break;
            //待交易
            case R.id.lin_ac_jiaoyi:
                 break;
             //已完成
            case R.id.lin_ac_complete:
                 break;
             //已取消
            case R.id.lin_ac_cancle:
                 break;
                 default:
                     break;
        }

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
            List<GoodsBean> list=new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                GoodsBean myGoods=new GoodsBean();
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                myGoods.setAddress(jsonObject1.getString("address"));
                myGoods.setCreateTime(jsonObject1.getLong("createTime"));
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
                myGoodsAdapter=new MyGoodsAdapter(CenterActivity.this,listAll);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
        switch (requestCode){
            //添加商品后的返回
            case 1:
                Bundle bundle=data.getExtras();
                if(bundle==null){
                    return;
                }
                GoodsBean myGoods= (GoodsBean) bundle.getSerializable("myGoods");
                if(null!=myGoods){
                    listAll.add(0,myGoods);
                    if(null==myGoodsAdapter){
                        myGoodsAdapter=new MyGoodsAdapter(CenterActivity.this,listAll);
                        listView.setAdapter(myGoodsAdapter);
                    }else{
                        myGoodsAdapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
    }
}

