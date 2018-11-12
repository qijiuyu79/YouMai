package com.youmai.project.activity.photo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.youmai.project.application.MyApplication;
import com.youmai.project.R;
import com.youmai.project.http.HttpConstant;
import com.youmai.project.utils.photo.Bimp;
import com.youmai.project.view.TouchImageView;
/**
 * @author Administrator
 */

public class BigPhotoActivity extends Activity {
    private TouchImageView bigImage;
    private int id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.big_photo);
        Intent intent = getIntent();
        id = intent.getIntExtra("ID", 0);
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("图片详情");
        bigImage = (TouchImageView) findViewById(R.id.img_big_photo);
        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        for (int i = 0; i < Bimp.selectBitmap.size(); i++) {
            final String imgPath = Bimp.selectBitmap.get(i).getImagePath();
            if (id == i) {
                if (imgPath.indexOf(HttpConstant.IP) == -1) {
                        Glide.with(MyApplication.application).load("file://" + imgPath).diskCacheStrategy(DiskCacheStrategy.NONE).into(bigImage);
                } else {
                    Glide.with(MyApplication.application).load(imgPath).diskCacheStrategy(DiskCacheStrategy.NONE).into(bigImage);
                }
            }
        }
    }
}
