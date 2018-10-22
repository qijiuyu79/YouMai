package com.youmai.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.share.ShareActivity;
import com.youmai.project.callback.DeleteBabyCallBack;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.utils.DateUtil;
import java.util.List;

public class MyGoodsAdapter extends BaseAdapter{

	private Context context;
	private List<GoodsBean> list;
	private GoodsBean goodsBean;
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
			holder.imgShare=(ImageView)view.findViewById(R.id.tv_mi_share);
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
					Glide.with(context).load(imgUrl).override(100,100).centerCrop().error(R.mipmap.icon).into(holder.imageView);
				}
			}
			holder.tvDes.setText(goodsBean.getDescription());
			holder.tvAddress.setText(goodsBean.getAddress());
			holder.tvTime.setText("发布："+DateUtil.getData(goodsBean.getCreateTime()));
			holder.tvDel.setTag(goodsBean.getId());
			//删除
			holder.tvDel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(null==v.getTag()){
						return;
					}
					deleteBabyCallBack.deleteBaby(v.getTag().toString());
				}
			});
			holder.imgShare.setTag(goodsBean);
			//分享
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


	public void setCallBack(DeleteBabyCallBack deleteBabyCallBack){
		this.deleteBabyCallBack=deleteBabyCallBack;
	}
	
	private class ViewHolder{
		private ImageView imageView,imgShare;
		private TextView tvAddress,tvDes,tvTime,tvDel;
	 }
}
