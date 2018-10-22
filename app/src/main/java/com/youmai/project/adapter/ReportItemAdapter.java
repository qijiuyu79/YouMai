package com.youmai.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.youmai.project.R;
import com.youmai.project.bean.Report;
import java.util.List;

public class ReportItemAdapter extends BaseAdapter{

	private Context context;
	private List<Report.ReportData> list;
	private Report.ReportData reportData;
	private int index=0;
	public ReportItemAdapter(Context context, List<Report.ReportData> list) {
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
			view = LayoutInflater.from(context).inflate(R.layout.report_item, null);
			holder.tvName=(TextView)view.findViewById(R.id.tv_report);
			holder.imageView=(ImageView)view.findViewById(R.id.img_report);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		reportData=list.get(position);
		holder.tvName.setText(reportData.getName());
		if(position==index){
		    holder.imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.report_yes_select));
        }else{
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.report_no_select));
        }
		return view;
	}

	public void setIndex(int index){
	    this.index=index;
    }

	private class ViewHolder{
		private ImageView imageView;
		private TextView tvName;
	 }
}
