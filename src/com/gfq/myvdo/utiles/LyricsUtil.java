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
 * �������
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
	 * �õ������õĸ���б�
	 * 
	 * @return
	 */
	public ArrayList<Lyric> getLyrics() {
		return lyrics;
	}

	public void readLyricFile(File file) {
		if (file == null || !file.exists()) {
			// ����ļ�������
			lyrics = null;
			isExistLyric = false;
		} else {
			// ����ļ�����
			lyrics = new ArrayList<>();
			isExistLyric = true;
			// 1.����_һ��һ�еĵĶ�ȡ��Ȼ�����
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
				String line = "";
				while ((line = reader.readLine()) != null) {
					line = parseLyric(line);
				}
				reader.close();
			} catch (Exception e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}

			// 2.����
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
			// 3.����ÿ��ĸ�����ʾʱ��
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
	 * ����һ����
	 * 
	 * @param line
	 *            [01:02.34][05:06.78]���~����
	 * @return
	 */
	private String parseLyric(String line) {
		int position1 = line.indexOf("[");// ��,����0;û��,����-1

		int position2 = line.indexOf("]");// ��,����9;û��,����-1

		if (position1 == 0 && position2 != -1) {// �϶���һ����
			long[] times = new long[getCountTag(line)];
			String strTime = line.substring(position1 + 1, position2);// ��ȡ�ַ������õ�01:02.34
			times[0] = strTime2LongTime(strTime);

			String content = line;
			int i = 1;
			while (position1 == 0 && position2 != -1) {
				content = content.substring(position2 + 1);// [05:06.78]���~����
				position1 = content.indexOf("[");// �õ�0-->-1
				position2 = content.indexOf("]");// �õ�9-->-1
				
				if(position2!=-1) {
					strTime = content.substring(position1 + 1, position2);
					times[i] = strTime2LongTime(strTime);

					if (times[i] == -1) {
						return "";
					}
					i++;
				}
				
				
			}

			// ��ʱ��������ı����������������뼯����
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
			// 1.��:�и��01��02.34
			String[] s1 = strTime.split(":");

			// 2.��.�и��02��34
			String[] s2 = s1[1].split("\\.");

			
			// ����
			long min = Long.parseLong(s1[0]);
			// ��
			long second = Long.parseLong(s2[0]);
			// ����
			long mm = Long.parseLong(s2[1]);

			result = min * 60 * 1000 + second * 1000 + mm * 10;
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		}
		return result;

	}

	/**
	 * �ж��ж��پ���
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
