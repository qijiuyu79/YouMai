package com.youmai.project.activity.order;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.adapter.MyPagerAdapter;
import com.youmai.project.adapter.chat.ChatIconAdapter;
import com.youmai.project.adapter.chat.ChatMessageAdapter;
import com.youmai.project.bean.ChatMessage;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.chat.ChatIcon;
import com.youmai.project.view.ClickImageView;
import com.youmai.project.view.ClickTextView;
import com.youmai.project.view.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * 卖家详情-聊天
 * Created by Administrator on 2017/11/2 0002.
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout linIcon,linViewGroup;
    private ClickImageView imgIcon;
    private ViewPager viewPager;
    /**
     * 装表情包的view
     */
    private List<View> vList=null;
    /**
     * 装点点的ImageView数组
     */
    private ImageView[] tips;
    /**  表情包adapter  **/
    private ChatIconAdapter chatIconAdapter;
    public EditText etSendMsg;
    /** 聊天列表控件 **/
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    /**  聊天记录**/
    private List<ChatMessage> listChat=new ArrayList<>();
    private ChatMessageAdapter chatMessageAdapter;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_E51C23);
        setContentView(R.layout.activity_chat_details);
        initView();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        linIcon=(LinearLayout)findViewById(R.id.lin_icon);
        imgIcon=(ClickImageView)findViewById(R.id.img_asd_icon);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        linViewGroup=(LinearLayout)findViewById(R.id.viewGroup);
        etSendMsg=(EditText)findViewById(R.id.et_asd_content);
        ClickTextView tvSend=(ClickTextView)findViewById(R.id.tv_asd_send);
        listView=(ListView)findViewById(R.id.list);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.chat_swipe_layout);
        imgIcon.setOnClickListener(this);
        tvSend.setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //显示表情包
            case R.id.img_asd_icon:
                if(linIcon.getVisibility()==View.GONE){
                    linIcon.setVisibility(View.VISIBLE);
                    showChatIcon();
                }else{
                    linIcon.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_asd_send:
                final String data=etSendMsg.getText().toString();
                if(TextUtils.isEmpty(data)){
                    showMsg("请输入您要发送的信息！");
                }else{
                    ChatMessage chatMessage=new ChatMessage(1,data);
                    listChat.add(listChat.size(),chatMessage);
                    chatMessageAdapter=new ChatMessageAdapter(mContext,listChat);
                    listView.setAdapter(chatMessageAdapter);
                    listView.setSelection(listChat.size()-1);
                    etSendMsg.setText(null);
                }
                break;
            case R.id.img_back:
                 finish();
                 break;
            default:
                break;
        }
    }


    private void showChatIcon(){
        vList=new ArrayList<>();
        MyGridView myGridView=new MyGridView(ChatActivity.this);
        myGridView.setNumColumns(7);
        myGridView.setAdapter(new ChatIconAdapter(ChatActivity.this, ChatIcon.iconList.subList(0,21)));
        vList.add(myGridView);

        MyGridView myGridView1=new MyGridView(ChatActivity.this);
        myGridView1.setNumColumns(7);
        myGridView1.setAdapter(new ChatIconAdapter(ChatActivity.this, ChatIcon.iconList.subList(21,42)));
        vList.add(myGridView1);

        MyGridView myGridView2=new MyGridView(ChatActivity.this);
        myGridView2.setNumColumns(7);
        myGridView2.setAdapter(new ChatIconAdapter(ChatActivity.this, ChatIcon.iconList.subList(42,ChatIcon.iconList.size())));
        vList.add(myGridView2);
//
        viewPager.setAdapter(new MyPagerAdapter(vList));

        tips=new ImageView[3];
        for(int i=0; i<tips.length; i++){
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(10,10));
            tips[i] = imageView;
            if(i == 0){
                tips[i].setBackgroundResource(R.mipmap.dian_unfocused);
            }else{
                tips[i].setBackgroundResource(R.mipmap.dian_focused);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            linViewGroup.addView(imageView, layoutParams);
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setImageBackground(i % tips.length);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 设置选中的tip的背景
     * @param selectItems
     */
    private void setImageBackground(int selectItems){
        for(int i=0; i<tips.length; i++){
            if(i == selectItems){
                tips[i].setBackgroundResource(R.mipmap.dian_unfocused);
            }else{
                tips[i].setBackgroundResource(R.mipmap.dian_focused);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取位置
//        if (requestCode == Constant.REQUEST_CODE_MAP) {
//            if(data==null){
//                return;
//            }
//            double latitude = data.getDoubleExtra("latitude", 0);
//            double longitude = data.getDoubleExtra("longitude", 0);
//            String locationAddress = data.getStringExtra("address");
//            if(!TextUtils.isEmpty(locationAddress)){
//                ChatMessage chatMessage=new ChatMessage(3,latitude,longitude,locationAddress);
//                listChat.add(listChat.size(),chatMessage);
//                chatMessageAdapter=new ChatMessageAdapter(mContext,listChat);
//                listView.setAdapter(chatMessageAdapter);
//                listView.setSelection(listChat.size()-1);
//            }else{
//                showMsg("无法获取您当前的位置！");
//            }
//        }
    }
}
