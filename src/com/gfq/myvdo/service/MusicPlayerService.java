package com.gfq.myvdo.service;

import java.util.ArrayList;

import com.gfq.myvdo.R;
import com.gfq.myvdo.activity.MyMusicPlayer;
import com.gfq.myvdo.activity.MyVedioPlayer;
import com.gfq.myvdo.domain.MediaItem;
import com.gfq.myvdo.service.IMusicPlayerService.Stub;
import com.gfq.myvdo.utiles.CacheUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

public class MusicPlayerService extends Service {

	public static final String OPEN_MUSIC = "musicplayer_OPENMUSIC";
	private ArrayList<MediaItem> items;
	private MediaItem item;
	private MediaPlayer mediaPlayer;// ������������
	public static final int REPEAT_NORMAL = 1;// ˳��ѭ��
	public static final int REPEAT_SINGLE = 2;// ����ѭ��
	public static final int REPEAT_ALL = 3;// �б�ѭ��
	private int playMode = REPEAT_NORMAL;

	@Override
	public void onCreate() {
		super.onCreate();
		playMode = CacheUtil.getInt(this, "playMode");
		// ���������б�
		getDataFromLocal();
	}

	private void getDataFromLocal() {
		new Thread() {
			public void run() {
				super.run();
				items = new ArrayList<>();
				ContentResolver rs = getContentResolver();
				Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				String[] projection = { MediaStore.Audio.Media.DISPLAY_NAME, //
						MediaStore.Audio.Media.DURATION, //
						MediaStore.Audio.Media.SIZE, //
						MediaStore.Audio.Media.DATA, // ���ŵ�ַ
						MediaStore.Audio.Media.ARTIST,//
				};
				Cursor cursor = rs.query(uri, projection, null, null, null);
				if (cursor != null) {
					while (cursor.moveToNext()) {
						MediaItem item = new MediaItem();
						items.add(item);
						String name = cursor.getString(0);
						long duration = cursor.getLong(1);
						long size = cursor.getLong(2);
						String data = cursor.getString(3);
						String artist = cursor.getString(4);

						item.setName(name);
						item.setDuration(duration);
						item.setSize(size);
						item.setData(data);
						item.setArtist(artist);
					}
					cursor.close();
				}

			};
		}.start();

	}

	private Stub stub = new Stub() {
		MusicPlayerService service = MusicPlayerService.this;

		@Override
		public void stop() throws RemoteException {
			service.stop();
		}

		@Override
		public void start() throws RemoteException {
			service.start();
		}

		@Override
		public void setPlayMode(int mode) throws RemoteException {
			service.setPlayMode(mode);
		}

		@Override
		public void pre() throws RemoteException {
			service.pre();
		}

		@Override
		public void pause() throws RemoteException {
			service.pause();
		}

		@Override
		public void openMusic(int position) throws RemoteException {
			service.openMusic(position);
		}

		@Override
		public void next() throws RemoteException {
			service.next();
		}

		@Override
		public int getPlayMode() throws RemoteException {
			return service.getPlayMode();
		}

		@Override
		public String getMusicPath() throws RemoteException {
			return service.getMusicPath();
		}

		@Override
		public String getMusicName() throws RemoteException {
			return service.getMusicName();
		}

		@Override
		public int getDuration() throws RemoteException {
			return service.getDuration();
		}

		@Override
		public int getCurrentPostion() throws RemoteException {
			return service.getCurrentPostion();
		}

		@Override
		public String getArtist() throws RemoteException {
			return service.getArtist();
		}

		@Override
		public boolean isPlaying() throws RemoteException {
			return service.isPlaying();

		}

		@Override
		public void seekTo(int position) throws RemoteException {
			service.seekTo(position);
		}
	};
	private int position;
	private boolean isSamePosition = false;

	@Override
	public IBinder onBind(Intent intent) {
		return stub;
	}

	/**
	 * ����λ�ô򿪶�Ӧ��music
	 * 
	 * @param position
	 */
	private void openMusic(int p) {
		position = p;
		if (items != null && items.size() > 0) {
			item = items.get(p);

			if (mediaPlayer != null) {
				// mediaPlayer.release();//�ͷ���Դ
				mediaPlayer.reset();// ����

			}

			try {
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setDataSource(item.getData());
				// ���ü���
				mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
				mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
				mediaPlayer.setOnErrorListener(new MyOnErrorListener());
				mediaPlayer.prepareAsync();//

				if (playMode == MusicPlayerService.REPEAT_SINGLE) {
					// ����ѭ�����Ͳ�����������ɵĻص�
					mediaPlayer.setLooping(true);
				} else {
					mediaPlayer.setLooping(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			Toast.makeText(this, "not found music", Toast.LENGTH_SHORT).show();
		}

	}

	private NotificationManager manager;

	/**
	 * ����
	 */
	private void start() {
		mediaPlayer.start();
		// ���������ڲ���ʱ����״̬����ʾ���ڲ��ŵĸ��������ҵ���������ת������������
		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent intent = new Intent(this, MyMusicPlayer.class);
		intent.putExtra("Notification", true);// ��ʶ�����ͼ������Notification
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		;
		Notification notification = new Notification.Builder(this).setSmallIcon(R.drawable.notification_music_playing)
				.setContentTitle("���ֲ�����").setContentText("���ڲ���:" + getMusicName()).setContentIntent(pendingIntent)
				.build();
		manager.notify(1, notification);
	}

	/**
	 * ��ͣ
	 */
	private void pause() {
		mediaPlayer.pause();
		manager.cancel(1);
	}

	/**
	 * ֹͣ
	 */
	private void stop() {
		mediaPlayer.stop();
	}

	/**
	 * �õ���ǰ���Ž���
	 */
	private int getCurrentPostion() {
		return mediaPlayer.getCurrentPosition();

	}

	/**
	 * �õ���ʱ��
	 */
	private int getDuration() {
		// return (int) item.getDuration();
		return mediaPlayer.getDuration();
	}

	/**
	 * �õ��ݳ���
	 */
	private String getArtist() {
		return item.getArtist();

	}

	/**
	 * �õ�������
	 */
	private String getMusicName() {
		return item.getName();

	}

	/**
	 * �õ�����·��
	 * 
	 * @return
	 */
	private String getMusicPath() {
		return item.getData();

	}

	/**
	 * ��һ��
	 */
	private void next() {
		// 1.���ݵ�ǰ�Ĳ���ģʽ��������һ����λ��
		setNextPosition();
		// 2.���ݵ�ǰ�Ĳ���ģʽ��λ�ã�ȥ���Ÿ���
		openNextMusic();
	}

	private void openNextMusic() {
		int playMode = getPlayMode();
		if (playMode == MusicPlayerService.REPEAT_NORMAL) {
			if (position < items.size()) {
				openMusic(position);
			} else {
				position = items.size() - 1;
			}
		} else if (playMode == MusicPlayerService.REPEAT_SINGLE) {
			openMusic(position);
		} else if (playMode == MusicPlayerService.REPEAT_ALL) {
			openMusic(position);
		} else {
			if (position < items.size()) {
				openMusic(position);
			} else {
				position = items.size() - 1;
			}
		}
	}

	private void setNextPosition() {
		int playMode = getPlayMode();
		if (playMode == MusicPlayerService.REPEAT_NORMAL) {
			position++;
		} else if (playMode == MusicPlayerService.REPEAT_SINGLE) {
			position++;
			if (position >= items.size()) {
				position = 0;
			}
		} else if (playMode == MusicPlayerService.REPEAT_ALL) {
			position++;
			if (position >= items.size()) {
				position = 0;
			}
		} else {
			position++;
		}
	}

	/**
	 * ��һ��
	 */
	private void pre() {
		// 1.���ݵ�ǰ�Ĳ���ģʽ��������һ����λ��
		setPrePosition();
		// 2.���ݵ�ǰ�Ĳ���ģʽ��λ�ã�ȥ���Ÿ���
		openPreMusic();
	}

	private void openPreMusic() {
		int playMode = getPlayMode();
		if (playMode == MusicPlayerService.REPEAT_NORMAL) {
			if (position >=0) {
				openMusic(position);
			} else {
				position =0;
			}
		} else if (playMode == MusicPlayerService.REPEAT_SINGLE) {
			openMusic(position);
		} else if (playMode == MusicPlayerService.REPEAT_ALL) {
			openMusic(position);
		} else {
			if (position >=0) {
				openMusic(position);
			} else {
				position =0;
			}
		}		
	}

	private void setPrePosition() {
		int playMode = getPlayMode();
		if (playMode == MusicPlayerService.REPEAT_NORMAL) {
			position--;
		} else if (playMode == MusicPlayerService.REPEAT_SINGLE) {
			position--;
			if (position < 0) {
				position = items.size()-1;
			}
		} else if (playMode == MusicPlayerService.REPEAT_ALL) {
			position--;
			if (position < 0) {
				position = items.size()-1;
			}
		} else {
			position--;
		}
	}

	/**
	 * ���ò���ģʽ
	 */
	private void setPlayMode(int mode) {
		playMode = mode;
		CacheUtil.putInt(this, "playMode", mode);
		if (playMode == MusicPlayerService.REPEAT_SINGLE) {
			// ����ѭ�����Ͳ�����������ɵĻص�
			mediaPlayer.setLooping(true);
		} else {
			mediaPlayer.setLooping(false);
		}
	}

	/**
	 * �õ�����ģʽ
	 */
	private int getPlayMode() {
		return playMode;

	}

	private boolean isPlaying() {
		return mediaPlayer.isPlaying();

	}

	private void seekTo(int position) {
		mediaPlayer.seekTo(position);
	}

	class MyOnPreparedListener implements OnPreparedListener {

		@Override
		public void onPrepared(MediaPlayer mp) {
			// ֪ͨActivity��ȡ��Ϣ--�㲥
			notifyChange(OPEN_MUSIC);
			start();
		}

	}

	class MyOnCompletionListener implements OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
			next();
		}

	}

	class MyOnErrorListener implements OnErrorListener {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			next();
			return true;
		}

	}

	/**
	 * ���ݶ������㲥
	 * 
	 * @param action
	 */
	private void notifyChange(String action) {
		Intent in = new Intent(action);
		sendBroadcast(in);
	}
}
