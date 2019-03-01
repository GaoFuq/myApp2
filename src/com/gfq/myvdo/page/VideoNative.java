package com.gfq.myvdo.page;

import java.util.ArrayList;
import java.util.List;

import com.gfq.myvdo.R;
import com.gfq.myvdo.activity.MyVedioPlayer;
import com.gfq.myvdo.adapter.MyVideoNativeAdapter;
import com.gfq.myvdo.domain.MediaItem;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VideoNative extends BasePage implements OnItemClickListener{

	private ListView listView;
	private TextView textView;
	private ProgressBar progressBar;
	private View view;
	private BaseAdapter adapter;
	private ArrayList<MediaItem> items;
	private Handler handler=new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(hasLength(items)) {
				adapter=new MyVideoNativeAdapter(items,mContext,true);
				listView.setAdapter(adapter);
				textView.setVisibility(View.GONE);
			}else {
				textView.setVisibility(View.VISIBLE);
			}
			progressBar.setVisibility(View.GONE);
		};
	};
	public VideoNative(Context mContext) {
		super(mContext);
	}

	protected boolean hasLength(List list) {
		// TODO 自动生成的方法存根
		return  list!=null&&list.size()>0;
	}

	@Override
	public View initView() {
		items=new ArrayList<>();
		view= View.inflate(mContext, R.layout.video_native_page, null);
		listView=(ListView) view.findViewById(R.id.listview);
		textView=(TextView) view.findViewById(R.id.tv_not_found);
		progressBar=(ProgressBar) view.findViewById(R.id.pb_loading);
		
		listView.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void initData() {
		super.initData();
		getDataFromLocal();
	}

	private void getDataFromLocal() {
		new Thread() {
			public void run() {
				super.run();
				ContentResolver rs=mContext.getContentResolver();
				Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				String[] projection = {
						MediaStore.Video.Media.DISPLAY_NAME,//
						MediaStore.Video.Media.DURATION,//
						MediaStore.Video.Media.SIZE,//
						MediaStore.Video.Media.DATA,//播放地址
						MediaStore.Video.Media.ARTIST,//
				};
				Cursor cursor=rs.query(uri, projection, null, null, null);
				if(cursor!=null) {
					while(cursor.moveToNext()) {
						MediaItem item = new MediaItem();
						items.add(item);
						String name=cursor.getString(0);
						long duration=cursor.getLong(1);
						long size=cursor.getLong(2);
						String data=cursor.getString(3);
						String artist=cursor.getString(4);
						
						item.setName(name);
						item.setDuration(duration);
						item.setSize(size);
						item.setData(data);
						item.setArtist(artist);
					}
					cursor.close();
				}
				
				//更新视图
				handler.sendEmptyMessage(0);
				
			};
		}.start();
			
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent=new Intent(mContext,MyVedioPlayer.class);
		Bundle bd=new Bundle();
		bd.putSerializable("videoList", items);
		intent.putExtras(bd);
		intent.putExtra("position", position);
		mContext.startActivity(intent);
	}
	
}
