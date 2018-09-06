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
import com.youmai.project.bean.Comment;
import com.youmai.project.utils.DateUtil;
import com.youmai.project.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends BaseAdapter{

	private Context context;
	private List<Comment> listAll;
	private Comment comment;
	private List<ImageView> imgList=new ArrayList<>();
	public CommentAdapter(Context context,List<Comment> listAll) {
		super();
		this.context = context;
		this.listAll=listAll;
	}

	@Override
	public int getCount() {
		return listAll==null ? 0 : listAll.size();
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
			view = LayoutInflater.from(context).inflate(R.layout.comment_item, null);
			holder.imgHead=(ImageView)view.findViewById(R.id.img_ci_head);
			holder.tvNickName=(TextView)view.findViewById(R.id.tv_ci_name);
			holder.imgX1=(ImageView)view.findViewById(R.id.img_au_x1);
			holder.imgX2=(ImageView)view.findViewById(R.id.img_au_x2);
			holder.imgX3=(ImageView)view.findViewById(R.id.img_au_x3);
			holder.imgX4=(ImageView)view.findViewById(R.id.img_au_x4);
			holder.imgX5=(ImageView)view.findViewById(R.id.img_au_x5);
			holder.tvEvaluate=(TextView)view.findViewById(R.id.tv_ci_evaluate);
			holder.tvTime=(TextView)view.findViewById(R.id.tv_ci_time);
			holder.imgGood=(ImageView)view.findViewById(R.id.img_mi_icon);
			holder.tvDes=(TextView)view.findViewById(R.id.tv_mi_des);
			holder.tvMoney=(TextView)view.findViewById(R.id.tv_ci_money);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		comment=listAll.get(position);
		if(null==comment){
			return null;
		}
		final String imgHead=comment.getHead();
		holder.imgHead.setTag(R.id.imgHead,imgHead);
		if(holder.imgHead.getTag(R.id.imgHead)!=null && imgHead==holder.imgHead.getTag(R.id.imgHead)){
			Glide.with(context).load(imgHead).override(33,33).centerCrop().error(R.mipmap.icon).into(holder.imgHead);
		}
		holder.tvNickName.setText(comment.getNickname());
		//设置星级
		setXing(comment.getCreditLevel());
		holder.tvEvaluate.setText(comment.getEvaluate());
		holder.tvTime.setText(DateUtil.getData(comment.getCreateTime()));


		//设置商品信息
		if(comment.getImgList().size()>0){
			String imgUrl=comment.getImgList().get(0);
			holder.imgGood.setTag(R.id.imageid,imgUrl);
			if(holder.imgGood.getTag(R.id.imageid)!=null && imgUrl==holder.imgGood.getTag(R.id.imageid)){
				Glide.with(context).load(imgUrl).override(59,59).centerCrop().error(R.mipmap.icon).animate(R.anim.item_alpha_in).into(holder.imgGood);
			}
		}
		holder.tvDes.setText(comment.getDescription());
		holder.tvMoney.setText(Util.setDouble(comment.getPresentPrice()/100));
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
		private ImageView imgHead,imgGood;
		private TextView tvNickName,tvDes,tvTime,tvEvaluate,tvMoney;
		private ImageView imgX1,imgX2,imgX3,imgX4,imgX5;
	 }
}
