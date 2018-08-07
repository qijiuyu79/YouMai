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

import java.util.List;

public class OrderAdapter extends BaseAdapter{

	private Context context;
	private List<GoodsBean> list;
	private GoodsBean myGoods;
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
			holder.tv_psi_des=(TextView)view.findViewById(R.id.tv_psi_des);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		myGoods=list.get(position);
		if(null!=myGoods){
			if(null!=myGoods.getImgList() && myGoods.getImgList().size()>0){
				Glide.with(context).load(myGoods.getImgList().get(0)).error(R.mipmap.icon).into(holder.imageView);
			}
			holder.tv_psi_des.setText(myGoods.getDescription());
		}
		return view;
	}
	
	private class ViewHolder{
		private ImageView imageView;
		private TextView tv_psi_des;
	 }
}
