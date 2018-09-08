package com.youmai.project.activity.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;

/**
 * 加载html5页面
 * Created by Administrator on 2017/2/27 0027.
 */

public class WebViewActivity extends BaseActivity implements View.OnClickListener {
    private WebView webView;
    private TextView tvTitle;
    @SuppressLint("JavascriptInterface")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        tvTitle = (TextView) findViewById(R.id.tv_head);
        webView = (WebView) findViewById(R.id.web_view);

        findViewById(R.id.lin_back).setOnClickListener(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);//关键点
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
        webSettings.setSupportZoom(true); // 支持缩放
        webView.setHorizontalScrollBarEnabled(false);//水平不显示
        webView.setWebViewClient(new webViewClient());
        int type = getIntent().getIntExtra("type", 0);

        switch (type) {
            //使用说明
            case 1:
                tvTitle.setText("服务协议");
                webView.loadUrl("http://www.th2w.com/deal.html");
                break;
            default:
                break;
        }

        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                return false;
            }

            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
//                    clearTask();
                } else {
//                   showProgress("加载中");
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right:
                break;
            //返回
            case R.id.lin_back:
                WebViewActivity.this.finish();
                break;
            default:
                break;
        }
    }

    private static class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //处理WebView跳转返回
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!=webView){
            webView.destroy();
        }
    }
}
