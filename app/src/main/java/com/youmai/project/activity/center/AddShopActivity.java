package com.youmai.project.activity.center;

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
import android.widget.TextView;

import com.youmai.project.application.MyApplication;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.photo.BigPhotoActivity;
import com.youmai.project.adapter.photo.GridImageAdapter;
import com.youmai.project.bean.MyGoods;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.BitMapUtils;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.photo.Bimp;
import com.youmai.project.utils.photo.ImageItem;
import com.youmai.project.utils.photo.PicturesUtil;
import com.youmai.project.view.MyGridView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加商品
 * Created by Administrator on 2018/1/18 0018.
 */

public class AddShopActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvType1,tvType2,tvType3,tvAddress;
    private EditText etContent,etOldMoney,etNewMoney;
    private MyGridView gridView;
    private GridImageAdapter adapter = null;
    //商品分类
    private String type="USED";
    //压缩后的图片文件
    private List<File> listFile=new ArrayList<>();
    private List<TextView> tvList=new ArrayList<>();
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addshop);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.gray_bg);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("添加宝贝");
        tvType1=(TextView)findViewById(R.id.tv_type1);
        tvType2=(TextView)findViewById(R.id.tv_type2);
        tvType3=(TextView)findViewById(R.id.tv_type3);
        tvAddress=(TextView)findViewById(R.id.tv_aa_location);
        etContent=(EditText)findViewById(R.id.et_aa_content);
        etOldMoney=(EditText)findViewById(R.id.et_aa_oleMoney);
        etNewMoney=(EditText)findViewById(R.id.et_aa_newMoney);
        gridView=(MyGridView)findViewById(R.id.mg_addshop);
        findViewById(R.id.lin_back).setOnClickListener(this);
        findViewById(R.id.tv_aa_add).setOnClickListener(this);
        tvType1.setOnClickListener(this);
        tvType2.setOnClickListener(this);
        tvType3.setOnClickListener(this);

        //显示当前位置
        tvAddress.setText(MyApplication.spUtil.getString(SPUtil.LOCATION_ADDRESS));
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
                        PicturesUtil.selectPhoto(AddShopActivity.this,1);
                    }
                } else {
                    Intent intent = new Intent(mContext, BigPhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivityForResult(intent, 0x002);
                }
            }
        });

        tvList.add(tvType1);
        tvList.add(tvType2);
        tvList.add(tvType3);
    }


    /**
     * 改变分类颜色
     * @param position
     */
    private void updateColor(int position){
        for (int i=0;i<tvList.size();i++){
             if(i!=position){
                 TextView textView=tvList.get(i);
                 if(textView!=null){
                     textView.setTextColor(getResources().getColor(R.color.color_666666));
                     textView.setBackground(getResources().getDrawable(R.drawable.borders));
                 }
             }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_type1:
                 type="USED";
                 tvType1.setTextColor(getResources().getColor(R.color.color_FF4081));
                 tvType1.setBackground(getResources().getDrawable(R.drawable.bg_recommended_buy));
                 updateColor(0);
                 break;
            case R.id.tv_type2:
                 type="NEW";
                 tvType2.setTextColor(getResources().getColor(R.color.color_FF4081));
                 tvType2.setBackground(getResources().getDrawable(R.drawable.bg_recommended_buy));
                 updateColor(1);
                break;
            case R.id.tv_type3:
                 type="PET";
                 tvType3.setTextColor(getResources().getColor(R.color.color_FF4081));
                 tvType3.setBackground(getResources().getDrawable(R.drawable.bg_recommended_buy));
                 updateColor(2);
                break;
            //提交
            case R.id.tv_aa_add:
                 final String content=etContent.getText().toString().trim();
                 final String oldMoney=etOldMoney.getText().toString().trim();
                 final String newMoney=etNewMoney.getText().toString().trim();
                 if(TextUtils.isEmpty(content)){
                     showMsg("请输入商品描述！");
                 }else if(TextUtils.isEmpty(oldMoney)){
                     showMsg("请输入商品原价！");
                 }else if(TextUtils.isEmpty(newMoney)){
                     showMsg("请输入商品现价！");
                 }else if(Bimp.selectBitmap.size()==0){
                     showMsg("请选择商品图片！");
                 }else{
                     showProgress("图片压缩中...",false);
                     LogUtils.e(Bimp.selectBitmap.size()+"++++++++++++++");
                     mHandler.postDelayed(new Runnable() {
                         public void run() {
                             final List<String> imgAddress=new ArrayList<>();
                             for (int i=0;i<Bimp.selectBitmap.size();i++){
                                 final File file=new File(Bimp.selectBitmap.get(i).getImagePath());
                                 if(null!=file){
                                     final String newPath=BitMapUtils.compressBitMap(file);
                                     final File file1=new File(newPath);
                                     if(null!=file1){
                                         imgAddress.add(newPath);
                                         listFile.add(file1);
                                     }
                                 }
                             }
                             showProgress("数据上传中...",false);
                             HttpMethod.addGoods(content,oldMoney,newMoney,type,tvAddress.getText().toString().trim(),listFile,mHandler);
                         }
                     },100);
                 }
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


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            switch (msg.what){
                case HandlerConstant.ADD_GOODS_SUCCESS:
                     String message= (String) msg.obj;
                    LogUtils.e("___"+message);
                     if(TextUtils.isEmpty(message)){
                         return;
                     }
                     try{
                         final JSONObject jsonObject=new JSONObject(message);
                         if(jsonObject.getInt("code")==200){
                             final JSONObject jsonObject1=new JSONObject(jsonObject.getString("data"));
                             MyGoods myGoods=new MyGoods();
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
                             JSONObject jsonObject2=new JSONObject(jsonObject1.getString("location"));
                             if(null==jsonObject2){
                                 return;
                             }
                             JSONArray jsonArray2=new JSONArray(jsonObject2.getString("coordinates"));
                             for (int k = 0; k < jsonArray2.length(); k++) {
                                 if(k==0){
                                     myGoods.setLongitude(jsonArray2.getDouble(k));
                                 }else{
                                     myGoods.setLatitude(jsonArray2.getDouble(k));
                                 }
                             }

                             Intent intent=new Intent(mContext,CenterActivity.class);
                             Bundle bundle=new Bundle();
                             bundle.putSerializable("myGoods",myGoods);
                             intent.putExtras(bundle);
                             setResult(RESULT_OK,intent);

                             //删除压缩后的图片
                             deleteImg();
                             finish();

                         }else{
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
     * 删除压缩后的图片
     */
    private void deleteImg(){
        for (int i=0;i<listFile.size();i++){
             if(listFile.get(i).isFile()){
                 listFile.get(i).delete();
             }
        }
    }
}
