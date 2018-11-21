package com.youmai.project.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.main.BuyGoodsActivity;
import com.youmai.project.activity.map.RoutePlanActivity;
import com.youmai.project.activity.share.ShareActivity;
import com.youmai.project.activity.user.LoginActivity;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.bean.HttpBaseBean;
import com.youmai.project.bean.Report;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.Util;
import com.youmai.project.view.CircleImageView;
import com.youmai.project.view.ClickImageView;
import com.youmai.project.view.ClickTextView;
import java.util.ArrayList;
import java.util.List;

public class RecommendedAdapter extends BaseAdapter{

	private Context context;
	private List<GoodsBean> list;
	private GoodsBean goodsBean;
	//商品集合
	private List<ImageView> imgList=new ArrayList<>();
    //举报类型的集合
    private List<Report.ReportData> reportList=new ArrayList<>();
    //商品id
    private String goodsId;
    //举报id
    private int reportId;
    private ReportItemAdapter reportItemAdapter;
	public RecommendedAdapter(Context context,List<GoodsBean> list) {
		super();
		this.context = context;
		this.list=list;
	}

	@Override
	public int getCount() {
		return list==null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	ViewHolder holder = null;
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if(view==null){
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.recommended_item, null);
			holder.tvContext=(TextView)view.findViewById(R.id.tv_ri_content);
            holder.tvOldMoney=(TextView)view.findViewById(R.id.tv_ri_oldMoney);
			holder.tvLocation=(TextView)view.findViewById(R.id.tv_ri_location);
			holder.tvNewMoney=(TextView)view.findViewById(R.id.tv_ri_newMoney);
			holder.imgIcon=(ImageView)view.findViewById(R.id.img_ri_icon);
			holder.tvBuy=(ClickTextView)view.findViewById(R.id.tv_ri_buy);
			holder.circleImageView=(CircleImageView)view.findViewById(R.id.img_ri_pic);
			holder.tvNickName=(TextView)view.findViewById(R.id.tv_ri_name);
			holder.imgX1=(ImageView)view.findViewById(R.id.img_ri_x1);
			holder.imgX2=(ImageView)view.findViewById(R.id.img_ri_x2);
			holder.imgX3=(ImageView)view.findViewById(R.id.img_ri_x3);
			holder.imgX4=(ImageView)view.findViewById(R.id.img_ri_x4);
			holder.imgX5=(ImageView)view.findViewById(R.id.img_ri_x5);
			holder.imgShare=(ImageView)view.findViewById(R.id.img_ri_share);
			holder.imgReport=(ClickImageView)view.findViewById(R.id.img_report);
			holder.tvDistance=(TextView)view.findViewById(R.id.tv_ri_distance);
//			holder.shimmerTextView=(ShimmerTextView)view.findViewById(R.id.st_ri);
//			holder.shimmer=new Shimmer();
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		goodsBean=list.get(position);
		if(goodsBean!=null){
			holder.tvContext.setTag(goodsBean);
			holder.tvContext.setText(goodsBean.getDescription());
			holder.tvLocation.setTag(goodsBean);
			holder.tvLocation.setText(goodsBean.getAddress());
			holder.tvDistance.setText("距离："+goodsBean.getDistance());
			holder.tvNewMoney.setText("现价：¥"+ Util.setDouble(goodsBean.getPresentPrice()/100));
			holder.tvOldMoney.setText("原价：¥"+Util.setDouble(goodsBean.getOriginalPrice()/100));
			holder.tvOldMoney.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

//			holder.shimmer.start(holder.shimmerTextView);
			if(goodsBean.getImgList().size()>0){
				String imgUrl=goodsBean.getImgList().get(0);
				holder.imgIcon.setTag(R.id.imageid,imgUrl);
				if(holder.imgIcon.getTag(R.id.imageid)!=null && imgUrl==holder.imgIcon.getTag(R.id.imageid)){
					Glide.with(context).load(imgUrl).override(346,300).centerCrop().into(holder.imgIcon);
				}
			}else{
				holder.imgIcon.setImageDrawable(null);
			}
			Glide.with(context).load(goodsBean.getHead()).error(R.mipmap.default_head).into(holder.circleImageView);
			holder.tvNickName.setText(goodsBean.getNickname());
			//设置星级
			setXing(goodsBean.getCreditLevel());

			//购买
			holder.tvBuy.setTag(goodsBean);
			holder.tvBuy.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(!Util.isLogin()){
						Intent intent=new Intent(context, LoginActivity.class);
						context.startActivity(intent);
						return;
					}
					if(v.getTag()!=null){
						GoodsBean goodsBean= (GoodsBean) v.getTag();
						if(goodsBean!=null){
							Intent intent=new Intent(context, BuyGoodsActivity.class);
							intent.putExtra("goodsBean",goodsBean);
							context.startActivity(intent);
						}
					}
				}
			});

			//分享
			holder.imgShare.setTag(goodsBean);
			holder.imgShare.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(null==v.getTag()){
						return;
					}
					GoodsBean goodsBean= (GoodsBean) v.getTag();
					if(goodsBean!=null){
						Intent intent=new Intent(context, ShareActivity.class);
						intent.putExtra("goodsBean",goodsBean);
						context.startActivity(intent);
					}
				}
			});
			//路径规划
			holder.tvLocation.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(null==v.getTag()){
						return;
					}
					GoodsBean goodsBean= (GoodsBean) v.getTag();
					if(goodsBean!=null){
						Intent intent=new Intent(context, RoutePlanActivity.class);
						intent.putExtra("latitude",goodsBean.getLatitude());
						intent.putExtra("longtitude",goodsBean.getLongitude());
						context.startActivity(intent);
					}
				}
			});

			//举报
			holder.imgReport.setTag(goodsBean.getId());
			holder.imgReport.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(!Util.isLogin()){
						Intent intent=new Intent(context, LoginActivity.class);
						context.startActivity(intent);
						return;
					}
					if(null==v.getTag()){
						return;
					}
					goodsId=v.getTag().toString();
					if(reportList.size()>0){
					    showReport();
                    }else{
					    getReport();
                    }
				}
			});
		}
		return view;
	}


	/**
	 * 设置星级
	 */
	private void setXing(int index){
		imgList.clear();
		imgList.add(holder.imgX1);
		imgList.add(holder.imgX2);
		imgList.add(holder.imgX3);
		imgList.add(holder.imgX4);
		imgList.add(holder.imgX5);
		for (int i=0;i<imgList.size();i++){
			if(i<index){
				imgList.get(i).setImageDrawable(context.getResources().getDrawable(R.mipmap.yes_hselect_x));
			}else{
				imgList.get(i).setImageDrawable(context.getResources().getDrawable(R.mipmap.no_hselect_x));
			}
		}
	}
	
	private class ViewHolder{
		private TextView tvContext,tvLocation,tvNewMoney,tvOldMoney,tvNickName,tvDistance;
		private ClickTextView tvBuy;
		private ImageView imgIcon,imgX1,imgX2,imgX3,imgX4,imgX5,imgShare;
		private ClickImageView imgReport;
		private CircleImageView circleImageView;
//		private ShimmerTextView shimmerTextView;
//		private Shimmer shimmer;
	 }


	 private Handler mHandler=new Handler(){
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             switch (msg.what){
                 //获取举报类型
                 case HandlerConstant.GET_REPORT_LIST_SUCCESS:
                     final Report report= (Report) msg.obj;
                     if(null==report){
                         return;
                     }
                     if(report.isSussess()){
                         reportList=report.getData();
                         //展示举报界面
                         if(reportList.size()>0){
                         	 reportId=reportList.get(0).getId();
                             showReport();
                         }
                     }else{
                         Toast.makeText(context,report.getMsg(),Toast.LENGTH_LONG).show();
                     }
                     break;
                 //举报商品
				 case HandlerConstant.REPORT_GOODS_SUCCESS:
				 	  final HttpBaseBean httpBaseBean= (HttpBaseBean) msg.obj;
				 	  if(null==httpBaseBean){
				 	  	return;
					  }
					  if(httpBaseBean.isSussess()){
						  Toast.makeText(context,"举报成功！",Toast.LENGTH_LONG).show();
					  }else{
						  Toast.makeText(context,httpBaseBean.getMsg(),Toast.LENGTH_LONG).show();
					  }
				 	  break;
                 case HandlerConstant.REQUST_ERROR:
                     Toast.makeText(context,context.getString(R.string.http_error),Toast.LENGTH_LONG).show();
                     break ;
                 default:
                     break;
             }
         }
     };


    /**
     * 展示举报的界面
     */
    public  void showReport(){
        View view=LayoutInflater.from(context).inflate(R.layout.report_pop,null);
		final Dialog baseDialog=dialogPop(view,true);
		ListView listView=(ListView)view.findViewById(R.id.listView);
		reportItemAdapter=new ReportItemAdapter(context,reportList);
		listView.setAdapter(reportItemAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				reportItemAdapter.setIndex(position);
				reportItemAdapter.notifyDataSetChanged();
				reportId=reportList.get(position).getId();
			}
		});
		//提交
		view.findViewById(R.id.tv_submit).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				baseDialog.dismiss();
				HttpMethod.reportGoods(1,goodsId,reportId,mHandler);
			}
		});
		//关闭
		view.findViewById(R.id.img_delete).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				baseDialog.dismiss();
			}
		});
    }


    public Dialog dialogPop(View view, boolean b) {
        Dialog baseDialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        baseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        baseDialog.setTitle(null);
        baseDialog.setCancelable(b);
        baseDialog.setContentView(view);
        Window window = baseDialog.getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
        baseDialog.show();
        return baseDialog;
    }

    /**
     * 获取举报类型
     */
    private void getReport(){
        HttpMethod.getReportList(mHandler);
    }
}
