package com.youmai.project.adapter.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.youmai.project.R;
import com.youmai.project.activity.order.ChatActivity;
import com.youmai.project.utils.chat.ChatIcon;
import com.youmai.project.utils.chat.SpanStringUtils;

import java.util.List;

/**
 * 表情包adapter
 */
public class ChatIconAdapter extends BaseAdapter {

	private Context context;
	private List<String> list;
	public ChatIconAdapter(Context context, List<String> list) {
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
			view = LayoutInflater.from(context).inflate(R.layout.chat_icon_item, null);
			holder.img=(ImageView)view.findViewById(R.id.img_cii);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		holder.img.setImageResource(ChatIcon.iconMap.get(list.get(position)));
		holder.img.setTag(position);
		holder.img.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String emotionName=list.get(Integer.parseInt(v.getTag().toString()));

				// 获取当前光标位置,在指定位置上添加表情图片文本
				int curPosition = ((ChatActivity)context).etSendMsg.getSelectionStart();
				StringBuilder sb = new StringBuilder(((ChatActivity)context).etSendMsg.getText().toString());
				sb.insert(curPosition, emotionName);

				// 特殊文字处理,将表情等转换一下
				((ChatActivity)context).etSendMsg.setText(SpanStringUtils.getEmotionContent(context, ((ChatActivity)context).etSendMsg, sb.toString()));

				// 将光标设置到新增完表情的右侧
				((ChatActivity)context).etSendMsg.setSelection(curPosition + emotionName.length());

			}
		});
		return view;
	}
	
	private class ViewHolder{
		private ImageView img;
	 }
}
