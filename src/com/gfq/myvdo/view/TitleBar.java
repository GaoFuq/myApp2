package com.gfq.myvdo.view;

import com.gfq.myvdo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
//�Զ��������
import android.widget.TextView;
import android.widget.Toast;

public class TitleBar extends LinearLayout implements View.OnClickListener {

	private View tv_search;
	private View rl_game;
	private View iv_history;

	// �ڴ�����ʵ���������ʱ������������
	public TitleBar(Context context) {
		this(context, null);
		// TODO �Զ����ɵĹ��캯�����
	}

	// ���ڲ����ļ���ʹ�ø���ʱ��Androidϵͳ�����������ʵ��������
	public TitleBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO �Զ����ɵĹ��캯�����
	}

	// ����Ҫ������ʽ��ʱ��ʹ�ø÷���ʵ��������
	public TitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO �Զ����ɵĹ��캯�����
	}

	// �������ļ��������ʱ���ص��÷���.
	// ���ݿؼ�Ԫ�ص�λ������ȡ��������Ŀؼ�Ԫ��
	//���±��0��ʼ��
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		tv_search = getChildAt(1);
		rl_game = getChildAt(2);
		iv_history = getChildAt(3);

		// ���õ���¼�
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
