package com.youmai.project.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.utils.DateUtil;
import com.youmai.project.utils.Util;
import com.youmai.project.view.ClickImageView;

import java.util.List;

public class SellerGoodsAdapter extends BaseAdapter{

	private Context context;
	private List<GoodsBean> list;
	private GoodsBean goodsBean;
	public SellerGoodsAdapter(Context context, List<GoodsBean> list) {
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

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder = null;
		if(view==null){
			holder = new ViewHolder(); 
			view = LayoutInflater.from(context).inflate(R.layout.seller_goods_item, null);
			holder.tvAddress=(TextView)view.findViewById(R.id.tv_sgi_address);
			holder.imageView=(ImageView)view.findViewById(R.id.img_sgi_icon);
			holder.tvDes=(TextView)view.findViewById(R.id.tv_sgi_des);
			holder.tvNewMoney=(TextView)view.findViewById(R.id.tv_sgi_presentPrice);
			holder.tvOldMoney=(TextView)view.findViewById(R.id.tv_sgi_originalPrice);
			holder.tvTime=(TextView)view.findViewById(R.id.tv_sgi_creatTime);
			holder.tvBuy=(TextView)view.findViewById(R.id.tv_sgi_buy);
			holder.imgShare=(ClickImageView)view.findViewById(R.id.img_share);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		goodsBean=list.get(position);
		if(goodsBean!=null){
			if(goodsBean.getImgList().size()>0){
				String imgUrl=goodsBean.getImgList().get(0);
				holder.imageView.setTag(R.id.imageid,imgUrl);
				if(holder.imageView.getTag(R.id.imageid)!=null && imgUrl==holder.imageView.getTag(R.id.imageid)){
					Glide.with(context).load(imgUrl).override(100,100).centerCrop().error(R.mipmap.icon).into(holder.imageView);
				}
			}else{
				holder.imageView.setImageDrawable(null);
			}
			holder.tvAddress.setText(goodsBean.getAddress());
			holder.tvAddress.setTag(goodsBean);
			holder.tvNewMoney.setText("¥"+Util.setDouble(goodsBean.getPresentPrice()/100));
			holder.tvOldMoney.setText("¥"+Util.setDouble(goodsBean.getOriginalPrice()/100));
			holder.tvOldMoney.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			holder.tvDes.setText(goodsBean.getDescription());
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
			holder.tvAddress.setTag(goodsBean);
			holder.tvAddress.setOnClickListener(new View.OnClickListener() {
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
		}
		return view;
	}
	
	private class ViewHolder{
		private TextView tvAddress,tvDes,tvNewMoney,tvOldMoney,tvTime,tvBuy;
		private ImageView imageView;
		private ClickImageView imgShare;
	 }
}
