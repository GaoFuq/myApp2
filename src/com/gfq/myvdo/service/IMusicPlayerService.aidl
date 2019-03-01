package com.gfq.myvdo.service;
interface IMusicPlayerService{
/**
	 * ����λ�ô򿪶�Ӧ��music
	 * @param position
	 */
	void openMusic(int position);
	/**
	 * ����
	 */
	void start() ;
	/**
	 * ��ͣ
	 */
	void pause() ;
	/**
	 * ֹͣ
	 */
	void stop() ;
	/**
	 * �õ���ǰ���Ž���
	 */
	int getCurrentPostion() ;
	
	/**
	 * �õ���ʱ��
	 */
	int getDuration() ;
	
	/**
	 * �õ��ݳ���
	 */
	String getArtist();
	
	/**
	 * �õ�������
	 */
	String getMusicName();
	
	/**
	 * �õ�����·��
	 * @return
	 */
	String getMusicPath();
	/**
	 * ��һ��
	 */
	void next();
	/**
	 * ��һ��
	 */
	 void pre();
	
	/**
	 * ���ò���ģʽ
	 */
	void setPlayMode(int mode);
	
	/**
	 * �õ�����ģʽ
	 */
	 int getPlayMode();
	 
	 /**
	  * �Ƿ����ڲ���
	  */
	 boolean isPlaying();
	 
	 /**
	  * �϶�����
	  */
	 void seekTo(int position);
}