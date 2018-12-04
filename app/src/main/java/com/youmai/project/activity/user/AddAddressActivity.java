package com.youmai.project.activity.user;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.adapter.MyAddressAdapter;
import com.youmai.project.bean.Address;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.map.GetLocation;

/**
 * Created by Administrator on 2018/1/19 0019.
 */

public class AddAddressActivity extends BaseActivity implements View.OnClickListener{

    private EditText etName,etPhone,etHouseNum;
    private TextView tvAddress;
    private LatLng latLng;
    private Address.AddressBean addressBean;
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
        tintManager.setStatusBarTintResource(R.color.color_ffffff);
        initView();
        showData();
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


    /**
     * 展示要编辑的数据
     */
    private void showData(){
        addressBean= (Address.AddressBean) getIntent().getSerializableExtra("addressBean");
        if(null==addressBean){
            return;
        }
        etName.setText(addressBean.getName());
        etPhone.setText(addressBean.getMobile());
        tvAddress.setText(addressBean.getArea());
        etHouseNum.setText(addressBean.getAddress());
        latLng=new LatLng(addressBean.getLat(),addressBean.getLon());
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
                 showProgress("数据加载中");
                 if(null==addressBean){
                     HttpMethod.addAddress(name,phone,address,houseNum,String.valueOf(latLng.longitude),String.valueOf(latLng.latitude),mHandler);
                 }else{
                     HttpMethod.editAddress(addressBean.getIndex(),name,phone,address,houseNum,String.valueOf(latLng.longitude),String.valueOf(latLng.latitude),mHandler);
                 }
                 break;
            case R.id.lin_back:
                 finish();
                 break;
             default:
                 break;
        }
    }


    private Handler mHandler=new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            clearTask();
            switch (msg.what){
                case HandlerConstant.ADD_ADDRESS_SUCCESS:
                     Address address= (Address) msg.obj;
                     if(null==address){
                        break;
                     }
                     if(address.isSussess()){
                         Intent intent=new Intent();
                         intent.putExtra("address",address);
                         setResult(2,intent);
                         finish();
                     }else{
                         showMsg(address.getMsg());
                     }
                     break;
                case HandlerConstant.REQUST_ERROR:
                    showMsg(getString(R.string.http_error));
                    break;
                default:
                    break;
            }
            return false;
        }
    });


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