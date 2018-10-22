/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youmai.project.activity.main;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.webview.WebViewActivity;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.scan.cameras.CameraManager;
import com.youmai.project.utils.scan.decoding.InactivityTimer;
import com.youmai.project.utils.scan.decoding.ScanActivityHandler;
import com.youmai.project.utils.scan.view.ViewfinderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 扫描二维码
 */
public class ScanActivity extends BaseActivity implements SurfaceHolder.Callback, OnClickListener {

    private ScanActivityHandler handler;// 消息中心
    private ViewfinderView viewfinderView;// 绘制扫描区域
    private boolean hasSurface;// 控制调用相机属性
    private Vector<BarcodeFormat> decodeFormats;// 存储二维格式的数组
    private String characterSet;// 字符集
    private InactivityTimer inactivityTimer;// 相机扫描刷新timer
    private MediaPlayer mediaPlayer;// 播放器
    private boolean playBeep;// 声音布尔
    private static final float BEEP_VOLUME = 0.10f;// 声音大小
    private boolean vibrate;// 振动布尔
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing_scan);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FF4081);
        CameraManager.init(this);
        inactivityTimer = new InactivityTimer(this);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        findViewById(R.id.lin_back).setOnClickListener(this);
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        resumeScan();
        if (null == result) {
            return;
        }
        String resultString = result.getText();
        if (!TextUtils.isEmpty(resultString)) {
            resultString = resultString.replace(" ", "");
            if (resultString.indexOf("q.th2w") != -1) {
                int position1=resultString.indexOf("{");
                int position2=resultString.indexOf("}");
                String goodsId=resultString.substring(++position1,position2);
                showProgress("数据查询中");
                HttpMethod.getGoodsDetails(goodsId,mHandler);

            } else {
                showMsg(getString(R.string.please_scan_right_qr_code));
            }
        } else {
            showMsg(getString(R.string.scan_failed_try_again));
        }
    }


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            switch (msg.what){
                case HandlerConstant.GET_GOODS_DETAILS_SUCCESS:
                    String message= (String) msg.obj;
                    if(TextUtils.isEmpty(message)){
                        return;
                    }
                    try {
                        final JSONObject jsonObject=new JSONObject(message);
                        if(jsonObject.getInt("code")==200){
                            final JSONObject jsonObject1=new JSONObject(jsonObject.getString("data"));
                            GoodsBean myGoods=new GoodsBean();
                            myGoods.setAddress(jsonObject1.getString("address"));
                            myGoods.setDescription(jsonObject1.getString("description"));
                            myGoods.setId(jsonObject1.getString("id"));
                            myGoods.setOriginalPrice(jsonObject1.getDouble("originalPrice"));
                            myGoods.setPresentPrice(jsonObject1.getDouble("presentPrice"));
                            //解析图片
                            List<String> imgList=new ArrayList<>();
                            JSONArray jsonArray1=new JSONArray(jsonObject1.getString("images"));
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                imgList.add(jsonArray1.getString(j));
                            }
                            myGoods.setImgList(imgList);

                            //解析经纬度
                            final JSONArray jsonArray2=new JSONArray(jsonObject1.getString("location"));
                            for (int k = 0; k < jsonArray2.length(); k++) {
                                if(k==0){
                                    myGoods.setLongitude(jsonArray2.getDouble(k));
                                }else{
                                    myGoods.setLatitude(jsonArray2.getDouble(k));
                                }
                            }
                            //解析用户信息
                            if(!jsonObject1.isNull("seller")){
                                JSONObject jsonObject2=new JSONObject(jsonObject1.getString("seller"));
                                if(!jsonObject2.isNull("head")){
                                    myGoods.setHead(jsonObject2.getString("head"));
                                }
                                if(!jsonObject2.isNull("nickname")){
                                    myGoods.setNickname(jsonObject2.getString("nickname"));
                                }
                                if(!jsonObject2.isNull("storeId")){
                                    myGoods.setStoreId(jsonObject2.getString("storeId"));
                                }
                                if(!jsonObject2.isNull("creditLevel")){
                                    myGoods.setCreditLevel(jsonObject2.getInt("creditLevel"));
                                }
                            }
                            Intent intent=new Intent(mContext, GoodDetailsActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("goodsBean",myGoods);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                     break;
                case HandlerConstant.REQUST_ERROR:
                    showMsg(getString(R.string.http_error));
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 重复扫描
     */
    public void resumeScan() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (null != handler) {
                    handler.restartPreviewAndDecode();
                }
            }
        }, 3000);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_back:
                finish();
                break;
            default:
                break;
        }
    }


      /**
     * 初始化相机
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception ioe) {
            return;
        }
        if (handler == null) {
            handler = new ScanActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    /**
     * 声音设置
     */
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    /**
     * 结束后的声音
     */
    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        // 初始化相机画布
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        // 声音
        playBeep = true;
        // 初始化音频管理器
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        // 振动
        vibrate = true;
    }


    @Override
    protected void onPause() {
        // 停止相机 关闭闪光灯
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        clearTask();
        // 停止相机扫描刷新timer
        inactivityTimer.shutdown();
        removeHandler(mHandler);
        super.onDestroy();
    }
}
