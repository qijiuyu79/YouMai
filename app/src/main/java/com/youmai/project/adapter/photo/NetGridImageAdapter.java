package com.youmai.project.adapter.photo;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.youmai.project.application.MyApplication;
import com.youmai.project.R;

import java.util.List;

public class NetGridImageAdapter extends BaseAdapter {

	private List<String> selectBitmap;
	private LayoutInflater inflater;
	private Context context;
	public NetGridImageAdapter(Context context, List<String> selectBitmap){
		this.context=context;
		this.selectBitmap=selectBitmap;
		this.inflater = LayoutInflater.from(context);
	}
	public int getCount() {
		if(selectBitmap==null){
			return 0;
		}
		return selectBitmap.size();
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
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		String imgUrl=selectBitmap.get(position);
		if(!TextUtils.isEmpty(imgUrl)){
			holder.grida_image.setTag(R.id.imageid,imgUrl);
			if(holder.grida_image.getTag(R.id.imageid)!=null && imgUrl==holder.grida_image.getTag(R.id.imageid)){
				Glide.with(context).load(imgUrl).override(110,110).centerCrop().error(R.mipmap.icon).into(holder.grida_image);
			}
		}
		return convertView;
	}

	private class ViewHolder {
		 ImageView grida_image;
	}
}
