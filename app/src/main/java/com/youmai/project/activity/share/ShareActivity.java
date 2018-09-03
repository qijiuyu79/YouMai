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
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.utils.BitMapUtils;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.Util;
/**
 * 分享的类
 */
public class ShareActivity extends BaseActivity implements View.OnClickListener{
    private ScrollView scShare;
    //分享渠道
    private SHARE_MEDIA share_media;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_share);
        initView();
    }

    /**
     * 初始化
     */
    private void initView(){
        Bundle bundle=getIntent().getExtras();
        GoodsBean goodsBean= (GoodsBean) bundle.getSerializable("goodsBean");
        scShare=(ScrollView)findViewById(R.id.sc_sv);
        ImageView imageView=(ImageView)findViewById(R.id.img_sv_img);
        TextView tvDes=(TextView)findViewById(R.id.tv_sv_des);
        TextView tvMoney=(TextView)findViewById(R.id.tv_sv_money);
        TextView tvWei = (TextView) findViewById(R.id.tv_acd_wei);
        TextView tvPeng = (TextView) findViewById(R.id.tv_acd_peng);
        tvDes.setText(goodsBean.getDescription());
        tvMoney.setText("¥"+ Util.setDouble(goodsBean.getPresentPrice()/100));
        Glide.with(mContext).load(goodsBean.getImgList().get(0)).override(375,283).centerCrop().error(R.mipmap.icon).into(imageView);
        tvWei.setOnClickListener(this);
        tvPeng.setOnClickListener(this);
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
            default:
                break;
        }
    }


    //截图分享的图片
    private Bitmap bitmap;
    private void share(){
        bitmap= BitMapUtils.screenshot(scShare);
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
        new ShareAction(ShareActivity.this).setPlatform(share_media)
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
