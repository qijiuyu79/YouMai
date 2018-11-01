package com.youmai.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.share.ShareActivity;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.callback.TradingPlay;
import com.youmai.project.utils.Util;
import com.youmai.project.view.ClickLinearLayout;
import com.youmai.project.view.ClickTextView;
import java.util.ArrayList;
import java.util.List;

/**
 * 卖家订单
 */
public class MOrderAdapter extends BaseAdapter{

	private Context context;
	private List<GoodsBean> list;
	private GoodsBean goodsBean;
	private List<ImageView> imgList=new ArrayList<>();
	private TradingPlay tradingPlay;
	public MOrderAdapter(Context context, List<GoodsBean> list) {
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
			view = LayoutInflater.from(context).inflate(R.layout.m_order_item, null);
			holder.imageView=(ImageView)view.findViewById(R.id.img_psi_icon);
			holder.imgType=(ImageView)view.findViewById(R.id.img_oi_type);
			holder.tv_psi_des=(TextView)view.findViewById(R.id.tv_psi_des);
			holder.tvName=(TextView)view.findViewById(R.id.tv_psi_name);
			holder.tvMoney=(TextView)view.findViewById(R.id.tv_oi_money);
			holder.imgX1=(ImageView)view.findViewById(R.id.img_ri_x1);
			holder.imgX2=(ImageView)view.findViewById(R.id.img_ri_x2);
			holder.imgX3=(ImageView)view.findViewById(R.id.img_ri_x3);
			holder.imgX4=(ImageView)view.findViewById(R.id.img_ri_x4);
			holder.imgX5=(ImageView)view.findViewById(R.id.img_ri_x5);
			holder.imgShare=(ImageView)view.findViewById(R.id.img_oi_share);
			holder.linPhone=(ClickLinearLayout)view.findViewById(R.id.lin_oi_phone);
			holder.tvCancle=(ClickTextView)view.findViewById(R.id.tv_oi_cancle);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		goodsBean=list.get(position);
		if(null!=goodsBean){
			if(goodsBean.getImgList().size()>0){
				String imgUrl=goodsBean.getImgList().get(0);
				holder.imageView.setTag(R.id.imageid,imgUrl);
				if(holder.imageView.getTag(R.id.imageid)!=null && imgUrl==holder.imageView.getTag(R.id.imageid)){
					Glide.with(context).load(imgUrl).override(102,102).centerCrop().error(R.mipmap.icon).into(holder.imageView);
				}
			}
			holder.tvMoney.setText(Util.setDouble(goodsBean.getPresentPrice()/100));
			holder.tvName.setText(goodsBean.getNickname());
			holder.tv_psi_des.setText(goodsBean.getDescription());
			//设置星级
			setXing(goodsBean.getCreditLevel());
			switch (goodsBean.getStated()){
				case 1:
					holder.imgType.setImageDrawable(context.getResources().getDrawable(R.mipmap.yizhifu));
					holder.tvCancle.setText("交易取消");
					break;
				case 2:
					holder.imgType.setImageDrawable(context.getResources().getDrawable(R.mipmap.yiwancheng));
					break;
				case 4:
					holder.imgType.setImageDrawable(context.getResources().getDrawable(R.mipmap.yiquxiao));
					holder.tvCancle.setText("删除");
					break;
				default:
					break;
			}
			//交易取消/删除订单
			holder.tvCancle.setTag(goodsBean);
			holder.tvCancle.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(null==v.getTag()){
						return;
					}
					tradingPlay.cancle((GoodsBean) v.getTag());
				}
			});
			//联系买家
			holder.linPhone.setTag(goodsBean.getMobile());
			holder.linPhone.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(null==v.getTag()){
						return;
					}
					final String mobile=v.getTag().toString();
					if(!TextUtils.isEmpty(mobile)){
						Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
						context.startActivity(intent);
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

	public void setCallBack(TradingPlay tradingPlay){
		this.tradingPlay=tradingPlay;
	}

	private class ViewHolder{
		private ImageView imageView,imgType,imgShare;
		private ImageView imgX1,imgX2,imgX3,imgX4,imgX5;
		private TextView tvName,tv_psi_des,tvMoney;
		private ClickTextView tvCancle;
		private ClickLinearLayout linPhone;
	 }
}
