package com.youmai.project.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.youmai.project.R;
import com.youmai.project.activity.main.MainActivity;
import com.youmai.project.adapter.RecommendedAdapter;
import com.youmai.project.bean.MainBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.view.RefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐的fragment
 * Created by Administrator on 2018/1/3 0003.
 */

public class RecommendedFragment extends BaseFragment  implements SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener {

    private RefreshLayout swipeLayout;
    private ListView listView;
    private RecommendedAdapter recommendedAdapter;
    private List<MainBean> listBeanAll=new ArrayList<>();
    private int page=1;
    private boolean isTotal=false;
    //fragment是否可见
    private boolean isVisibleToUser=false;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    View view=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommended, container, false);
        swipeLayout=(RefreshLayout)view.findViewById(R.id.swipe_container);
        listView=(ListView)view.findViewById(R.id.list);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,
                R.color.color_bule,
                R.color.color_bule2,
                R.color.color_bule3);
        swipeLayout.setOnRefreshListener(RecommendedFragment.this);
        swipeLayout.setOnLoadListener(RecommendedFragment.this);
        swipeLayout.post(new Thread(new Runnable() {
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        }));
        //查询数据
        loadData();
        recommendedAdapter=new RecommendedAdapter(getActivity(),listBeanAll);
        listView.setAdapter(recommendedAdapter);
        return view;
    }


    /**
     * 查询数据
     */
    private void loadData(){
        if(isVisibleToUser && view!=null && listBeanAll.size()==0){
            swipeLayout.postDelayed(new Runnable() {
                public void run() {
                    listView.addHeaderView(new View(getActivity()));
                    HttpMethod.getLocationGoods("USED",page,mHandler);
                }
            }, 0);
        }
    }

    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HandlerConstant.GET_LOCATION_GOODS_SUCCESS:
                     final String message= (String) msg.obj;
                     refresh(message);
                     swipeLayout.setRefreshing(false);
                     break;
                case HandlerConstant.REQUST_ERROR:
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
            List<MainBean> list=new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                 JSONObject jsonObject1=jsonArray.getJSONObject(i);
                 MainBean mainBean=new MainBean();
                 mainBean.setAddress(jsonObject1.getString("address"));
                 mainBean.setDescription(jsonObject1.getString("description"));
                 mainBean.setId(jsonObject1.getString("id"));
                 mainBean.setOriginalPrice(jsonObject1.getDouble("originalPrice"));
                 mainBean.setPresentPrice(jsonObject1.getDouble("presentPrice"));
                 List<String> imgList=new ArrayList<>();

                //解析图片
                final JSONArray jsonArray1=new JSONArray(jsonObject1.getString("images"));
                for (int j = 0; j < jsonArray1.length(); j++) {
                     imgList.add(jsonArray1.getString(j));
                }
                mainBean.setImgList(imgList);

                //解析经纬度
                final JSONArray jsonArray2=new JSONArray(jsonObject1.getString("location"));
                for (int k = 0; k < jsonArray2.length(); k++) {
                     if(k==0){
                         mainBean.setLongitude(jsonArray2.getDouble(k));
                     }else{
                         mainBean.setLatitude(jsonArray2.getDouble(k));
                     }
                }
                list.add(mainBean);
            }
            listBeanAll.addAll(list);
            if(null==recommendedAdapter){
                recommendedAdapter=new RecommendedAdapter(getActivity(),listBeanAll);
                listView.setAdapter(recommendedAdapter);
            }else{
                recommendedAdapter.notifyDataSetChanged();
            }
            if(list.size()<10){
                isTotal=true;
                swipeLayout.setFooter(isTotal);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoad() {


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser=isVisibleToUser;
        //查询数据
        loadData();
    }
}
