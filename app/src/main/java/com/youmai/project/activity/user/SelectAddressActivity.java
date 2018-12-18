package com.youmai.project.activity.user;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.adapter.SelectAddrAdapter;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.map.GetLocation;

import java.util.List;

public class SelectAddressActivity extends BaseActivity implements OnGetPoiSearchResultListener{

    private ListView listView;
    private EditText etKeys;
    private PoiSearch mPoiSearch;
    private PoiNearbySearchOption poiNearbySearchOption;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_select_addr);
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
        listView=(ListView)findViewById(R.id.listView);
        etKeys=(EditText) findViewById(R.id.tv_keys);
        // POI初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        poiNearbySearchOption = new PoiNearbySearchOption();
        poiNearbySearchOption.keyword("公司,家");
        poiNearbySearchOption.location(GetLocation.getInstance().getNewLatLng());
        poiNearbySearchOption.radius(200);  // 检索半径，单位是米
        poiNearbySearchOption.pageCapacity(30);  // 默认每页10条
        mPoiSearch.searchNearby(poiNearbySearchOption);  // 发起附近检索请求

        /**
         * 根据关键字搜索
         */
        etKeys.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()==0){
                    return;
                }
                poiNearbySearchOption.keyword(s.toString());
                mPoiSearch.searchNearby(poiNearbySearchOption);  // 发起附近检索请求
            }
        });
        //返回
        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SelectAddressActivity.this.finish();
            }
        });
    }


    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            final List<PoiInfo> list=poiResult.getAllPoi();
            SelectAddrAdapter selectAddrAdapter=new SelectAddrAdapter(mContext,list);
            listView.setAdapter(selectAddrAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent();
                    intent.putExtra("poiInfo",list.get(position));
                    setResult(1,intent);
                    SelectAddressActivity.this.finish();
                }
            });
        }
    }
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
    }
}
