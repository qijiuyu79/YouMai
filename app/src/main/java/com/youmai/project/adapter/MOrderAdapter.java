package com.youmai.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.utils.Util;
import java.util.List;

/**
 * 卖家订单
 */
public class MOrderAdapter extends BaseAdapter{

	private Context context;
	private List<GoodsBean> list;
	private GoodsBean goodsBean;
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

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder = null;
		if(view==null){
			holder = new ViewHolder(); 
			view = LayoutInflater.from(context).inflate(R.layout.m_order_item, null);
			holder.imageView=(ImageView)view.findViewById(R.id.img_psi_icon);
			holder.imgType=(ImageView)view.findViewById(R.id.img_oi_type);
			holder.tv_psi_des=(TextView)view.findViewById(R.id.tv_psi_des);
			holder.tvName=(TextView)view.findViewById(R.id.tv_psi_name);
			holder.tvMoney=(TextView)view.findViewById(R.id.tv_oi_money);
			holder.tvPhone=(TextView)view.findViewById(R.id.tv_oi_phone);
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
					Glide.with(context).load(imgUrl).centerCrop().error(R.mipmap.icon).into(holder.imageView);
				}
			}
			holder.tvMoney.setText(Util.setDouble(goodsBean.getPresentPrice()/100));
			holder.tvName.setText(goodsBean.getNickname());
			holder.tv_psi_des.setText(goodsBean.getDescription());
			switch (goodsBean.getStated()){
				case 1:
					holder.imgType.setImageDrawable(context.getResources().getDrawable(R.mipmap.yizhifu));
					break;
				case 2:
					holder.imgType.setImageDrawable(context.getResources().getDrawable(R.mipmap.yiwancheng));
					break;
				case 4:
					holder.imgType.setImageDrawable(context.getResources().getDrawable(R.mipmap.yiquxiao));
					break;
				default:
					break;
			}
			holder.tvPhone.setTag(goodsBean.getMobile());
			holder.tvPhone.setOnClickListener(new View.OnClickListener() {
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
		}
		return view;
	}

	private class ViewHolder{
		private ImageView imageView,imgType;
		private TextView tvName,tv_psi_des,tvMoney,tvPhone;
	 }
}
