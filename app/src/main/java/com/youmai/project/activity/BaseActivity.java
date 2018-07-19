package com.youmai.project.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.UserInfo;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.utils.error.CockroachUtil;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/10/26 0026.
 */

public class BaseActivity extends FragmentActivity {

    public static final String UPDATE_USER_INFO = "update_user_info";
    private ProgressDialog progressDialog = null;
    protected Context mContext = this;
    public Dialog baseDialog;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;        // a|=b的意思就是把a和b按位或然后赋值给a   按位或的意思就是先把a和b都换成2进制，然后用或操作，相当于a=a|b
        } else {
            winParams.flags &= ~bits;        //&是位运算里面，与运算  a&=b相当于 a = a&b  ~非运算符
        }
        win.setAttributes(winParams);
    }

    /**
     * 跳转Activity
     * @param cls
     */
    protected void setClass(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
    }


    public void showProgress(String msg,boolean isCancelable) {
        //如果已经存在并且在显示中就不处理
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(msg);
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(isCancelable);
        progressDialog.show();
    }


    /**
     * 取消进度条
     */
    public void clearTask() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    /**
     * dialog弹框
     *
     * @param view
     */
    public Dialog dialogPop(View view, boolean b) {
        baseDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        baseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        baseDialog.setTitle(null);
        baseDialog.setCancelable(b);
        baseDialog.setContentView(view);
        Window window = baseDialog.getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
//        window.setWindowAnimations(R.style.mystyle);  //添加动画
        //设置dialog全屏
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        android.view.WindowManager.LayoutParams params = baseDialog.getWindow().getAttributes();  //获取对话框当前的参数值、
        params.width = (int) (d.getWidth());    //宽度设置全屏宽度
        baseDialog.getWindow().setAttributes(params);     //设置生效
        baseDialog.show();
        return baseDialog;
    }

    public void closeDialog() {
        if (baseDialog != null) {
            baseDialog.dismiss();
        }
    }

    /**
     * 确保系统字体大小不会影响app中字体大小
     *
     * @return
     */
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }


    /**
     * 隐藏键盘
     */
    public void lockKey(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    /**
     * 弹出软键盘
     * @param et
     */
    public void openKey(EditText et){
        InputMethodManager inputManager = (InputMethodManager)et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et, 0);
    }


    public void showMsg(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 获取用户信息
     */
    public void getUserInfo(){
        HttpMethod.getUserInfo(mHandler);
    }

    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //获取用户信息返回
                case HandlerConstant.GET_USERINFO_SUCCESS:
                     UserInfo userInfo= (UserInfo) msg.obj;
                     if(null==userInfo){
                        return;
                     }
                     MyApplication.userInfoBean=userInfo.getData();
                     MyApplication.spUtil.addString(SPUtil.USER_INFO, MyApplication.gson.toJson(userInfo.getData()));
                     mContext.sendBroadcast(new Intent(UPDATE_USER_INFO));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        CockroachUtil.install();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CockroachUtil.clear();
    }
}
