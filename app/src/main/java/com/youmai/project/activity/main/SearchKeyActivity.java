package com.youmai.project.activity.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.utils.IatSettings;
import com.youmai.project.utils.JsonParser;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;

public class SearchKeyActivity extends BaseActivity implements View.OnClickListener{


    private EditText etKeys;
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog iatDialog;
    private SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_search_key);
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
        etKeys=(EditText)findViewById(R.id.et_am_keys);
        findViewById(R.id.img_am_apeck).setOnClickListener(this);

        // 初始化识别对象
        mIat = SpeechRecognizer.createRecognizer(this, IatSettings.mInitListener);
        // 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
        iatDialog = new RecognizerDialog(this, IatSettings.mInitListener);
        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME, Context.MODE_PRIVATE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_am_apeck:
                IatSettings.setParam(mSharedPreferences, mIat);
                boolean isShowDialog = mSharedPreferences.getBoolean("iat_show", true);
                if (isShowDialog) {
                    // 显示听写对话框
                    iatDialog.setListener(recognizerDialogListener);
                    iatDialog.show();
                }
                break;
            default:
                break;
        }

    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            String text = JsonParser.parseIatResult(results.getResultString());
            etKeys.append(text);
        }
        /**
         * 识别回调错误.
         */
        @Override
        public void onError(SpeechError error) {
        }
    };


    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mIat.cancel();
        mIat.destroy();
    }

}
