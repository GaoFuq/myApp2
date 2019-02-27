package com.gfq.myvdo.view;

import com.gfq.myvdo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
//自定义标题栏
import android.widget.TextView;
import android.widget.Toast;

public class TitleBar extends LinearLayout implements View.OnClickListener {

	private View tv_search;
	private View rl_game;
	private View iv_history;

	// 在代码中实例化该类的时候调用这个方法
	public TitleBar(Context context) {
		this(context, null);
		// TODO 自动生成的构造函数存根
	}

	// 当在布局文件中使用该类时，Android系统调用这个方法实例化该类
	public TitleBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO 自动生成的构造函数存根
	}

	// 当需要设置样式的时候，使用该方法实例化该类
	public TitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO 自动生成的构造函数存根
	}

	// 当布局文件加载完成时，回调该方法.
	// 根据控件元素的位置来获取布局里面的控件元素
	//下下标从0开始。
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		tv_search = getChildAt(1);
		rl_game = getChildAt(2);
		iv_history = getChildAt(3);

		// 设置点击事件
		tv_search.setOnClickListener(this);
		rl_game.setOnClickListener(this);
		iv_history.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_search:
			Toast.makeText(getContext(), "search", Toast.LENGTH_SHORT).show();
			break;
		case R.id.rl_game:
			Toast.makeText(getContext(), "game", Toast.LENGTH_SHORT).show();

			break;
		case R.id.iv_history:
			Toast.makeText(getContext(), "history", Toast.LENGTH_SHORT).show();

			break;

		default:
			break;
		}
	}
}
