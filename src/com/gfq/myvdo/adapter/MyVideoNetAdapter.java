package com.gfq.myvdo.adapter;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

public class MyVideoNetAdapter extends BaseAdapter {

	private ArrayList<MediaItem> list;
	private Context mContext;
	public MyVideoNetAdapter(ArrayList<MediaItem> list,Context c) {
		this.list=list;
		mContext=c;
	}
	public MyVideoNetAdapter() {
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
			convertView=View.inflate(mContext, R.layout.item_video_net, null);
			holder=new ViewHolder();
			holder.img=(ImageView) convertView.findViewById(R.id.iv_icon);
			holder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_desc=(TextView) convertView.findViewById(R.id.tv_description);
			convertView.setTag(holder);
		}
		holder=(ViewHolder) convertView.getTag();
		//x.image().bind(holder.img, list.get(position).getImageUrl());
		Glide.with(mContext).load(list.get(position).getImageUrl())
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.ic_launcher)
				.error(R.drawable.ic_launcher)
				.into(holder.img);
		holder.tv_name.setText(list.get(position).getName());
		holder.tv_desc.setText(list.get(position).getDesc());
		return convertView;
	}
static class ViewHolder{
	private ImageView img;
	private TextView tv_name;
	private TextView tv_desc;
}
}
