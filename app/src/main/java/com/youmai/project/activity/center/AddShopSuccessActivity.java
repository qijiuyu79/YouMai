package com.youmai.project.activity.center;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.utils.BitMapUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.ZXingUtils;

/**
 * 宝贝添加成功
 */
public class AddShopSuccessActivity extends BaseActivity implements View.OnClickListener{

    private ImageView imgScan;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_success);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.gray_bg);
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
        findViewById(R.id.tv_save).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
    }


    /**
     * 展示二维码图片
     */
    private Bitmap bitmap;
    private void showImg(){
        String goodId=getIntent().getStringExtra("goodId");
        final String url="http://q.th2w.net/g/"+goodId;
        bitmap= ZXingUtils.createQRImage(url,179,179);
        imgScan.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_save:
                 BitMapUtils.saveImageToGallery(mContext,bitmap);
                 showMsg("已保存在手机本地！");
                 break;
            case R.id.lin_back:
                 finish();
                 break;
                 default:
                     break;
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
