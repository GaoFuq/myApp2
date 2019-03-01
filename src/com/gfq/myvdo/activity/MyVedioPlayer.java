package com.gfq.myvdo.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.gfq.myvdo.R;
import com.gfq.myvdo.domain.MediaItem;
import com.gfq.myvdo.utiles.Utils;
import com.gfq.myvdo.view.VideoView;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MyVedioPlayer extends Activity implements OnClickListener {

	private static final int DEFALT = 0;
	private static final int FULL = 1;
	private static final int SHOW_NET_SPEED = 7;
	private final int HIDE_VIDEO_CONTROL = 3;
	private final int PLAY_NEXT_VIDEO = 0;
	private final int PROGRESS = 1;// ��Ƶ����
	private VideoView videoView;
	private Uri uri;
	private TextView tvName;
	private RelativeLayout videoControl;
	private ImageView btnVoice;
	private SeekBar seekbarVoice;
	private TextView tvCurrentTime;
	private SeekBar seekbarVideo;
	private TextView tvDuration;
	private TextView tv_netSpeed;
	private LinearLayout ll_buffer;
	private Button btnExit;
	private Button btnVideoStartPause;
	private Button btnVideoNext;
	private Button btnVideoSiwchScreen;
	private int position;
	private Utils util = new Utils();
	private ArrayList<MediaItem> items;

	private float startY;
	private float endY;
	private float rangY;
	private int mVol;
	private DisplayMetrics m;
	private int screenH;
	private int screenW;
	private final int HIDE_VOICE_SEEKBAR = 5;
	private final int UPDATE_VOICE = 6;
	private int voice;
	private GestureDetector detector;
	private AudioManager audioManage;
	private int maxVolume;
	private int currentVolume;
	private LinearLayout ll_voice;
	private boolean isFullScreen;
	protected boolean isNetUri;
	public int videoW;
	public int videoH;
	private boolean isUseSystem;
	protected int precurrentPosition;
	private TextView tv_systemTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		;

		setContentView(R.layout.my_video_player);

		// // ����ǹ���Ȩ�޵ģ�һ��Ҫ��ӣ���ȻҲ�ᱨ�쳣
		// NotificationManager mNotificationManager =
		// (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
		// !mNotificationManager.isNotificationPolicyAccessGranted()) {
		// Intent intent_v = new
		// Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
		// startActivity(intent_v);
		// }

		videoView = (VideoView) findViewById(R.id.video_view);
		findViews();

		position = getIntent().getIntExtra("position", 0);

		// �õ����ŵĵ�ַ
		getVideoUriFromIntent();

		setListener();

		setData2VideoControl();

		/**
		 * ��������
		 */

		// ����ϵͳ�Դ��Ŀ������
		// videoView.setMediaController(new MediaController(this));

	}

	private void setData2VideoControl() {
		tvName.setText(items.get(position).getName());
		tvDuration.setText(util.stringForTime((int) items.get(position).getDuration()));
	}

	private void getVideoUriFromIntent() {
		items = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videoList");
		uri = getIntent().getData();//
		if (items.size() > 0 && items != null) {
			videoView.setVideoPath(items.get(position).getData());
		} else if (uri != null) {
			videoView.setVideoURI(uri);
		} else {
			Toast.makeText(this, "...", Toast.LENGTH_SHORT).show();
		}

	}

	private void findViews() {
		changeScreenWorH();
		audioManage = (AudioManager) this.getSystemService(AUDIO_SERVICE);
		// ��ȡϵͳ�������
		maxVolume = audioManage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		// ��ȡ��ǰֵ
		currentVolume = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);
		tv_systemTime=(TextView)findViewById(R.id.tv_system_time);
		ll_buffer=(LinearLayout) findViewById(R.id.ll_buffer);
		tv_netSpeed=(TextView) findViewById(R.id.tv_netSpeed);
		ll_voice = (LinearLayout) findViewById(R.id.ll_voice);
		videoControl = (RelativeLayout) findViewById(R.id.video_control);
		tvName = (TextView) findViewById(R.id.tv_name);
		seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
		tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
		seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
		tvDuration = (TextView) findViewById(R.id.tv_duration);
		btnExit = (Button) findViewById(R.id.btn_exit);
		btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
		btnVideoNext = (Button) findViewById(R.id.btn_video_next);
		btnVideoSiwchScreen = (Button) findViewById(R.id.btn_video_siwch_screen);

		seekbarVoice.setMax(maxVolume);
		seekbarVoice.setProgress(currentVolume);

		btnExit.setOnClickListener(this);
		btnVideoStartPause.setOnClickListener(this);
		btnVideoNext.setOnClickListener(this);
		btnVideoSiwchScreen.setOnClickListener(this);
		
		handel.sendEmptyMessage(SHOW_NET_SPEED);

		detector = new GestureDetector(this, new SimpleOnGestureListener() {

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				// ˫�����Ż�����ͣ
				videoStatus();
				return super.onDoubleTap(e);
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				ll_voice.setVisibility(View.GONE);
				if (videoControl.isShown()) {
					videoControl.setVisibility(View.GONE);
					handel.removeMessages(HIDE_VIDEO_CONTROL);
				} else {
					videoControl.setVisibility(View.VISIBLE);
					handel.sendEmptyMessageDelayed(HIDE_VIDEO_CONTROL, 5000);
				}

				return super.onSingleTapConfirmed(e);
			}

		});

	}

	private void changeScreenWorH() {
		m = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(m);
		screenH = m.heightPixels;
		screenW = m.widthPixels;
	}

	/**
	 * Handle button click events<br />
	 * <br />
	 * Auto-created on 2019-01-13 13:36:07 by Android Layout Finder
	 * (http://www.buzzingandroid.com/tools/android-layout-finder)
	 */
	/**
	 * 
	 * 1.���벥��ҳ�棬���Ű�ť��ʼΪ����״̬������������ء�onPrepared
	 * 2.�����Ļ��������ʾ������塣5����Զ����أ�handler����Ϣ����UI���� 3.���»�����Ļ����������������ʶ������
	 * -a.����GestureDetector -b.ʵ����new GestureDetector(context,new
	 * SimpleOnGestureListener()),��д������ -c.���¼����ݸ�����ʶ���� detector.onTouchEvent(event);
	 */
	// -----------------------------------------------------------------------------
	@Override
	public void onClick(View v) {
		if (v == btnVoice) {

		} else if (v == btnExit) {
			finish();
		} else if (v == btnVideoStartPause) {
			videoStatus();

		} else if (v == btnVideoNext) {

			playNextvideo();

		} else if (v == btnVideoSiwchScreen) {
			switchScreen();
		}
		handel.removeMessages(HIDE_VIDEO_CONTROL);
		handel.sendEmptyMessageDelayed(HIDE_VIDEO_CONTROL, 5000);
	}

	private void switchScreen() {
		if (isFullScreen) {
			setVideoScreenType(DEFALT);
		} else {
			setVideoScreenType(FULL);
		}
	}

	private void setVideoScreenType(int type) {
		switch (type) {
		case DEFALT:
			// ��Ƶ��ʵ���
			int mVideoWidth = videoW;
			int mVideoHeight = videoH;
			// ��Ļ�Ŀ��
			int height = screenH;
			int width = screenW;
			// for compatibility, we adjust size based on aspect ratio
			if (mVideoWidth * height < width * mVideoHeight) {
				// Log.i("@@@", "image too wide, correcting");
				width = height * mVideoWidth / mVideoHeight;
			} else if (mVideoWidth * height > width * mVideoHeight) {
				// Log.i("@@@", "image too tall, correcting");
				height = width * mVideoHeight / mVideoWidth;
			}
			videoView.setVideoSize(width, height);
			btnVideoSiwchScreen.setBackgroundResource(R.drawable.iv_full_screen_selector);
			isFullScreen = false;
			break;

		case FULL:
			videoView.setVideoSize(screenW, screenH);
			btnVideoSiwchScreen.setBackgroundResource(R.drawable.iv_default_screen_selector);
			isFullScreen = true;
			break;

		default:
			break;
		}
	}
	
	/**
	 * ������Ƶ�Ĳ���״̬���ı䲥�Ű�ť��״̬
	 */
	private void videoStatus() {
		if (videoView.isPlaying()) {
			videoView.pause();
			btnVideoStartPause.setBackgroundResource(R.drawable.iv_start__selector);
		} else {
			videoView.start();
			btnVideoStartPause.setBackgroundResource(R.drawable.btn_pause_selector);

		}
	}

	private void playNextvideo() {
		position++;
		if (position < items.size()) {
			setData2VideoControl();
			videoView.setVideoPath(items.get(position).getData());
			setButtonStatus();
		} else if (uri != null) {
			tvName.setText(uri.toString());
		} else {
			Toast.makeText(MyVedioPlayer.this, "�Ѿ������һ����", Toast.LENGTH_SHORT).show();

		}
	}

	private void setButtonStatus() {
		if (items.size() == 1) {
			btnVideoNext.setEnabled(false);
		} else if (items.size() > 1 && position == items.size() - 1) {
			btnVideoNext.setEnabled(false);
		}
	}

	private Handler handel = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PROGRESS:
				// 1.�õ���ǰ����
				int currentPosition = videoView.getCurrentPosition();
				// 2.���ø�ProgressBar
				seekbarVideo.setProgress(currentPosition);
				// �����ı��Ľ���
				tvCurrentTime.setText(util.stringForTime(currentPosition));
				//����ϵͳʱ��
				tv_systemTime.setText(util.getSystemTime());
				
				//������ȸ���
				if(isNetUri) {
					int buffer=videoView.getBufferPercentage();//0`100
					int totaoBuffer=buffer*seekbarVideo.getMax();
					int secondaryProgress=totaoBuffer/100;
					seekbarVideo.setSecondaryProgress(secondaryProgress);
				}
				if(!isUseSystem) {
					if(videoView.isPlaying()) {
						int buffer =currentPosition-precurrentPosition;
						if(buffer<500) {
							ll_buffer.setVisibility(View.VISIBLE);
						}else {
							ll_buffer.setVisibility(View.GONE);
						}
					}
				}
				precurrentPosition=currentPosition;
				// 3.ÿ�����һ��
				removeMessages(PROGRESS);
				sendEmptyMessageDelayed(PROGRESS, 1000);
				break;
			case PLAY_NEXT_VIDEO:
				playNextvideo();
				break;

			case HIDE_VIDEO_CONTROL:
				hideVideoControl();
				break;

			case HIDE_VOICE_SEEKBAR:
				ll_voice.setVisibility(View.GONE);
				break;
			case UPDATE_VOICE:
				updateVoice(voice);
				break;
				
			case SHOW_NET_SPEED:
				String netSpeed=util.getNetSpeed(MyVedioPlayer.this);
				tv_netSpeed.setText("������..."+netSpeed);
				//ÿ2�����һ��
				removeMessages(SHOW_NET_SPEED);
				sendEmptyMessageDelayed(SHOW_NET_SPEED, 2000);
				break;
			default:
				break;
			}
		};
	};


	private void setListener() {
		// �����Ƿ�׼����
		videoView.setOnPreparedListener(new MyOnPreparedListener());
		// �����Ƿ񲥷���
		videoView.setOnCompletionListener(new MyCompletionListener());

		// �����Ƿ����
		videoView.setOnErrorListener(new MyOnErrorListener());
		//������Ƶ��--ϵͳapi
		if(isUseSystem) {
			if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1) {
				videoView.setOnInfoListener(new MyOnInfoListener());
			}
		}
		seekbarVideo.setOnSeekBarChangeListener(new MyVideoSeekBarChangeListener());
	}

	

	protected void updateVoice(int p) {
		audioManage.setStreamVolume(AudioManager.STREAM_MUSIC, p, 0);
		seekbarVoice.setProgress(p);
		currentVolume = p;
	}

	protected void hideVideoControl() {
		videoControl.setVisibility(View.GONE);
	}

class MyOnInfoListener implements OnInfoListener{

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			ll_buffer.setVisibility(View.VISIBLE);
			break;
			
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			ll_buffer.setVisibility(View.GONE);
			break;

		default:
			break;
		}
		return true;
	}
	
}
	class MyVideoSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

		@Override // ��ָ�϶�ʱ�ص��÷���
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (fromUser) {
				videoView.seekTo(progress);
				handel.removeMessages(HIDE_VIDEO_CONTROL);
			}
		}

		@Override // ��ָ����ʱ�ص��÷���
		public void onStartTrackingTouch(SeekBar seekBar) {
			handel.removeMessages(HIDE_VIDEO_CONTROL);

		}

		@Override // ��ָ�뿪ʱ�ص��÷���
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO �Զ����ɵķ������
			handel.sendEmptyMessageDelayed(HIDE_VIDEO_CONTROL, 5000);
		}

	}

	class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

		@Override
		public void onPrepared(MediaPlayer mp) {
			videoView.start();
			// �õ���Ƶ����ʵ���
			videoW = mp.getVideoWidth();
			videoH = mp.getVideoHeight();

			setVideoScreenType(DEFALT);
			// ���ý����ʼ��ʾ
			btnVideoStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
			videoControl.setVisibility(View.GONE);
			int totalTime = mp.getDuration();
			tvDuration.setText(util.stringForTime(totalTime));
			seekbarVideo.setMax(totalTime);
			Message msg = new Message();
			msg.what = 1;
			handel.sendMessage(msg);
		}

	}

	class MyOnErrorListener implements MediaPlayer.OnErrorListener {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			// TODO �Զ����ɵķ������
			return false;
		}

	}

	class MyCompletionListener implements MediaPlayer.OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
			Toast.makeText(MyVedioPlayer.this, "3��󲥷���һ����Ƶ", Toast.LENGTH_SHORT).show();
			handel.sendEmptyMessageDelayed(PLAY_NEXT_VIDEO, 3000);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startY = event.getY();
			mVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);
			rangY = Math.min(screenH, screenW);
			handel.removeMessages(HIDE_VIDEO_CONTROL);

			break;

		case MotionEvent.ACTION_MOVE:
			handel.removeMessages(HIDE_VOICE_SEEKBAR);
			endY = event.getY();
			float distanceY = startY - endY;
			float dt = (distanceY / rangY) * maxVolume;
			voice = (int) Math.min(Math.max(mVol + dt, 0), maxVolume);
			if (dt != 0) {
				handel.sendEmptyMessage(UPDATE_VOICE);
				ll_voice.setVisibility(View.VISIBLE);
			}else{
				ll_voice.setVisibility(View.GONE);
			}
			break;

		case MotionEvent.ACTION_UP:
			handel.sendEmptyMessageDelayed(HIDE_VOICE_SEEKBAR, 1500);
			handel.sendEmptyMessageDelayed(HIDE_VIDEO_CONTROL, 5000);
			break;
		}

		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		handel.removeCallbacksAndMessages(null);
	}
}
