package com.youmai.project.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import com.youmai.project.utils.error.CockroachUtil;

/**
 * Created by Administrator on 2017/3/23 0023.
 */

public class BaseFragment extends Fragment {

    protected Activity mActivity;
    protected ProgressDialog progressDialog = null;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    protected void setClass(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        startActivity(intent);
    }

    /**
     * 自定义toast
     *
     * @param message
     */
    public void showMsg(String message) {
        Toast toast=Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * 取消进度条
     */
    public void clearTask() {
        if (progressDialog != null&&progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public void showProgress(String msg) {
        //如果已经存在并且在显示中就不处理
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * 可以返回键取消的弹框
     * @param msg
     */
    public void isCancleProgress(String msg) {
        //如果已经存在并且在显示中就不处理
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(msg);
            return;
        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        CockroachUtil.install();
    }

    public void onDetach(){
        super.onDetach();
        mActivity = null;
    }

    public void onDestroy() {
        super.onDestroy();
        CockroachUtil.clear();
    }
}
