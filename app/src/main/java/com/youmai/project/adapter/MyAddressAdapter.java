package com.youmai.project.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youmai.project.R;

public class MyAddressAdapter extends BaseAdapter{

	private Context context;
	public MyAddressAdapter(Context context) {
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		return 2;
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
			view = LayoutInflater.from(context).inflate(R.layout.address_item, null);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		return view;
	}
	
	private class ViewHolder{
		private TextView tvOldMoney;
	 }
}
