package com.gfq.myvdo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class VideoView extends android.widget.VideoView{

	public VideoView(Context context) {
		this(context,null);
		// TODO 自动生成的构造函数存根
	}


	public VideoView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO 自动生成的构造函数存根
	}
	
	public VideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO 自动生成的构造函数存根
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void setVideoSize(int w,int h) {
		ViewGroup.LayoutParams params=getLayoutParams();
		params.width=w;
		params.height=h;
		setLayoutParams(params);
	}
}
