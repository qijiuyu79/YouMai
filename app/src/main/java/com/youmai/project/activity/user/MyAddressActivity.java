package com.youmai.project.activity.user;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.adapter.MyAddressAdapter;
import com.youmai.project.utils.SystemBarTintManager;

/**
 * 我的地址
 * Created by Administrator on 2018/1/19 0019.
 */

public class MyAddressActivity extends BaseActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_address);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FF4081);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        ListView listView=(ListView)findViewById(R.id.listView);
        MyAddressAdapter myAddressAdapter=new MyAddressAdapter(mContext);
        listView.setAdapter(myAddressAdapter);

        findViewById(R.id.lin_aa_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClass(AddAddressActivity.class);
            }
        });

        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyAddressActivity.this.finish();
            }
        });

    }
}
