package com.youmai.project.activity.photo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youmai.project.application.MyApplication;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;

import java.util.List;


/**
 * @author Administrator
 */

public class NetBigPhotoActivity extends BaseActivity {

    private ImageView bigImage;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.big_photo);
        final String imgUrl=getIntent().getStringExtra("imgUrl");
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("选择图片");
        bigImage = (ImageView) findViewById(R.id.img_big_photo);
        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        Glide.with(MyApplication.application).load(imgUrl).diskCacheStrategy(DiskCacheStrategy.NONE).into(bigImage);
    }
}
