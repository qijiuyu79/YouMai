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
import com.youmai.project.activity.user.LoginActivity;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.utils.Util;
import com.youmai.project.view.ClickTextView;

import java.util.List;

public class RecommendedAdapter extends BaseAdapter{

	private Context context;
	private List<GoodsBean> list;
	private GoodsBean goodsBean;
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

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder = null;
		if(view==null){
			holder = new ViewHolder(); 
			view = LayoutInflater.from(context).inflate(R.layout.recommended_item, null);
			holder.tvContext=(TextView)view.findViewById(R.id.tv_ri_content);
            holder.tvOldMoney=(TextView)view.findViewById(R.id.tv_ri_oldMoney);
			holder.tvLocation=(TextView)view.findViewById(R.id.tv_ri_location);
			holder.tvNewMoney=(TextView)view.findViewById(R.id.tv_ri_newMoney);
			holder.imgIcon=(ImageView)view.findViewById(R.id.img_ri_icon);
			holder.tvBuy=(ClickTextView)view.findViewById(R.id.tv_ri_buy);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		goodsBean=list.get(position);
		if(goodsBean!=null){
			holder.tvContext.setText(goodsBean.getDescription());
			holder.tvLocation.setText(goodsBean.getAddress());
			holder.tvNewMoney.setText("现价：¥"+ Util.setDouble(goodsBean.getPresentPrice()/100));
			holder.tvOldMoney.setText("原价：¥"+Util.setDouble(goodsBean.getOriginalPrice()/100));
			holder.tvOldMoney.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			if(null!=goodsBean.getImgList() && goodsBean.getImgList().size()>0){
				Glide.with(context).load(goodsBean.getImgList().get(0)).error(R.mipmap.icon).into(holder.imgIcon);
			}
			holder.tvBuy.setTag(goodsBean);
			holder.tvBuy.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(!Util.isLogin(context)){
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
		}
		return view;
	}
	
	private class ViewHolder{
		private TextView tvContext,tvLocation,tvNewMoney,tvOldMoney;
		private ClickTextView tvBuy;
		private ImageView imgIcon;
	 }
}
