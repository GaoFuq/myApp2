package com.gfq.myvdo.activity;

import com.gfq.myvdo.R;
import com.gfq.myvdo.service.IMusicPlayerService;
import com.gfq.myvdo.service.MusicPlayerService;
import com.gfq.myvdo.utiles.Utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MyMusicPlayer extends Activity implements OnClickListener {
	protected static final int PROGRESS = 0;// ���ȸ���
	private TextView tvMusicName;
	private TextView tvArtist;
	private TextView tvTime;
	private SeekBar seekbarMusic;
	private TextView tvTotalTime;
	private Button btnMusicPlaymode;
	private Button btnMusicPre;
	private Button btnMusicStartPause;
	private Button btnMusicNext;
	private Button btnMusicLyrics;
	private ImageView iv_icon;
	private int position;
	private Utils util = new Utils();
	private MyReceiver myReceiver;

	private IMusicPlayerService service;// ����Ĵ����࣬ͨ�������÷���ķ���

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PROGRESS:

				try {
					// 1.�õ���ǰ�Ľ���
					int currentPosition = service.getCurrentPostion();
					// 2.���ý���
					seekbarMusic.setProgress(currentPosition);
					// 3.ʱ���ı�����
					tvTime.setText(util.stringForTime(currentPosition));
					// 4.ÿ�����һ��
					handler.removeMessages(PROGRESS);
					handler.sendEmptyMessageDelayed(PROGRESS, 1000);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_music_player);

		registBroadcast();

		topAnimation();

		initView();

		getData();

		bind_startService();

	}

	// ע��㲥
	private void registBroadcast() {
		myReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MusicPlayerService.OPEN_MUSIC);
		registerReceiver(myReceiver, filter);
	}

	private void initView() {
		tvMusicName = (TextView) findViewById(R.id.tv_music_name);
		tvArtist = (TextView) findViewById(R.id.tv_artist);
		tvTime = (TextView) findViewById(R.id.tv_time);
		seekbarMusic = (SeekBar) findViewById(R.id.seekbar_music);
		tvTotalTime = (TextView) findViewById(R.id.tv_total_time);
		btnMusicPlaymode = (Button) findViewById(R.id.btn_music_playmode);
		btnMusicPre = (Button) findViewById(R.id.btn_music_pre);
		btnMusicStartPause = (Button) findViewById(R.id.btn_music_start_pause);
		btnMusicNext = (Button) findViewById(R.id.btn_music_next);
		btnMusicLyrics = (Button) findViewById(R.id.btn_music_lyrics);

		btnMusicPlaymode.setOnClickListener(this);
		btnMusicPre.setOnClickListener(this);
		btnMusicStartPause.setOnClickListener(this);
		btnMusicNext.setOnClickListener(this);
		btnMusicLyrics.setOnClickListener(this);

		seekbarMusic.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
	}

	private void bind_startService() {
		Intent in = new Intent(this, MusicPlayerService.class);
		in.setAction("musicplayer_OPENMUSIC");
		bindService(in, conn, Context.BIND_AUTO_CREATE);
		startService(in);// ֻʵ����һ������
	}

	private ServiceConnection conn = new ServiceConnection() {
		/**
		 * �����ӳɹ�ʱ���ص��÷���
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder iBinder) {
			service = IMusicPlayerService.Stub.asInterface(iBinder);
			if (service != null) {
				try {
					if (!isFromNotify) {// �����ڲ����б�
						service.openMusic(position);
						checkPlayMode();
					} else {
						// ������״̬��
						showMusicPlayerViewData();
					}

				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * ���Ͽ�����ʱ���ص��÷���. ȡ���󶨲���ص�
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			try {
				if (service != null) {
					service.stop();
					service = null;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	};
	private boolean isFromNotify = false;

	private void getData() {
		isFromNotify = getIntent().getBooleanExtra("Notification", false);

		if (!isFromNotify) {
			position = getIntent().getIntExtra("position", 0);
		}
	}

	private void topAnimation() {
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		iv_icon.setBackgroundResource(R.drawable.frame_anim);
		AnimationDrawable animationDrawable = (AnimationDrawable) iv_icon.getBackground();
		animationDrawable.start();
	}

	@Override
	public void onClick(View v) {
		if (v == btnMusicPlaymode) {
			setPlayMode();
		} else if (v == btnMusicPre) {
			if (service != null) {
				try {
					service.pre();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		} else if (v == btnMusicStartPause) {
			if (service != null) {
				try {
					if (service.isPlaying()) {
						service.pause();
						btnMusicStartPause.setBackgroundResource(R.drawable.iv_start__selector);
					} else {
						service.start();
						btnMusicStartPause.setBackgroundResource(R.drawable.btn_pause_selector);

					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		} else if (v == btnMusicNext) {
			if (service != null) {
				try {
					service.next();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		} else if (v == btnMusicLyrics) {
		}
	}
	
	

	private void showPlayMode() {
		try {
			int playMode = service.getPlayMode();
			if (playMode == MusicPlayerService.REPEAT_NORMAL) {
				btnMusicPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
				Toast.makeText(this, "˳�򲥷�", Toast.LENGTH_SHORT).show();
			} else if (playMode == MusicPlayerService.REPEAT_SINGLE) {
				btnMusicPlaymode.setBackgroundResource(R.drawable.btn_playmode_single_selector);
				Toast.makeText(this, "����Сѭ��", Toast.LENGTH_SHORT).show();
			} else if (playMode == MusicPlayerService.REPEAT_ALL) {
				btnMusicPlaymode.setBackgroundResource(R.drawable.btn_playmode_all_selector);
				Toast.makeText(this, "ȫ���󲥷�", Toast.LENGTH_SHORT).show();
			} else {
				btnMusicPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
				Toast.makeText(this, "˳�򲥷�", Toast.LENGTH_SHORT).show();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void setPlayMode() {
		
		try {
			int playMode = service.getPlayMode();

			if (playMode == MusicPlayerService.REPEAT_NORMAL) {
				playMode = MusicPlayerService.REPEAT_SINGLE;
			} else if (playMode == MusicPlayerService.REPEAT_SINGLE) {
				playMode = MusicPlayerService.REPEAT_ALL;
			} else if (playMode == MusicPlayerService.REPEAT_ALL) {
				playMode = MusicPlayerService.REPEAT_NORMAL;
			} else {
				playMode = MusicPlayerService.REPEAT_NORMAL;
			}
			service.setPlayMode(playMode);
			showPlayMode();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private void checkPlayMode() {
		try {
			int playMode = service.getPlayMode();
			if (playMode == MusicPlayerService.REPEAT_NORMAL) {
				btnMusicPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
			} else if (playMode == MusicPlayerService.REPEAT_SINGLE) {
				btnMusicPlaymode.setBackgroundResource(R.drawable.btn_playmode_single_selector);
			} else if (playMode == MusicPlayerService.REPEAT_ALL) {
				btnMusicPlaymode.setBackgroundResource(R.drawable.btn_playmode_all_selector);
			} else {
				btnMusicPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
			}
			service.setPlayMode(playMode);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ȡ��ע��Ĺ㲥
		if (myReceiver != null) {
			unregisterReceiver(myReceiver);
			myReceiver = null;
		}

		// �Ƴ�������Ϣ
		handler.removeCallbacksAndMessages(null);

	}

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			showMusicPlayerViewData();
			checkPlayMode();
		}

	}

	public void showMusicPlayerViewData() {
		try {
			tvMusicName.setText(service.getMusicName());
			tvArtist.setText(service.getArtist());
			tvTotalTime.setText(util.stringForTime(service.getDuration()));
			seekbarMusic.setMax(service.getDuration());
			// ����Ϣ������seekBar
			handler.sendEmptyMessage(PROGRESS);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	class MyOnSeekBarChangeListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (fromUser) {
				try {
					service.seekTo(progress);

				} catch (RemoteException e) {
					e.printStackTrace();
				}

			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}

	}
}
