package com.youmai.project.activity.order;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.adapter.CommentAdapter;
import com.youmai.project.bean.Comment;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.JsonUtils;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.view.RefreshLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论的页面
 */
public class CommentListActivity extends BaseActivity   implements SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener {

    private TextView tvNickName;
    private RefreshLayout swipeLayout;
    private ListView listView;
    private CommentAdapter commentAdapter;
    private ImageView imgX1,imgX2,imgX3,imgX4,imgX5;
    private int page=1;
    private boolean isTotal=false;
    private List<Comment> listAll=new ArrayList<>();
    //商铺id
    private String storeId;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_comment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FF4081);
        initView();
    }


    /**
     * 初始化
     */
    private void initView(){
        tvNickName=(TextView)findViewById(R.id.tv_ac_name);
        imgX1=(ImageView)findViewById(R.id.img_au_x1);
        imgX2=(ImageView)findViewById(R.id.img_au_x2);
        imgX3=(ImageView)findViewById(R.id.img_au_x3);
        imgX4=(ImageView)findViewById(R.id.img_au_x4);
        imgX5=(ImageView)findViewById(R.id.img_au_x5);
        //获取商品基本信息
        final String nickName=getIntent().getStringExtra("nickName");
        storeId=getIntent().getStringExtra("storeId");
        final int creditLevel=getIntent().getIntExtra("creditLevel",0);
        tvNickName.setText(nickName);
        //设置星级
        setXing(creditLevel);

        swipeLayout=(RefreshLayout)findViewById(R.id.swipe_container);
        listView=(ListView)findViewById(R.id.list);
        listView.setDividerHeight(0);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,
                R.color.color_bule,
                R.color.color_bule2,
                R.color.color_bule3);
        swipeLayout.setOnRefreshListener(CommentListActivity.this);
        swipeLayout.setOnLoadListener(CommentListActivity.this);
        swipeLayout.post(new Thread(new Runnable() {
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        }));
        swipeLayout.postDelayed(new Runnable() {
            public void run() {
                listView.addHeaderView(new View(CommentListActivity.this));
                //查询数据
                getData(HandlerConstant.GET_COMMENT_LIST_SUCCESS);
            }
        }, 0);

        //返回
        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CommentListActivity.this.finish();
            }
        });
    }


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message;
            switch (msg.what){
                case HandlerConstant.GET_COMMENT_LIST_SUCCESS:
                    message= (String) msg.obj;
                    listAll.clear();
                    refresh(message);
                    swipeLayout.setRefreshing(false);
                    break;
                case HandlerConstant.GET_COMMENT_LIST_SUCCESS2:
                    message= (String) msg.obj;
                    refresh(message);
                    swipeLayout.setLoading(false);
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
    private void refresh(String msg){
        List<Comment> list= JsonUtils.getCommentList(msg);
        listAll.addAll(list);
        if(null==commentAdapter){
            commentAdapter=new CommentAdapter(mContext,listAll);
            listView.setAdapter(commentAdapter);
        }else{
            commentAdapter.notifyDataSetChanged();
        }
        if(list.size()<20){
            isTotal=true;
            swipeLayout.setFooter(isTotal);
        }
    }


    /**
     * 设置星级
     */
    private List<ImageView> imgList=new ArrayList<>();
    private void setXing(int index){
        imgList.add(imgX1);
        imgList.add(imgX2);
        imgList.add(imgX3);
        imgList.add(imgX4);
        imgList.add(imgX5);
        for (int i=0;i<imgList.size();i++){
            if(i<index){
                imgList.get(i).setImageDrawable(getResources().getDrawable(R.mipmap.yes_select_x));
            }else{
                imgList.get(i).setImageDrawable(getResources().getDrawable(R.mipmap.no_select_x));
            }
        }
    }

    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {
            public void run() {
                page=1;
                isTotal=false;
                swipeLayout.setFooter(isTotal);
                getData(HandlerConstant.GET_COMMENT_LIST_SUCCESS);
            }
        }, 200);
    }

    @Override
    public void onLoad() {
        if(isTotal){
            swipeLayout.setLoading(false);
            return;
        }
        swipeLayout.postDelayed(new Runnable() {
            public void run() {
                page++;
                getData(HandlerConstant.GET_COMMENT_LIST_SUCCESS2);
            }
        }, 200);

    }

    /**
     * 查询评论列表数据
     */
    private void getData(int index){
        HttpMethod.getCommentList(storeId,null,page,index,mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeHandler(mHandler);
    }
}
