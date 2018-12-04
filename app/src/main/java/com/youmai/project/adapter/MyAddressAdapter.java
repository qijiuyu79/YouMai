package com.youmai.project.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.youmai.project.R;
import com.youmai.project.activity.user.AddAddressActivity;
import com.youmai.project.bean.Address;
import java.util.List;
public class MyAddressAdapter extends BaseAdapter{

	private Activity activity;
	private List<Address.AddressBean> list;
	public MyAddressAdapter(Activity activity, List<Address.AddressBean> list) {
		super();
		this.activity = activity;
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
			view = LayoutInflater.from(activity).inflate(R.layout.address_item, null);
			holder.tvName=(TextView)view.findViewById(R.id.tv_name);
			holder.tvPhone=(TextView)view.findViewById(R.id.tv_phone);
			holder.tvAddress=(TextView)view.findViewById(R.id.tv_address);
			holder.linEdit=(LinearLayout)view.findViewById(R.id.lin_edit);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		Address.AddressBean addressBean=list.get(position);
		holder.tvName.setText(addressBean.getName());
		holder.tvPhone.setText(addressBean.getMobile());
		holder.tvAddress.setText(addressBean.getArea()+addressBean.getAddress());
		holder.linEdit.setTag(addressBean);
		//编辑地址
		holder.linEdit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(null==v.getTag()){
					return;
				}
				final Address.AddressBean addressBean= (Address.AddressBean) v.getTag();
				Intent intent=new Intent(activity, AddAddressActivity.class);
				intent.putExtra("addressBean",addressBean);
				activity.startActivityForResult(intent,2);
			}
		});
		return view;
	}
	
	private class ViewHolder{
		private TextView tvName,tvPhone,tvAddress;
		private LinearLayout linEdit;
	 }
}
