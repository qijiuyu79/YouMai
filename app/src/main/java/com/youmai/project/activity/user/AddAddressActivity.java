package com.youmai.project.activity.user;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;

/**
 * Created by Administrator on 2018/1/19 0019.
 */

public class AddAddressActivity extends BaseActivity implements View.OnClickListener{

    private EditText etName,etPhone,etHouseNum;
    private TextView tvAddress;
    private LatLng latLng;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_add_address);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(android.R.color.white);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("编辑地址");
        etName=(EditText)findViewById(R.id.et_name);
        etPhone=(EditText)findViewById(R.id.et_phone);
        tvAddress=(TextView)findViewById(R.id.tv_address);
        etHouseNum=(EditText)findViewById(R.id.tv_house_num);
        findViewById(R.id.tv_address).setOnClickListener(this);
        findViewById(R.id.tv_submit).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //选择地址
            case R.id.tv_address:
                 Intent intent=new Intent(mContext,SelectAddressActivity.class);
                 startActivityForResult(intent,1);
                 break;
            //提交
            case R.id.tv_submit:
                 final String name=etName.getText().toString().trim();
                 final String phone=etPhone.getText().toString().trim();
                 final String address=tvAddress.getText().toString().trim();
                 final String houseNum=etHouseNum.getText().toString().trim();
                 if(TextUtils.isEmpty(name)){
                     showMsg("请输入姓名！");
                     return;
                 }
                 if(TextUtils.isEmpty(phone)){
                    showMsg("请输入手机号！");
                    return;
                 }
                 if(TextUtils.isEmpty(address)){
                    showMsg("请选择地址！");
                    return;
                 }
                 if(TextUtils.isEmpty(houseNum)){
                    showMsg("请输入门牌号！");
                    return;
                 }
                 break;
            case R.id.lin_back:
                 finish();
                 break;
             default:
                 break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            final PoiInfo poiInfo=data.getParcelableExtra("poiInfo");
            if(null==poiInfo){
                return;
            }
            latLng=poiInfo.location;
            tvAddress.setText(poiInfo.name);
            etHouseNum.setText(poiInfo.address);
        }
    }
}