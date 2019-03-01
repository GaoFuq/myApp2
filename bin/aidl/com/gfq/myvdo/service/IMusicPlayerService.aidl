package com.gfq.myvdo.service;
interface IMusicPlayerService{
/**
	 * 根据位置打开对应的music
	 * @param position
	 */
	void openMusic(int position);
	/**
	 * 播放
	 */
	void start() ;
	/**
	 * 暂停
	 */
	void pause() ;
	/**
	 * 停止
	 */
	void stop() ;
	/**
	 * 得到当前播放进度
	 */
	int getCurrentPostion() ;
	
	/**
	 * 得到总时长
	 */
	int getDuration() ;
	
	/**
	 * 得到演唱者
	 */
	String getArtist();
	
	/**
	 * 得到歌曲名
	 */
	String getMusicName();
	
	/**
	 * 得到音乐路径
	 * @return
	 */
	String getMusicPath();
	/**
	 * 下一曲
	 */
	void next();
	/**
	 * 上一曲
	 */
	 void pre();
	
	/**
	 * 设置播放模式
	 */
	void setPlayMode(int mode);
	
	/**
	 * 得到播放模式
	 */
	 int getPlayMode();
	 
	 /**
	  * 是否正在播放
	  */
	 boolean isPlaying();
	 
	 /**
	  * 拖动音乐
	  */
	 void seekTo(int position);
}