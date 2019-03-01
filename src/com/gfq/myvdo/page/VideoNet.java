package com.gfq.myvdo.page;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.maxwin.view.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import com.gfq.myvdo.R;
import com.gfq.myvdo.activity.MyVedioPlayer;
import com.gfq.myvdo.adapter.MyVideoNetAdapter;
import com.gfq.myvdo.domain.MediaItem;
import com.gfq.myvdo.utiles.CacheUtil;
import com.gfq.myvdo.utiles.Constants;
import com.gfq.myvdo.utiles.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VideoNet extends BasePage implements OnItemClickListener{

	@ViewInject(R.id.listview)
	private XListView xListView;
	
	@ViewInject(R.id.tv_noNet)
	private TextView tv_noNet;

	@ViewInject(R.id.pb_loading)
	private ProgressBar bar;
	
	private ArrayList<MediaItem> items;

	private MyVideoNetAdapter adapter;

	private boolean isLoadMore=false;
	public VideoNet(Context mContext) {
		super(mContext);
	
	}

	@Override
	public View initView() {
		View view = View.inflate(mContext, R.layout.video_net_page, null);
		x.view().inject(this,view);//映射关联
		xListView.setOnItemClickListener(this);
		xListView.setPullLoadEnable(true);
		xListView.setXListViewListener(new MyXListViewListener());
		return view;
	}
	
	class MyXListViewListener implements XListView.IXListViewListener{

		@Override
		public void onRefresh() {
			// TODO 自动生成的方法存根
			getDataFromNet();
		}

		@Override
		public void onLoadMore() {
			// TODO 自动生成的方法存根
			getMoreDataFromNet();
		}

		
		
	}
	@Override
	public void initData() {
		super.initData();
		String saveJson=CacheUtil.getString(mContext, Constants.uri_net);
		if(!TextUtils.isEmpty(saveJson)) {
			processData(saveJson);
		}
		getDataFromNet();
	}

	private void getMoreDataFromNet() {
		RequestParams params=new RequestParams(Constants.uri_net);
		x.http().get(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				isLoadMore=true;
				processData(result);
			}
			@Override
			public void onError(Throwable ex, boolean arg1) {
				isLoadMore=false;
			}
			@Override
			public void onCancelled(CancelledException cex) {
				isLoadMore=false;
			}
			@Override
			public void onFinished() {
				isLoadMore=false;
			}

			
		});
	}
	private void getDataFromNet() {
		RequestParams params=new RequestParams(Constants.uri_net);
		x.http().get(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				//缓存数据
				CacheUtil.putString(mContext, Constants.uri_net, result);
				processData(result);
			}
			@Override
			public void onError(Throwable ex, boolean arg1) {
				showData();
			}
			@Override
			public void onCancelled(CancelledException cex) {
				Log.d("onSuccess", cex.getMessage());				
			}
			@Override
			public void onFinished() {
				Log.d("onSuccess","");				
			}

			
		});
	}
	
	private void onLoad() {
		xListView.stopRefresh();
		xListView.stopLoadMore();
		xListView.setRefreshTime("更新时间:"+getSystemTime());
	}

	public String getSystemTime() {
		SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
		return format.format(new Date());
	}
	//得到数据
	private void processData(String json) {
		if(!isLoadMore) {
			//解析数据
			items=parseJson(json);
			showData();
		}else {
			isLoadMore=false;
			//加载更多
			//将新数据添加到原来的集合中
			//ArrayList<MediaItem> moreLtems=parseJson(json);
			items.addAll(parseJson(json));
			//刷新适配器
			adapter.notifyDataSetChanged();
			
			onLoad();
			
		}
		
	}

	private void showData() {
		//设置适配器
		if(items.size()>0&&items!=null) {
			adapter=new MyVideoNetAdapter(items,mContext);
			xListView.setAdapter(adapter);
			onLoad();
			tv_noNet.setVisibility(View.GONE);
		}else {
			tv_noNet.setVisibility(View.VISIBLE);
		}
		bar.setVisibility(View.GONE);
	}

	

	/**
	 * 解析数据：
	 * 1.用系统接口解析
	 * 2.使用第三方工具
	 * @param json
	 * @return 
	 */
	private ArrayList<MediaItem> parseJson(String json) {
		ArrayList<MediaItem> items=new ArrayList<>();
		try {
			JSONObject jsonObject=new JSONObject(json);
			JSONArray jsonArray=jsonObject.optJSONArray("trailers");
			
			if(jsonArray!=null&&jsonArray.length()>0) {
				
				for(int i=0;i<jsonArray.length();i++) {
					
					JSONObject jsObjectItem=(JSONObject) jsonArray.get(i);
					
					if(jsObjectItem!=null) {
						
						MediaItem item =new MediaItem();
						items.add(item);
						
						String movieName=jsObjectItem.optString("movieName");
						item.setName(movieName);
						
						String videoTitle=jsObjectItem.optString("videoTitle");//desc
						item.setDesc(videoTitle);
						
						String imageUrl=jsObjectItem.optString("coverImg");
						item.setImageUrl(imageUrl);
						
						String hightUrl=jsObjectItem.optString("hightUrl");//data
						item.setData(hightUrl);
						
					}
				}
			}
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
				
		return items;
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent=new Intent(mContext,MyVedioPlayer.class);
		Bundle bd=new Bundle();
		bd.putSerializable("videoList", items);
		intent.putExtras(bd);
		//listView多了一个下拉刷新头，所以要减一
		intent.putExtra("position", position-1);
		mContext.startActivity(intent);
	}
	
}
