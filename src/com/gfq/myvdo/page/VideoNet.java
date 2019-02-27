package com.gfq.myvdo.page;

import com.gfq.myvdo.R;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VideoNet extends BasePage{

	private ListView listView;
	private TextView textView;
	private ProgressBar progressBar;
	public VideoNet(Context mContext) {
		super(mContext);
	}

	@Override
	public View initView() {
		View view = View.inflate(mContext, R.layout.video_native_page, null);
		listView=(ListView) view.findViewById(R.id.listview);
		textView=(TextView) view.findViewById(R.id.tv_not_found);
		progressBar=(ProgressBar) view.findViewById(R.id.pb_loading);
		return view;
	}

	
}
