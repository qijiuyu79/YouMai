package com.youmai.project.activity.center;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.share.ShareActivity;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.utils.BitMapUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.ZXingUtils;

/**
 * 宝贝添加成功
 */
public class AddShopSuccessActivity extends BaseActivity implements View.OnClickListener{

    private ImageView imgScan;
    //分享渠道
    private SHARE_MEDIA share_media;
    private GoodsBean goodsBean;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_success);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(android.R.color.white);
        initView();
        showImg();
    }


    /**
     * 初始化
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("添加成功");
        imgScan=(ImageView)findViewById(R.id.img_scan);
        findViewById(R.id.lin_share).setOnClickListener(this);
        findViewById(R.id.lin_save).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
    }


    /**
     * 展示二维码图片
     */
    private Bitmap bitmap,bm_logo;
    private void showImg(){
        goodsBean= (GoodsBean) getIntent().getSerializableExtra("goodsBean");
        final String url="http://q.th2w.net/g/"+goodsBean.getId();
        bitmap= ZXingUtils.createQRImage(url,200,200);
        bm_logo = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
        imgScan.setImageBitmap(ZXingUtils.addLogo(bitmap,bm_logo));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //分
            case R.id.lin_share:
                 Intent intent=new Intent(mContext,ShareActivity.class);
                 intent.putExtra("goodsBean",goodsBean);
                 startActivity(intent);
//                 View view= LayoutInflater.from(mContext).inflate(R.layout.share_pop,null);
//                 bottomPopupWindow(0,0,view);
//                 view.findViewById(R.id.img_share_wx).setOnClickListener(new View.OnClickListener() {
//                     public void onClick(View v) {
//                         share_media = SHARE_MEDIA.WEIXIN;
//                         share();
//                     }
//                 });
//                view.findViewById(R.id.img_share_wxp).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        share_media = SHARE_MEDIA.WEIXIN_CIRCLE;
//                        share();
//                    }
//                });
                 break;
            //保存在本地
            case R.id.lin_save:
                 BitMapUtils.saveImageToGallery(mContext,bitmap);
                 View view2= LayoutInflater.from(mContext).inflate(R.layout.save_photo,null);
                 dialogPop(view2,true);
                 break;
            case R.id.lin_back:
                 finish();
                 break;
             default:
                 break;
        }

    }


    /**
     * 开始分享
     */
    private void share(){
        UMImage img = new UMImage(this, bitmap);
        new ShareAction(AddShopSuccessActivity.this).setPlatform(share_media)
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
        if(null!=bm_logo){
            bm_logo.recycle();;
            bm_logo=null;
        }
    }
}
