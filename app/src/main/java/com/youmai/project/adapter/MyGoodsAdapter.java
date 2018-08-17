package com.youmai.project.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.bean.DeleteBabyCallBack;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.utils.DateUtil;
import com.youmai.project.utils.Util;

import java.util.List;

public class MyGoodsAdapter extends BaseAdapter{

	private Context context;
	private List<GoodsBean> list;
	private GoodsBean myGoods;
	private DeleteBabyCallBack deleteBabyCallBack;
	public MyGoodsAdapter(Context context,List<GoodsBean> list) {
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
			view = LayoutInflater.from(context).inflate(R.layout.mybyby_item, null);
			holder.tvAddress=(TextView)view.findViewById(R.id.tv_mi_address);
			holder.imageView=(ImageView)view.findViewById(R.id.img_mi_icon);
			holder.tvDes=(TextView)view.findViewById(R.id.tv_mi_des);
			holder.tvTime=(TextView)view.findViewById(R.id.tv_mi_creatTime);
			holder.tvDel=(TextView)view.findViewById(R.id.tv_mi_delete);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		myGoods=list.get(position);
		if(null!=myGoods){
			if(null!=myGoods.getImgList() && myGoods.getImgList().size()>0){
				Glide.with(context).load(myGoods.getImgList().get(0)).error(R.mipmap.icon).into(holder.imageView);
			}
			holder.tvDes.setText(myGoods.getDescription());
			holder.tvAddress.setText(myGoods.getAddress());
			holder.tvTime.setText("发布："+DateUtil.getData(myGoods.getCreateTime()));
			holder.tvDel.setTag(myGoods.getId());
			holder.tvDel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(null==v.getTag()){
						return;
					}
					deleteBabyCallBack.deleteBaby(v.getTag().toString());
				}
			});
		}
		return view;
	}


	public void setCallBack(DeleteBabyCallBack deleteBabyCallBack){
		this.deleteBabyCallBack=deleteBabyCallBack;
	}
	
	private class ViewHolder{
		private ImageView imageView;
		private TextView tvAddress,tvDes,tvTime,tvDel;
	 }
}
