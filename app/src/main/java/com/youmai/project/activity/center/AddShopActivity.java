package com.youmai.project.activity.center;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.youmai.project.Application.MyApplication;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.main.MainActivity;
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
import com.youmai.project.view.CycleWheelView;
import com.youmai.project.view.MyGridView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加商品
 * Created by Administrator on 2018/1/18 0018.
 */

public class AddShopActivity extends BaseActivity implements View.OnClickListener{

    private EditText etContent,etOldMoney,etNewMoney;
    private TextView tvGoodsType;
    private MyGridView gridView;
    private GridImageAdapter adapter = null;
    //商品分类的索引
    private int position;
    //压缩后的图片文件
    private List<File> listFile=new ArrayList<>();
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addshop);
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
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("添加宝贝");
        etContent=(EditText)findViewById(R.id.et_aa_content);
        etOldMoney=(EditText)findViewById(R.id.et_aa_oleMoney);
        etNewMoney=(EditText)findViewById(R.id.et_aa_newMoney);
        tvGoodsType=(TextView)findViewById(R.id.tv_aa_goodsType);
        gridView=(MyGridView)findViewById(R.id.mg_addshop);
        tvGoodsType.setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
        findViewById(R.id.tv_aa_add).setOnClickListener(this);
        //清空图片集合
        if (Bimp.selectBitmap.size() != 0) {
            Bimp.selectBitmap.clear();
        }
        adapter = new GridImageAdapter(getApplicationContext(), Bimp.selectBitmap);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == Bimp.selectBitmap.size()) {
                    if (Bimp.selectBitmap.size() >5) {
                        showMsg("图片最多选择9个！");
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //选择商品分类
            case R.id.tv_aa_goodsType:
                 selectGoodsType();
                 break;
            //关闭dialog
            case R.id.picker_yes:
            case R.id.rel_wheel:
                 closeDialog();
                 break;
            //提交
            case R.id.tv_aa_add:
                 final String content=etContent.getText().toString().trim();
                 final String oldMoney=etOldMoney.getText().toString().trim();
                 final String newMoney=etNewMoney.getText().toString().trim();
                 final String goodsType=tvGoodsType.getText().toString().trim();
                 if(TextUtils.isEmpty(content)){
                     showMsg("请输入商品描述！");
                 }else if(TextUtils.isEmpty(oldMoney)){
                     showMsg("请输入商品原价！");
                 }else if(TextUtils.isEmpty(newMoney)){
                     showMsg("请输入商品现价！");
                 }else if(TextUtils.isEmpty(goodsType)){
                     showMsg("请选择商品分类！");
                 }else if(Bimp.selectBitmap.size()==0){
                     showMsg("请选择商品图片！");
                 }else{
                     showProgress("图片压缩中...",false);
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
                             final String address= MyApplication.spUtil.getString(SPUtil.LOCATION_ADDRESS);
                             HttpMethod.addGoods(content,oldMoney,newMoney,MainActivity.keyList.get(position),address,listFile,mHandler);
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


    /**
     * 选择商品分类
     */
    private void selectGoodsType(){
        final View view = LayoutInflater.from(mContext).inflate(R.layout.goods_type_wheel, null);
        CycleWheelView cycleWheelView=(CycleWheelView)view.findViewById(R.id.cycleWheelView);
        dialogPop(view, true);
        cycleWheelView.setLabels(MainActivity.valList);
        try {
            cycleWheelView.setWheelSize(5);
        }catch (Exception e){
            e.printStackTrace();
        }
        cycleWheelView.setCycleEnable(false);
        cycleWheelView.setSelection(0);
        cycleWheelView.setAlphaGradual(0.5f);
        cycleWheelView.setDivider(Color.parseColor("#abcdef"),1);
        cycleWheelView.setSolid(Color.WHITE,Color.WHITE);
        cycleWheelView.setLabelColor(Color.GRAY);
        cycleWheelView.setLabelSelectColor(Color.BLACK);
        cycleWheelView.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
            public void onItemSelected(int position, String label) {
                AddShopActivity.this.position=position;
                tvGoodsType.setText(MainActivity.valList.get(position));
            }
        });
        view.findViewById(R.id.picker_yes).setOnClickListener(this);
        view.findViewById(R.id.rel_wheel).setOnClickListener(this);
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
