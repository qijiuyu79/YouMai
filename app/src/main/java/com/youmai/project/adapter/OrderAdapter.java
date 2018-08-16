package com.youmai.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.bean.TradingPlay;
import com.youmai.project.view.ClickTextView;

import java.util.List;

public class OrderAdapter extends BaseAdapter{

	private Context context;
	private List<GoodsBean> list;
	private GoodsBean goodsBean;
	private TradingPlay tradingPlay;
	public OrderAdapter(Context context, List<GoodsBean> list) {
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
			view = LayoutInflater.from(context).inflate(R.layout.order_item, null);
			holder.imageView=(ImageView)view.findViewById(R.id.img_psi_icon);
			holder.imgType=(ImageView)view.findViewById(R.id.img_oi_type);
			holder.tv_psi_des=(TextView)view.findViewById(R.id.tv_psi_des);
			holder.tvComplete=(ClickTextView)view.findViewById(R.id.tv_oi_complete);
			holder.tvCancle=(ClickTextView)view.findViewById(R.id.tv_oi_cancle);
			holder.tvName=(TextView)view.findViewById(R.id.tv_psi_name);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		goodsBean=list.get(position);
		if(null!=goodsBean){
			if(null!=goodsBean.getImgList() && goodsBean.getImgList().size()>0){
				Glide.with(context).load(goodsBean.getImgList().get(0)).error(R.mipmap.icon).into(holder.imageView);
			}
			holder.tvName.setText(goodsBean.getNickname());
			holder.tv_psi_des.setText(goodsBean.getDescription());
			switch (goodsBean.getStated()){
				case 1:
					 holder.imgType.setImageDrawable(context.getResources().getDrawable(R.mipmap.yizhifu));
					 holder.tvCancle.setVisibility(View.VISIBLE);
					 holder.tvComplete.setText("交易完成");
					 break;
				case 2:
					holder.imgType.setImageDrawable(context.getResources().getDrawable(R.mipmap.yiwancheng));
					holder.tvCancle.setVisibility(View.GONE);
					holder.tvComplete.setText("评价晒单");
					break;
				case 4:
					holder.imgType.setImageDrawable(context.getResources().getDrawable(R.mipmap.yiquxiao));
					holder.tvCancle.setVisibility(View.GONE);
					holder.tvComplete.setText("重新购买");
					break;
					default:
						break;
			}
			holder.tvComplete.setTag(goodsBean.getOrderId());
			holder.tvCancle.setTag(goodsBean.getOrderId());
			holder.tvComplete.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(null==v.getTag()){
						return;
					}
					final String orderId=v.getTag().toString();
					tradingPlay.complete(orderId);
				}
			});
			holder.tvCancle.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(null==v.getTag()){
						return;
					}
					final String orderId=v.getTag().toString();
					tradingPlay.cancle(orderId);
				}
			});
		}
		return view;
	}


	public void setCallBack(TradingPlay tradingPlay){
		this.tradingPlay=tradingPlay;
	}
	
	private class ViewHolder{
		private ImageView imageView,imgType;
		private TextView tvName,tv_psi_des;
		private ClickTextView tvComplete,tvCancle;
	 }
}
