package com.youmai.project.activity.order;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.photo.BigPhotoActivity;
import com.youmai.project.adapter.photo.GridImageAdapter;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.bean.HttpBaseBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.BitMapUtils;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.photo.Bimp;
import com.youmai.project.utils.photo.ImageItem;
import com.youmai.project.utils.photo.PicturesUtil;
import com.youmai.project.view.MyGridView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 评价晒单
 */
public class EvaluateActivity extends BaseActivity implements View.OnClickListener{

    private ImageView imageView1,imageView2,imageView3,imageView4,imageView5;
    private EditText etContent;
    private List<ImageView> listImg=new ArrayList<>();
    private MyGridView gridView;
    private GridImageAdapter adapter = null;
    //评分
    private int score;
    //压缩后的图片文件
    private List<File> listFile=new ArrayList<>();
    //商品对象
    private GoodsBean goodsBean;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_evaluate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FF4081);
        initView();
        showImg();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        Bundle bundle=getIntent().getExtras();
        goodsBean= (GoodsBean) bundle.getSerializable("goodsBean");
        ImageView imageView=(ImageView)findViewById(R.id.img_ae_img);
        if(goodsBean!=null){
            if(goodsBean.getImgList().size()>0){
                Glide.with(mContext).load(goodsBean.getImgList().get(0)).centerCrop().error(R.mipmap.icon).into(imageView);
            }
        }
        imageView1=(ImageView)findViewById(R.id.img_ae_xing1);
        imageView2=(ImageView)findViewById(R.id.img_ae_xing2);
        imageView3=(ImageView)findViewById(R.id.img_ae_xing3);
        imageView4=(ImageView)findViewById(R.id.img_ae_xing4);
        imageView5=(ImageView)findViewById(R.id.img_ae_xing5);
        gridView=(MyGridView)findViewById(R.id.mg_addshop);
        etContent=(EditText)findViewById(R.id.et_ae_des);
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);
        imageView5.setOnClickListener(this);
        findViewById(R.id.tv_ae_submit).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
        listImg.add(imageView1);
        listImg.add(imageView2);
        listImg.add(imageView3);
        listImg.add(imageView4);
        listImg.add(imageView5);
    }


    /**
     * 显示照片
     */
    private void showImg(){
        //清空图片集合
        Bimp.selectBitmap.clear();
        adapter = new GridImageAdapter(getApplicationContext(), Bimp.selectBitmap);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == Bimp.selectBitmap.size()) {
                    if (Bimp.selectBitmap.size() >=5) {
                        showMsg("图片最多选择5个！");
                    } else {
                        PicturesUtil.selectPhoto(EvaluateActivity.this,1);
                    }
                } else {
                    Intent intent = new Intent(mContext, BigPhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivityForResult(intent, 0x002);
                }
            }
        });
    }

    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            switch (msg.what){
                case HandlerConstant.ADD_COMMENT_SUCCESS:
                     final String message=msg.obj.toString();
                     if(TextUtils.isEmpty(message)){
                         return;
                     }
                     try{
                         JSONObject jsonObject=new JSONObject(message);
                         if(jsonObject.getInt("code")==200){
                             showMsg("评价成功！");
                             finish();
                         }else{
                             showMsg("评价失败！");
                             finish();
                         }
                     }catch (Exception e){
                         e.printStackTrace();
                     }
                      break;
                case HandlerConstant.REQUST_ERROR:
                    showMsg(getString(R.string.http_error));
                    deleteImg();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_ae_xing1:
                 setXing(1);
                 break;
            case R.id.img_ae_xing2:
                setXing(2);
                break;
            case R.id.img_ae_xing3:
                setXing(3);
                break;
            case R.id.img_ae_xing4:
                setXing(4);
                break;
            case R.id.img_ae_xing5:
                setXing(5);
                break;
            //提交
            case R.id.tv_ae_submit:
                 final String content=etContent.getText().toString().trim();
                 if(TextUtils.isEmpty(content)){
                     showMsg("请描述您的评论！");
                     return;
                 }
                 if(Bimp.selectBitmap.size()>0){
                     showProgress("图片压缩中...");
                 }
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        for (int i=0;i<Bimp.selectBitmap.size();i++){
                            final File file=new File(Bimp.selectBitmap.get(i).getImagePath());
                            if(!file.isFile()){
                                return;
                            }
                            final String newPath= BitMapUtils.compressBitMap(file);
                            final File file1=new File(newPath);
                            if(file1.isFile()){
                                listFile.add(file1);
                            }
                        }
                        showProgress("添加宝贝中...");
                        HttpMethod.addComment(goodsBean.getOrderId(),score+"",content,listFile,mHandler);
                    }
                },100);
                 break;
            case R.id.lin_back:
                 finish();
                 break;
                default:
                    break;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //返回拍照图片
            case PicturesUtil.CODE_CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    File file = new File(PicturesUtil.pai);
                    if(file.isFile()){
                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setImagePath(file.getPath());
                        Bimp.selectBitmap.add(takePhoto);
                        Bimp.imgList.add(takePhoto);
                        adapter=new GridImageAdapter(getApplicationContext(), Bimp.selectBitmap);
                        gridView.setAdapter(adapter);
                    }else{
                        showMsg("拍照失败！");
                    }
                }
                break;
            //返回相册选择图片
            case PicturesUtil.CODE_GALLERY_REQUEST:
                adapter=new GridImageAdapter(getApplicationContext(), Bimp.selectBitmap);
                gridView.setAdapter(adapter);
                break;
            default:
                break;

        }
    }



    /**
     * 设置星级
     */
    private void setXing(int index){
        score=index;
        for (int i=0;i<listImg.size();i++){
            if(i<index){
                listImg.get(i).setImageDrawable(getResources().getDrawable(R.mipmap.yes_select_x));
            }else{
                listImg.get(i).setImageDrawable(getResources().getDrawable(R.mipmap.no_select_x));
            }
        }
    }


    /**
     * 删除压缩后的图片
     */
    private void deleteImg(){
        for (int i=0;i<listFile.size();i++){
            if(listFile.get(i).isFile()){
                listFile.get(i).delete();
            }
        }
        listFile.clear();
    }
}
