package com.gfq.myvdo.page;

import android.content.Context;
import android.view.View;

public abstract class BasePage {
	public Context mContext;
	public View rootView;
	public boolean isInitedData=false;
	public BasePage(Context mContext) {
		this.mContext=mContext;
		rootView=initView();
	}
	
	public abstract View initView();
	public void initData() {
		
	}
}
