package com.youmai.project.activity.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.umeng.socialize.handler.UMWXHandler;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.media.WeiXinShareContent;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.map.RoutePlanActivity;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.utils.BitMapUtils;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.Util;
import com.youmai.project.utils.ZXingUtils;

/**
 * 分享的类
 */
public class ShareActivity extends BaseActivity implements View.OnClickListener{
    private ScrollView scShare;
    private ImageView imgQR;
    //分享渠道
    private SHARE_MEDIA share_media;
    private GoodsBean goodsBean;
    private Bitmap bitmap;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_share);
        initView();
        createQR();
    }

    /**
     * 初始化
     */
    private void initView(){
        goodsBean= (GoodsBean) getIntent().getSerializableExtra("goodsBean");
        scShare=(ScrollView)findViewById(R.id.sc_sv);
        imgQR=(ImageView)findViewById(R.id.img_qr);
        ImageView imageView=(ImageView)findViewById(R.id.img_sv_img);
        TextView tvDes=(TextView)findViewById(R.id.tv_sv_des);
        TextView tvMoney=(TextView)findViewById(R.id.tv_sv_money);
        TextView tvWei = (TextView) findViewById(R.id.tv_acd_wei);
        TextView tvPeng = (TextView) findViewById(R.id.tv_acd_peng);
        TextView tvLocation=(TextView)findViewById(R.id.tv_as_location);
        tvDes.setText(goodsBean.getDescription());
        tvMoney.setText("¥ "+ Util.setDouble(goodsBean.getPresentPrice()/100));
        Glide.with(mContext).load(goodsBean.getImgList().get(0)).override(375,283).centerCrop().error(R.mipmap.icon).into(imageView);
        //显示当前位置
        tvLocation.setText(MyApplication.spUtil.getString(SPUtil.LOCATION_ADDRESS));
        tvWei.setOnClickListener(this);
        tvPeng.setOnClickListener(this);
        tvLocation.setOnClickListener(this);
    }

    /**
     * 生成二维码
     */
    private void createQR(){
        final String url="http://q.th2w.net/g/"+goodsBean.getId();
        bitmap=ZXingUtils.createQRImage(url,150,150);
        imgQR.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_acd_wei:
                share_media = SHARE_MEDIA.WEIXIN;
                share();
                break;
            case R.id.tv_acd_peng:
                share_media = SHARE_MEDIA.WEIXIN_CIRCLE;
                share();
                break;
            case R.id.tv_as_location:
                 Intent intent=new Intent(mContext, RoutePlanActivity.class);
                 intent.putExtra("latitude",goodsBean.getLatitude());
                 intent.putExtra("longtitude",goodsBean.getLongitude());
                 startActivity(intent);
                 break;
            default:
                break;
        }
    }


    //截图分享的图片
    private void share(){
        bitmap= BitMapUtils.screenshot(scShare);
        startShare();
    }

    /**
     * 分享
     */
    private void startShare() {
        UMImage img = new UMImage(this, bitmap);
        new ShareAction(ShareActivity.this).setPlatform(share_media)
                .setCallback(umShareListener)
                .withMedia(img)
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=bitmap){
            bitmap.recycle();;
            bitmap=null;
        }
    }
}
