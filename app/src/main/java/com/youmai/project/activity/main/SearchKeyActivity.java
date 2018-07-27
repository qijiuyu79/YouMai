package com.youmai.project.activity.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.adapter.RecommendedAdapter;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.IatSettings;
import com.youmai.project.utils.JsonParser;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.view.RefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchKeyActivity extends BaseActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener{


    private EditText etKeys;
    private RefreshLayout swipeLayout;
    private ListView listView;
    private boolean isTotal=false;
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog iatDialog;
    private SharedPreferences mSharedPreferences;
    private RecommendedAdapter recommendedAdapter;
    private List<GoodsBean> listBeanAll=new ArrayList<>();
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

        swipeLayout=(RefreshLayout)findViewById(R.id.swipe_container);
        listView=(ListView)findViewById(R.id.list);
        listView.setDividerHeight(0);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,
                R.color.color_bule,
                R.color.color_bule2,
                R.color.color_bule3);
        swipeLayout.setOnRefreshListener(SearchKeyActivity.this);
        swipeLayout.setOnLoadListener(SearchKeyActivity.this);
        swipeLayout.post(new Thread(new Runnable() {
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        }));

        // 初始化识别对象
        mIat = SpeechRecognizer.createRecognizer(this, IatSettings.mInitListener);
        // 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
        iatDialog = new RecognizerDialog(this, IatSettings.mInitListener);
        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME, Context.MODE_PRIVATE);

        etKeys.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    //查询数据
                    getGoods();
                    return true;
                }
                return false;
            }
        });
    }


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HandlerConstant.SEARCH_BOODS_SUCCESS:
                    final String message= (String) msg.obj;
                    refresh(message);
                    swipeLayout.setRefreshing(false);
                    break;
                case HandlerConstant.REQUST_ERROR:
                    showMsg(getString(R.string.http_error));
                    break ;
                default:
                    break;
            }
        }
    };


    /**
     * 解析并刷新数据
     */
    private void refresh(String message){
        if(TextUtils.isEmpty(message)){
            return;
        }
        try {
            final JSONObject jsonObject=new JSONObject(message);
            final JSONArray jsonArray=new JSONArray(jsonObject.getString("data"));
            List<GoodsBean> list=new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                GoodsBean goodsBean=new GoodsBean();
                goodsBean.setAddress(jsonObject1.getString("address"));
                goodsBean.setDescription(jsonObject1.getString("description"));
                goodsBean.setId(jsonObject1.getString("id"));
                goodsBean.setOriginalPrice(jsonObject1.getDouble("originalPrice"));
                goodsBean.setPresentPrice(jsonObject1.getDouble("presentPrice"));
                List<String> imgList=new ArrayList<>();

                //解析图片
                final JSONArray jsonArray1=new JSONArray(jsonObject1.getString("images"));
                for (int j = 0; j < jsonArray1.length(); j++) {
                    imgList.add(jsonArray1.getString(j));
                }
                goodsBean.setImgList(imgList);

                //解析经纬度
                final JSONArray jsonArray2=new JSONArray(jsonObject1.getString("location"));
                for (int k = 0; k < jsonArray2.length(); k++) {
                    if(k==0){
                        goodsBean.setLongitude(jsonArray2.getDouble(k));
                    }else{
                        goodsBean.setLatitude(jsonArray2.getDouble(k));
                    }
                }
                list.add(goodsBean);
            }
            listBeanAll.addAll(list);
            if(null==recommendedAdapter){
                recommendedAdapter=new RecommendedAdapter(mContext,listBeanAll);
                listView.setAdapter(recommendedAdapter);
            }else{
                recommendedAdapter.notifyDataSetChanged();
            }
            if(list.size()<20){
                isTotal=true;
                swipeLayout.setFooter(isTotal);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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


    /**
     * 查询数据
     */
    private void getGoods(){
        final String key=etKeys.getText().toString().trim().replace(",","").replace(".","").replace("!","");
        HttpMethod.getGoodsByKey(key,mHandler);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoad() {

    }

    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mIat.cancel();
        mIat.destroy();
    }
}
