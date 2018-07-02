package com.youmai.project.adapter.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.youmai.project.R;
import com.youmai.project.bean.ChatMessage;
import com.youmai.project.utils.chat.SpanStringUtils;

import java.util.List;

/**
 * 聊天adapter
 */
public class ChatMessageAdapter extends BaseAdapter {

	private Context context;
	private List<ChatMessage> list;
	private LayoutInflater inflater;
	public ChatMessageAdapter(Context context, List<ChatMessage> list) {
		super();
		this.context = context;
		this.list=list;
		this.inflater = LayoutInflater.from(context);
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

	private View createViewByMessage(ChatMessage chatMessage){
		if(chatMessage!=null){
			final int msgType=chatMessage.getType();
			if(msgType==1){
				return inflater.inflate(R.layout.chat_text_item,null);
			}
			if(msgType==3){
				return inflater.inflate(R.layout.chat_location_item,null);
			}
		}
		return null;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ChatMessage chatMessage=list.get(position);
		final int msgType=chatMessage.getType();
		ViewHolder holder = null;
		if(view==null){
			holder = new ViewHolder(); 
			view = createViewByMessage(chatMessage);
			if(msgType==1){
				holder.tvChatContent=(TextView)view.findViewById(R.id.tv_chatcontent);
			}
			if(msgType==3){
				holder.tvLocation=(TextView)view.findViewById(R.id.tv_cli_location);
			}
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}


		if(msgType==1){
			holder.tvChatContent.setText(SpanStringUtils.getEmotionContent(context,holder.tvChatContent, chatMessage.getText()));
		}
		if(msgType==3){
			holder.tvLocation.setText(chatMessage.getAddress());
			final LatLng latLng=new LatLng(chatMessage.getLatitude(),chatMessage.getLongitude());
			holder.tvLocation.setOnClickListener(new MapClickListener(latLng, chatMessage.getAddress()));
		}
		return view;
	}
	
	private class ViewHolder{
		private TextView tvChatContent,tvLocation;
	 }


	/*
    * 点击地图消息listener
    */
	class MapClickListener implements View.OnClickListener {

		LatLng location;
		String address;

		public MapClickListener(LatLng loc, String address) {
			location = loc;
			this.address = address;

		}

		@Override
		public void onClick(View v) {
//			Intent intent = new Intent(context, BaiduMapActivity.class);
//			intent.putExtra("latitude", location.latitude);
//			intent.putExtra("longitude", location.longitude);
//			intent.putExtra("address", address);
//			context.startActivity(intent);
		}
	}
}
