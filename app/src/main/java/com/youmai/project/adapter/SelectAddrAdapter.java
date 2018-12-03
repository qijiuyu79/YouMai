package com.youmai.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.youmai.project.R;
import java.util.List;
public class SelectAddrAdapter extends BaseAdapter{

	private Context context;
	private List<com.baidu.mapapi.search.core.PoiInfo> list;
	public SelectAddrAdapter(Context context, List<com.baidu.mapapi.search.core.PoiInfo> list) {
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
			view = LayoutInflater.from(context).inflate(R.layout.select_addr_item, null);
			holder.tvAddress=(TextView)view.findViewById(R.id.tv_address);
			holder.tvName=(TextView)view.findViewById(R.id.tv_name);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		holder.tvName.setText(list.get(position).name);
		holder.tvAddress.setText(list.get(position).address);
		return view;
	}



	private class ViewHolder{
		private TextView tvAddress,tvName;
	 }
}
