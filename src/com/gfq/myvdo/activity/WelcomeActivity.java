package com.gfq.myvdo.activity;

import com.gfq.myvdo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends Activity {
	private Handler handle = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what==0) {
				Log.d("ss", Thread.currentThread().getName());
				startMainActivity();
			}
		};
	};
	private boolean isStart;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Log.d("yy", Thread.currentThread().getName());
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				
				Message msg=new Message();
				msg.what=0;
				handle.sendMessage(msg);
			}
		}).start();
	}

	
	
	protected void startMainActivity() {
		if (!isStart) {
			isStart=true;
			startActivity(new Intent(this, MainActivity.class));
			this.finish();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		startMainActivity();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {

		handle.removeCallbacksAndMessages(null);
	
		super.onDestroy();
	}
}
