package com.gfq.myvdo.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gfq.myvdo.R;
import com.gfq.myvdo.domain.MediaItem;
import com.gfq.myvdo.utiles.Utils;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyVideoNativeAdapter extends BaseAdapter {

	private ArrayList<MediaItem> list;
	private Utils util=new Utils();
	private Context mContext;
	private boolean isVideo;
	public MyVideoNativeAdapter(ArrayList<MediaItem> list,Context c,boolean isVideo) {
		this.list=list;
		mContext=c;
		this.isVideo=isVideo;
	}
	public MyVideoNativeAdapter() {
		// TODO 自动生成的构造函数存根
	}
	
	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null) {
			convertView=View.inflate(mContext, R.layout.item_nativa_vedio, null);
			holder=new ViewHolder();
			holder.img=(ImageView) convertView.findViewById(R.id.iv_icon);
			holder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_time=(TextView) convertView.findViewById(R.id.tv_duration);
			holder.tv_size=(TextView) convertView.findViewById(R.id.tv_size);
			convertView.setTag(holder);
		}
		holder=(ViewHolder) convertView.getTag();
		holder.img.setBackgroundResource(R.drawable.video_default_icon);
		holder.tv_name.setText(list.get(position).getName());
		holder.tv_size.setText(Formatter.formatFileSize(mContext, list.get(position).getSize()));
		holder.tv_time.setText(util.stringForTime((int)list.get(position).getDuration()));
		
		if(!isVideo) {
			holder.img.setBackgroundResource(R.drawable.music_default_bg);
		}
		return convertView;
	}
static class ViewHolder{
	private ImageView img;
	private TextView tv_name;
	private TextView tv_size;
	private TextView tv_time;
}
}
