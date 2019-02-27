package com.gfq.myvdo.activity;

import java.util.ArrayList;

import com.gfq.myvdo.R;
import com.gfq.myvdo.page.BasePage;
import com.gfq.myvdo.page.MusicNative;
import com.gfq.myvdo.page.MusicNet;
import com.gfq.myvdo.page.VideoNative;
import com.gfq.myvdo.page.VideoNet;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnCheckedChangeListener{
	
	private ArrayList<BasePage> list;
	private VideoNative videoNative;
	private VideoNet videoNet;
	private MusicNet musicNet;
	private MusicNative musicNative;
	private Context mContext;
	private FrameLayout frameLayout;
	private RadioGroup radioGroup; 
	private int position;
	private boolean isInitedData=false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		initVars();
		
		addPage2List();
		
		radioGroup.setOnCheckedChangeListener(this);
		radioGroup.check(0);
		
	
	}
	
	private void addPage2List() {
		list.add(videoNative);
		list.add(musicNative);
		list.add(musicNet);
		list.add(videoNet);
	}
	private void initVars() {
		mContext=MainActivity.this;
		position=0;
		list=new ArrayList<>();
		videoNative=new VideoNative(mContext);
		videoNet=new VideoNet(mContext);
		musicNet=new MusicNet(mContext);
		musicNative=new MusicNative(mContext);
		frameLayout=(FrameLayout) findViewById(R.id.fl_main);
		radioGroup=(RadioGroup) findViewById(R.id.rg);
		
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_video:
			position=0;
			break;
			
		case R.id.rb_net_video:
			position=1;
			break;
			
		case R.id.rb_audio:
			position=2;
			break;

		case R.id.rb_net_audio:
			position=3;
			break;
			
		default:
			position=0;
			break;
		}
		
		showPage();
	}
	private void showPage() {
		FragmentManager fm=getFragmentManager();
		FragmentTransaction ft=fm.beginTransaction();
		ft.replace(R.id.fl_main, new Fragment(){
			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				BasePage page=getPage();
				if(page!=null) {
					return page.rootView;
				}
				return null;					
			}
			
		});
		ft.commit();
		
	}
	protected BasePage getPage() {
		BasePage page =list.get(position);
		if(page!=null&&!isInitedData) {
			page.initData();
			isInitedData=true;
		}
		return page;
		
	}
//	private boolean hasLength(ArrayList<BasePage> list) {
//		return list!=null&&list.size()>0;
//	}
}