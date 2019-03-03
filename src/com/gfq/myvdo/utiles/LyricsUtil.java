package com.gfq.myvdo.utiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.gfq.myvdo.domain.Lyric;

import android.util.Log;

/**
 * 解析歌词
 * 
 * @author Administrator
 *
 */
public class LyricsUtil {

	private ArrayList<Lyric> lyrics;
	private boolean isExistLyric = false;

	public boolean isExistLyric() {
		return isExistLyric;
	}

	/**
	 * 得到解析好的歌词列表
	 * 
	 * @return
	 */
	public ArrayList<Lyric> getLyrics() {
		return lyrics;
	}

	public void readLyricFile(File file) {
		if (file == null || !file.exists()) {
			// 歌词文件不存在
			lyrics = null;
			isExistLyric = false;
		} else {
			// 歌词文件存在
			lyrics = new ArrayList<>();
			isExistLyric = true;
			// 1.解析_一行一行的的读取，然后解析
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
				String line = "";
				while ((line = reader.readLine()) != null) {
					line = parseLyric(line);
				}
				reader.close();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}

			// 2.排序
			Collections.sort(lyrics, new Comparator<Lyric>() {
				@Override
				public int compare(Lyric lhs, Lyric rhs) {
					if (lhs.getTimePoint() < rhs.getTimePoint()) {
						return -1;
					} else if (lhs.getTimePoint() > rhs.getTimePoint()) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			// 3.计算每句的高亮显示时间
			for (int i = 0; i < lyrics.size(); i++) {
				Lyric pre = lyrics.get(i);
				if (i + 1 < lyrics.size()) {
					Lyric next = lyrics.get(i + 1);
					pre.setHighLightTime(next.getTimePoint() - pre.getTimePoint());
					
				}
			}

		}
	}

	/**
	 * 解析一句歌词
	 * 
	 * @param line
	 *            [01:02.34][05:06.78]歌詞內容
	 * @return
	 */
	private String parseLyric(String line) {
		int position1 = line.indexOf("[");// 有,返回0;没有,返回-1

		int position2 = line.indexOf("]");// 有,返回9;没有,返回-1

		if (position1 == 0 && position2 != -1) {// 肯定有一句歌词
			long[] times = new long[getCountTag(line)];
			String strTime = line.substring(position1 + 1, position2);// 截取字符串，得到01:02.34
			times[0] = strTime2LongTime(strTime);

			String content = line;
			int i = 1;
			while (position1 == 0 && position2 != -1) {
				content = content.substring(position2 + 1);// [05:06.78]歌詞內容
				position1 = content.indexOf("[");// 得到0-->-1
				position2 = content.indexOf("]");// 得到9-->-1
				
				if(position2!=-1) {
					strTime = content.substring(position1 + 1, position2);
					times[i] = strTime2LongTime(strTime);

					if (times[i] == -1) {
						return "";
					}
					i++;
				}
				
				
			}

			// 把时间数组和文本关联起来，并加入集合中
			Lyric ly = new Lyric();
			for (int j = 0; j < times.length; j++) {
				if (times[j] != 0) {
//					try {
//						byte[] b=content.getBytes("UTF-8");
//						String mContent=new String(b,"UTF-8");
//						ly.setContent(mContent);
//					} catch (UnsupportedEncodingException e) {
//						e.printStackTrace();
//					}
					ly.setContent(content);
					ly.setTimePoint(times[j]);
					Log.d("times[j]"+j, times[j]+"");
					lyrics.add(ly);
					ly = new Lyric();
				}
			}
			return content;
		}

		return "";
	}

	private long strTime2LongTime(String strTime) {
		long result = -1;
		try {
			// 1.按:切割成01和02.34
			String[] s1 = strTime.split(":");

			// 2.按.切割成02和34
			String[] s2 = s1[1].split("\\.");

			
			// 分钟
			long min = Long.parseLong(s1[0]);
			// 秒
			long second = Long.parseLong(s2[0]);
			// 毫秒
			long mm = Long.parseLong(s2[1]);

			result = min * 60 * 1000 + second * 1000 + mm * 10;
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		}
		return result;

	}

	/**
	 * 判断有多少句歌词
	 * 
	 * @param line
	 * @return
	 */
	private int getCountTag(String line) {
		int result = -1;
		String[] left = line.split("\\[");
		String[] right = line.split("\\]");

		if (left.length == 0 && right.length == 0) {
			result = 1;
		} else if (left.length > right.length) {
			result = left.length;
		} else {
			result = right.length;
		}
		return result;
	}
}
