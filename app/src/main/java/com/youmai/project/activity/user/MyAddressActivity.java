package com.youmai.project.activity.user;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.adapter.MyAddressAdapter;
import com.youmai.project.bean.Address;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
/**
 * 我的地址
 * Created by Administrator on 2018/1/19 0019.
 */

public class MyAddressActivity extends BaseActivity {

    private ListView listView;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_address);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FF4081);
        initView();
        getAddressList();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        listView=(ListView)findViewById(R.id.listView);
        //新增收获地址
        findViewById(R.id.lin_add).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(mContext,AddAddressActivity.class);
                startActivityForResult(intent,2);
            }
        });

        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyAddressActivity.this.finish();
            }
        });

    }


    private Handler mHandler=new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            clearTask();
            switch (msg.what){
                case HandlerConstant.GET_ADDRESS_LIST_SUCCESS:
                     Address address= (Address) msg.obj;
                     if(null==address){
                         break;
                     }
                     if(address.isSussess()){
                         MyAddressAdapter myAddressAdapter=new MyAddressAdapter(MyAddressActivity.this,address.getData());
                         listView.setAdapter(myAddressAdapter);
                         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                             }
                         });
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


    /**
     * 查询地址列表数据
     */
    private void getAddressList(){
        showProgress("数据加载中");
        HttpMethod.getAddressList(mHandler);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2){
            final Address address= (Address) data.getSerializableExtra("address");
            if(null==address){
                return;
            }
            MyAddressAdapter myAddressAdapter=new MyAddressAdapter(MyAddressActivity.this,address.getData());
            listView.setAdapter(myAddressAdapter);
        }
    }
}
