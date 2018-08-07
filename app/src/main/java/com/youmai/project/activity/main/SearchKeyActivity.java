package com.youmai.project.activity.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.adapter.RecommendedAdapter;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.IatSettings;
import com.youmai.project.utils.JsonParser;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.Util;
import com.youmai.project.view.DialogView;
import com.youmai.project.view.RefreshLayout;
import com.youmai.project.view.TagLayoutView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 按关键字搜索
 */
public class SearchKeyActivity extends BaseActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener,TextView.OnEditorActionListener{


    private RelativeLayout relTags;
    private EditText etKeys;
    private RefreshLayout swipeLayout;
    private ListView listView;
    private TagLayoutView tagLayoutView;
    private ImageView imgClear;
    private boolean isTotal=false;
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog iatDialog;
    private SharedPreferences mSharedPreferences;
    private RecommendedAdapter recommendedAdapter;
    private List<GoodsBean> listBeanAll=new ArrayList<>();
    //输入的关键字
    private String strKey;
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
        initView();//初始化控件
        initSpeech();//初始化语音转文字
        showTags();//显示历史搜索记录
    }


    /**
     * 初始化控件
     */
    private void initView(){
        relTags=(RelativeLayout)findViewById(R.id.rel_ask_tag);
        etKeys=(EditText)findViewById(R.id.et_am_keys);
        tagLayoutView=(TagLayoutView)findViewById(R.id.tag_as);
        imgClear=(ImageView)findViewById(R.id.img_clear_et);
        swipeLayout=(RefreshLayout)findViewById(R.id.swipe_container);
        listView=(ListView)findViewById(R.id.list);
        listView.setDividerHeight(0);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,
                R.color.color_bule,
                R.color.color_bule2,
                R.color.color_bule3);
        swipeLayout.setOnRefreshListener(SearchKeyActivity.this);
        swipeLayout.setOnLoadListener(SearchKeyActivity.this);
        etKeys.setOnEditorActionListener(this);
        findViewById(R.id.img_am_apeck).setOnClickListener(this);
        findViewById(R.id.img_as_clear).setOnClickListener(this);
        imgClear.setOnClickListener(this);
        etKeys.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final int length=s.toString().length();
                if(length>0){
                    imgClear.setVisibility(View.VISIBLE);
                }else{
                    imgClear.setVisibility(View.GONE);
                }
            }
        });
    }


    /**
     * 初始化语音转文字
     */
    private void initSpeech(){
        // 初始化识别对象
        mIat = SpeechRecognizer.createRecognizer(this, IatSettings.mInitListener);
        // 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
        iatDialog = new RecognizerDialog(this, IatSettings.mInitListener);
        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 显示历史搜索记录
     */
    private void showTags(){
        List<String> lable = new ArrayList<>();
        final String keys=MyApplication.spUtil.getString(SPUtil.TAG_KEY);
        String[] str=keys.split("&");
        if(null==str){
            return;
        }
        for(int i=0;i<str.length;i++){
            if(!TextUtils.isEmpty(str[i])){
                lable.add(str[i]);
            }
        }
        //设置标签
        tagLayoutView.setLables(lable, true);
    }


    /**
     * 搜索键触发事件
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH){
            strKey= Util.format(etKeys.getText().toString().trim());
            if(TextUtils.isEmpty(strKey)){
                showMsg("请输入要搜索的关键字！");
                return false;
            }
            //保存搜索过的关键字
            String keys= MyApplication.spUtil.getString(SPUtil.TAG_KEY);
            keys+="&"+strKey;
            MyApplication.spUtil.addString(SPUtil.TAG_KEY,keys);

            //清空之前搜索过的列表
            listBeanAll.clear();
            swipeLayout.setVisibility(View.VISIBLE);
            swipeLayout.post(new Thread(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(true);
                }
            }));
            swipeLayout.postDelayed(new Runnable() {
                public void run() {
                    listView.addHeaderView(new View(SearchKeyActivity.this));
                    //查询数据
                    getGoods();
                }
            }, 0);
            return true;
        }
        return false;
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
            if(listBeanAll.size()>0){
                relTags.setVisibility(View.GONE);
            }else{
                showMsg("没有搜索到任何数据！");
            }
            if(list.size()<20){
                isTotal=true;
                swipeLayout.setFooter(isTotal);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private DialogView dialogView;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //开启语音转文字
            case R.id.img_am_apeck:
                IatSettings.setParam(mSharedPreferences, mIat);
                boolean isShowDialog = mSharedPreferences.getBoolean("iat_show", true);
                if (isShowDialog) {
                    // 显示听写对话框
                    iatDialog.setListener(recognizerDialogListener);
                    iatDialog.show();
                }
                break;
            //清空搜索历史
            case R.id.img_as_clear:
                 dialogView = new DialogView(dialogView, mContext, "确定清空搜索历史吗？",
                        "确定", "取消", new View.OnClickListener() {
                    public void onClick(View v) {
                        dialogView.dismiss();
                        MyApplication.spUtil.removeMessage(SPUtil.TAG_KEY);
                        tagLayoutView.removeAllViews();
                    }
                }, null);
                dialogView.show();
                 break;
            //清空搜索框
            case R.id.img_clear_et:
                 etKeys.setText(null);
                 swipeLayout.setVisibility(View.GONE);
                 relTags.setVisibility(View.VISIBLE);
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
            etKeys.append(Util.format(text));
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
        HttpMethod.getGoodsByKey(strKey,mHandler);
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
