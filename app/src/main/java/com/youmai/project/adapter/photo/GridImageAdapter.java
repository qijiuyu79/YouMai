package com.youmai.project.adapter.photo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.youmai.project.activity.center.AddShopActivity;
import com.youmai.project.application.MyApplication;
import com.youmai.project.R;
import com.youmai.project.http.HttpConstant;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.photo.Bimp;
import com.youmai.project.utils.photo.ImageItem;
import com.youmai.project.view.ClickImageView;

import java.util.List;

public class GridImageAdapter extends BaseAdapter {

	private List<ImageItem> list;
	private LayoutInflater inflater;
	private Context context;
	public GridImageAdapter(Context context, List<ImageItem> list){
		this.context=context;
		this.list=list;
		this.inflater = LayoutInflater.from(context);
	}
	public int getCount() {
		return list.size()+1;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = this.inflater.inflate(R.layout.selectphoto_item, null);
			holder.grida_image=(ImageView)convertView.findViewById(R.id.item_grida_image);
			holder.imgDelete=(ClickImageView)convertView.findViewById(R.id.img_si_delete);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position==list.size()){
			holder.imgDelete.setVisibility(View.GONE);
			holder.grida_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.add_photo_icon));
		}else{
			holder.imgDelete.setVisibility(View.VISIBLE);
			holder.imgDelete.setTag(position);
			String imgPath=list.get(position).getImagePath();
			if(!TextUtils.isEmpty(imgPath)){
				if(imgPath.indexOf(HttpConstant.IP)==-1){
					Glide.with(MyApplication.application).load("file://" +imgPath).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.grida_image);
				}else{
					Glide.with(MyApplication.application).load(imgPath).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.grida_image);
				}
			}
			holder.imgDelete.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(v.getTag()!=null){
						final int position= (int) v.getTag();
						list.remove(position);
						GridImageAdapter.this.notifyDataSetChanged();
					}
				}
			});
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView grida_image;
		ClickImageView imgDelete;
	}
}
