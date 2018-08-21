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
import com.youmai.project.utils.Util;

import java.util.List;

public class MapGoodsListAdapter extends BaseAdapter{

	private Context context;
	private List<GoodsBean> list;
	private GoodsBean goodsBean;
	public MapGoodsListAdapter(Context context, List<GoodsBean> list) {
		super();
		this.context = context;
		this.list=list;
	}

	@Override
	public int getCount() {
		if(null==list){
			return 0;
		}
		if(list.size()>3){
			return 3;
		}else{
			return list.size();
		}
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
			view = LayoutInflater.from(context).inflate(R.layout.map_goods_item, null);
			holder.imageView=(ImageView)view.findViewById(R.id.img_mgi_photo);
			holder.tvMoney=(TextView)view.findViewById(R.id.tv_mgi_money);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		goodsBean=list.get(position);
		if(goodsBean!=null){
			if(goodsBean.getImgList().size()>0){
				Glide.with(context).load(goodsBean.getImgList().get(0)).centerCrop().error(R.mipmap.icon).into(holder.imageView);
			}else{
				holder.imageView.setImageDrawable(null);
			}
			holder.tvMoney.setText("¥ "+Util.setDouble(goodsBean.getPresentPrice()/100));
		}
		return view;
	}
	
	private class ViewHolder{
		private ImageView imageView;
		private TextView tvMoney;
	 }
}
