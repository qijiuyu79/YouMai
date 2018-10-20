package com.youmai.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.main.BuyGoodsActivity;
import com.youmai.project.activity.map.RoutePlanActivity;
import com.youmai.project.activity.share.ShareActivity;
import com.youmai.project.activity.user.LoginActivity;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.Util;
import com.youmai.project.view.CircleImageView;
import com.youmai.project.view.ClickImageView;
import com.youmai.project.view.ClickTextView;
import com.youmai.project.view.Shimmer;
import com.youmai.project.view.ShimmerTextView;

import java.util.ArrayList;
import java.util.List;

public class RecommendedAdapter extends BaseAdapter{

	private Context context;
	private List<GoodsBean> list;
	private GoodsBean goodsBean;
	private List<ImageView> imgList=new ArrayList<>();
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
			holder.tvNewMoney.setText("现价：¥"+ Util.setDouble(goodsBean.getPresentPrice()/100));
			holder.tvOldMoney.setText("原价：¥"+Util.setDouble(goodsBean.getOriginalPrice()/100));
			holder.tvOldMoney.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

//			holder.shimmer.start(holder.shimmerTextView);
			if(goodsBean.getImgList().size()>0){
				String imgUrl=goodsBean.getImgList().get(0);
				holder.imgIcon.setTag(R.id.imageid,imgUrl);
				if(holder.imgIcon.getTag(R.id.imageid)!=null && imgUrl==holder.imgIcon.getTag(R.id.imageid)){
					Glide.with(context).load(imgUrl).override(346,281).centerCrop().error(R.mipmap.icon).into(holder.imgIcon);
				}
			}else{
				holder.imgIcon.setImageDrawable(null);
			}
			Glide.with(context).load(goodsBean.getHead()).error(R.mipmap.icon).into(holder.circleImageView);
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
							Bundle bundle=new Bundle();
							bundle.putSerializable("goodsBean",goodsBean);
							intent.putExtras(bundle);
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
						Bundle bundle=new Bundle();
						bundle.putSerializable("goodsBean",goodsBean);
						intent.putExtras(bundle);
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
			holder.imgReport.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

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
				imgList.get(i).setImageDrawable(context.getResources().getDrawable(R.mipmap.yes_select_x));
			}else{
				imgList.get(i).setImageDrawable(context.getResources().getDrawable(R.mipmap.no_select_x));
			}
		}
	}
	
	private class ViewHolder{
		private TextView tvContext,tvLocation,tvNewMoney,tvOldMoney,tvNickName;
		private ClickTextView tvBuy;
		private ImageView imgIcon,imgX1,imgX2,imgX3,imgX4,imgX5,imgShare;
		private ClickImageView imgReport;
		private CircleImageView circleImageView;
//		private ShimmerTextView shimmerTextView;
//		private Shimmer shimmer;
	 }
}
