package com.youmai.project.activity.center;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.order.CommentListActivity;
import com.youmai.project.activity.order.MOrderActivity;
import com.youmai.project.activity.user.CertificationActivity;
import com.youmai.project.adapter.MyGoodsAdapter;
import com.youmai.project.application.MyApplication;
import com.youmai.project.callback.DeleteBabyCallBack;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.bean.HttpBaseBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.view.DialogView;
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
    //宝贝id
    private String goodsId;
    private DialogView dialogView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);
        initView();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvNickName=(TextView)findViewById(R.id.tv_ac_name);
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
                getMyGoodsList(HandlerConstant.GET_MYGOODS_SUCCESS);
            }
        }, 0);
        tvNickName.setText(MyApplication.userInfoBean.getNickname());
        findViewById(R.id.lin_ac_add).setOnClickListener(this);
        findViewById(R.id.lin_sign_in).setOnClickListener(this);
        findViewById(R.id.tv_ac_order).setOnClickListener(this);
        findViewById(R.id.lin_ac_jiaoyi).setOnClickListener(this);
        findViewById(R.id.lin_ac_complete).setOnClickListener(this);
        findViewById(R.id.lin_ac_cancle).setOnClickListener(this);
        findViewById(R.id.tv_ac_evaluation).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        switch (v.getId()){
            //评价
            case R.id.tv_ac_evaluation:
                 if(listAll.size()==0){
                     showMsg("请先添加宝贝！");
                     return;
                 }
                 intent.setClass(mContext,CommentListActivity.class);
                 intent.putExtra("storeId",listAll.get(0).getStoreId());
                 startActivity(intent);
                 break;
            //添加宝贝
            case R.id.lin_ac_add:
                 if(!MyApplication.userInfoBean.isReal()){
                     dialogView = new DialogView(dialogView, mContext, "需要实名认证后才可以添加宝贝！",
                             "去认证", "取消", new View.OnClickListener() {
                         public void onClick(View v) {
                             dialogView.dismiss();
                             setClass(CertificationActivity.class);
                         }
                     }, null);
                     dialogView.show();
                     return;
                 }
                 intent.setClass(CenterActivity.this,AddShopActivity.class);
                 startActivityForResult(intent,1);
                 break;
            //店铺签到
            case R.id.lin_sign_in:
                 showProgress("签到中...");
                 HttpMethod.storeEvaluate(mHandler);
                 break;
            //全部订单
            case R.id.tv_ac_order:
                intent.setClass(CenterActivity.this, MOrderActivity.class);
                intent.putExtra("type",0);
                startActivity(intent);
                 break;
            //待交易
            case R.id.lin_ac_jiaoyi:
                intent.setClass(CenterActivity.this,MOrderActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
                 break;
             //已完成
            case R.id.lin_ac_complete:
                 intent.setClass(CenterActivity.this,MOrderActivity.class);
                 intent.putExtra("type",2);
                 startActivity(intent);
                 break;
             //已取消
            case R.id.lin_ac_cancle:
                intent.setClass(CenterActivity.this,MOrderActivity.class);
                intent.putExtra("type",3);
                startActivity(intent);
                 break;
                 default:
                     break;
        }

    }


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            String message;
            HttpBaseBean httpBaseBean;
            switch (msg.what){
                case HandlerConstant.GET_MYGOODS_SUCCESS:
                    message= (String) msg.obj;
                    listAll.clear();
                    refresh(message);
                    swipeLayout.setRefreshing(false);
                    break;
                case HandlerConstant.GET_MYGOODS_SUCCESS2:
                    message= (String) msg.obj;
                    refresh(message);
                    swipeLayout.setLoading(false);
                    break;
                //店铺签到
                case HandlerConstant.STORE_EVALUATE_SUCCESS:
                     httpBaseBean= (HttpBaseBean) msg.obj;
                     if(null==httpBaseBean){
                        return;
                     }
                     if(httpBaseBean.isSussess()){
                        showMsg("签到成功！");
                     }else{
                         showMsg(httpBaseBean.getMsg());
                     }
                     break;
                 //删除宝贝
                case HandlerConstant.DELETE_BABY_SUCCESS:
                      httpBaseBean= (HttpBaseBean) msg.obj;
                      if(null==httpBaseBean){
                         return;
                      }
                      if(httpBaseBean.isSussess()){
                         for(int i=0;i<listAll.size();i++){
                             if(listAll.get(i).getId().equals(goodsId)){
                                 listAll.remove(i);
                                 myGoodsAdapter.notifyDataSetChanged();
                                 break;
                             }
                         }
                         showMsg("删除成功！");
                     }
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
            if(jsonObject.getInt("code")!=200){
                return;
            }
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
            myGoodsAdapter.setCallBack(deleteBabyCallBack);
            if(list.size()<20){
                isTotal=true;
                swipeLayout.setFooter(isTotal);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    DeleteBabyCallBack deleteBabyCallBack=new DeleteBabyCallBack() {
        /**
         * 删除我的宝贝回调
         * @param goodsId
         */
        public void deleteBaby(final String goodsId) {
            if(TextUtils.isEmpty(goodsId)){
                return;
            }
            CenterActivity.this.goodsId=goodsId;
            dialogView = new DialogView(dialogView,mContext, "确定删除宝贝吗？",
                    "确定", "取消", new View.OnClickListener() {
                public void onClick(View v) {
                    dialogView.dismiss();
                    showProgress("删除中...");
                    HttpMethod.deleteBaby(goodsId,mHandler);
                }
            }, null);
            dialogView.show();
        }
    };

    /**
     * 获取我的商品
     */
    private void getMyGoodsList(int index){
        HttpMethod.getMyGoodsList(page,index,mHandler);
    }

    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {
            public void run() {
                page=1;
                isTotal=false;
                swipeLayout.setFooter(isTotal);
                getMyGoodsList(HandlerConstant.GET_MYGOODS_SUCCESS);
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
                getMyGoodsList(HandlerConstant.GET_MYGOODS_SUCCESS2);
            }
        }, 200);

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

