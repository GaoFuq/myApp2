package com.gfq.myvdo.domain;

public class Lyric {
	/**
	 * 歌词内容
	 */
	private String content;
	
	/**
	 * 时间戳
	 */
	private long timePoint;
	
	/**
	 * 歌词高亮显示时间
	 */
	private long highLightTime;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTimePoint() {
		return timePoint;
	}

	public void setTimePoint(long timePoint) {
		this.timePoint = timePoint;
	}

	public long getHighLightTime() {
		return highLightTime;
	}

	public void setHighLightTime(long highLightTime) {
		this.highLightTime = highLightTime;
	}

	@Override
	public String toString() {
		return "Lyric [content=" + content + ", timePoint=" + timePoint + ", highLightTime=" + highLightTime + "]";
	}
	
	
}
