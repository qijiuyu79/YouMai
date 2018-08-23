package com.youmai.project.activity.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.map.SellerGoodsActivity;
import com.youmai.project.adapter.GoodsListAdapter;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.BitMapUtils;
import com.youmai.project.utils.JsonUtils;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.Util;
import com.youmai.project.view.MyGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 宝贝详情
 */
public class GoodDetailsActivity extends BaseActivity implements View.OnClickListener{

    private ScrollView scrollView;
    private GoodsBean goodsBean;
    private MyGridView myGridView;
    private List<GoodsBean> listBeanAll=new ArrayList<>();
    private GoodsListAdapter goodsListAdapter;
    //分享渠道
    private SHARE_MEDIA share_media;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_good_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FF4081);
        initView();
        //根据storeId查询卖家商品
        getGoods();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            goodsBean= (GoodsBean) bundle.getSerializable("goodsBean");
        }
        scrollView=(ScrollView)findViewById(R.id.scrollView_agd);
        TextView tvNickName=(TextView)findViewById(R.id.tv_agd_nickName);
        ImageView imageView=(ImageView)findViewById(R.id.img_agd_img);
        TextView tvCount=(TextView)findViewById(R.id.tv_agd_count);
        TextView tvContent=(TextView)findViewById(R.id.tv_agd_content);
        TextView tvPresentPrice=(TextView)findViewById(R.id.tv_agd_presentPrice);
        TextView tvAddress=(TextView)findViewById(R.id.tv_agd_address);
        myGridView=(MyGridView)findViewById(R.id.mg_agd_goods);
        tvNickName.setText(goodsBean.getNickname());
        if(null!=goodsBean.getImgList() && goodsBean.getImgList().size()>0){
            Glide.with(mContext).load(goodsBean.getImgList().get(0)).centerCrop().error(R.mipmap.icon).into(imageView);
        }
        tvCount.setText("1/"+goodsBean.getImgList().size());
        tvContent.setText(goodsBean.getDescription());
        tvPresentPrice.setText("现价："+ Util.setDouble(goodsBean.getPresentPrice()/100));
        tvAddress.setText(goodsBean.getAddress());
        imageView.setOnClickListener(this);
        findViewById(R.id.tv_agd_buy).setOnClickListener(this);
        findViewById(R.id.lin_search).setOnClickListener(this);
        findViewById(R.id.lin_agd_share).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
    }


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HandlerConstant.GET_GOODS_BY_STOREID_SUCCESS:
                     final String message=msg.obj.toString();
                     parsingData(message);
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
     * 解析数据
     * @param msg
     */
    private void parsingData(String msg){
        List<GoodsBean> list= JsonUtils.getGoods(msg);
        listBeanAll.addAll(list);
        goodsListAdapter=new GoodsListAdapter(mContext,listBeanAll);
        myGridView.setAdapter(goodsListAdapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        switch (v.getId()){
            case R.id.img_agd_img:
                 intent.setClass(mContext,ShowImgActivity.class);
                 intent.putExtra("imgs", MyApplication.gson.toJson(goodsBean.getImgList()));
                 startActivity(intent);
                 break;
            //购买
            case R.id.tv_agd_buy:
                 intent.setClass(mContext,BuyGoodsActivity.class);
                 bundle.putSerializable("goodsBean",goodsBean);
                 intent.putExtras(bundle);
                 startActivity(intent);
                 break;
            //查看更多的卖家商品
            case R.id.lin_search:
                 intent.setClass(mContext, SellerGoodsActivity.class);
                 bundle.putSerializable("goodsBean",goodsBean);
                 intent.putExtras(bundle);
                 startActivity(intent);
                 break;
            //分享
            case R.id.lin_agd_share:
                 shareWindow();
                 break;
            case R.id.tv_acd_wei:
                share_media = SHARE_MEDIA.WEIXIN;
                share();
                break;
            case R.id.tv_acd_peng:
                share_media = SHARE_MEDIA.WEIXIN_CIRCLE;
                share();
                break;
            case R.id.tv_acd_qq:
                share_media = SHARE_MEDIA.QQ;
                share();
                break;
            case R.id.tv_acd_bo:
                share_media = SHARE_MEDIA.SINA;
                share();
                break;
            case R.id.tv_acd_kong:
                share_media = SHARE_MEDIA.QZONE;
                share();
                break;
            case R.id.lin_back:
                 finish();
                 break;
             default:
                 break;
        }

    }

    /**
     * 根据storeId查询卖家商品
     */
    private void getGoods(){
        if(null==goodsBean){
            return;
        }
        HttpMethod.getGoodsByStoreId(1,goodsBean.getStoreId(),HandlerConstant.GET_GOODS_BY_STOREID_SUCCESS,mHandler);
    }


    /**
     * 弹出分享框
     */
    private void shareWindow() {
        View shareView = LayoutInflater.from(GoodDetailsActivity.this).inflate(R.layout.share, null);
        bottomPopupWindow(0, 0, shareView);
        TextView tvWei = (TextView) shareView.findViewById(R.id.tv_acd_wei);
        TextView tvPeng = (TextView) shareView.findViewById(R.id.tv_acd_peng);
        TextView tvQQ = (TextView) shareView.findViewById(R.id.tv_acd_qq);
        TextView tvBo = (TextView) shareView.findViewById(R.id.tv_acd_bo);
        TextView tvKong = (TextView) shareView.findViewById(R.id.tv_acd_kong);
        tvWei.setOnClickListener(this);
        tvPeng.setOnClickListener(this);
        tvQQ.setOnClickListener(this);
        tvBo.setOnClickListener(this);
        tvKong.setOnClickListener(this);
    }


    //截图分享的图片
    private Bitmap bitmap;
    private void share(){
        bitmap= BitMapUtils.screenshot(scrollView);
        startShare();
    }


    /**
     * 分享
     */
    private void startShare() {
        UMImage img = new UMImage(this, bitmap);
        UMWeb web = new UMWeb("http://www.baidu.com");
        web.setTitle("有卖");
        web.setDescription("买卖闲置商品，请选择有卖");
        web.setThumb(img);
        new ShareAction(GoodDetailsActivity.this).setPlatform(share_media)
                .setCallback(umShareListener)
                .withMedia(web)
                .share();
    }


    private UMShareListener umShareListener = new UMShareListener() {
        public void onStart(SHARE_MEDIA share_media) {
        }

        public void onResult(SHARE_MEDIA platform) {
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                showMsg(getString(R.string.collect_success));
            } else {
                showMsg(getString(R.string.share_success));
            }
        }

        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (t.getMessage().indexOf("2008") != -1) {
                if (platform.name().equals("WEIXIN") || platform.name().equals("WEIXIN_CIRCLE")) {
                    showMsg(getString(R.string.share_failed_install_wechat));
                } else if (platform.name().equals("QQ") || platform.name().equals("QZONE")) {
                    showMsg(getString(R.string.share_failed_install_qq));
                }
            }
            showMsg(getString(R.string.share_failed));
        }

        public void onCancel(SHARE_MEDIA platform) {
            showMsg(getString(R.string.share_canceled));
        }
    };


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0) {
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        }
    }
}
