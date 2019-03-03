package com.gfq.myvdo.view;

import java.util.ArrayList;

import com.gfq.myvdo.domain.Lyric;
import com.gfq.myvdo.utiles.Dp2PxUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * 自定义的显示歌词控件
 * 
 * @author Administrator
 *
 */
public class ShowLyricView extends TextView {

	private ArrayList<Lyric> lyrics;// 歌词列表
	private Paint mpaint;
	private int width;
	private int hight;
	private int index;// 第几句歌词
	private Paint whitePaint;
	private float textHeight;
	private float currentPosition;
	private float hightLightTime;
	private float timePoint;

	public ArrayList<Lyric> getLyrics() {
		return lyrics;
	}

	public void setLyrics(ArrayList<Lyric> lyrics) {
		this.lyrics = lyrics;
	}

	public ShowLyricView(Context context) {
		this(context, null);
	}

	public ShowLyricView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ShowLyricView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		hight = h;
	}

	private void initView(Context context) {
		textHeight = Dp2PxUtil.dip2px(context, 20);
		mpaint = new Paint();
		mpaint.setColor(Color.GREEN);
		mpaint.setTextSize(Dp2PxUtil.dip2px(context, 20));
		mpaint.setAntiAlias(true);
		mpaint.setTextAlign(Paint.Align.CENTER);// 设置文字对齐方式

		whitePaint = new Paint();
		whitePaint.setColor(Color.WHITE);
		whitePaint.setTextSize(Dp2PxUtil.dip2px(context, 20));
		whitePaint.setAntiAlias(true);
		whitePaint.setTextAlign(Paint.Align.CENTER);// 设置文字对齐方式
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (lyrics != null && lyrics.size() > 0) {

			float dy = 0;
			if (hightLightTime == 0) {
				dy = 0;
			} else {
				// 当前句所花的时间 :高亮时间 = 移动的距离 :总距离(行高)
				// 移动的距离 = (当前句所花的时间 :高亮时间)*总距离(行高)
				// float dt=((currentPosition-timePoint)/hightLightTime)*textHeight;
				// 屏幕上的坐标 =行高 + 移动的距离
				dy = textHeight + ((currentPosition - timePoint) / hightLightTime) * textHeight;
			}
			//Log.d("dy==", dy+"");
			//Log.d("hightLightTime==", hightLightTime+"");
			// 歌词缓缓网上移动
			canvas.translate(0, -dy);
			// 1.绘制歌词--当前句
			String currentText = lyrics.get(index).getContent();
			canvas.drawText(currentText, width / 2, hight / 2, mpaint);
			// 2.绘制前面部分歌词
			float tempY = hight / 2;// 中间位置Y坐标
			for (int i = index - 1; i >= 0; i--) {
				// 得到每一句歌词
				String preText = lyrics.get(i).getContent();
				tempY = tempY - textHeight;
				if (tempY < 0) {
					break;
				}
				canvas.drawText(preText, width / 2, tempY, whitePaint);
			}
			// 3.绘制后面部分歌词
			tempY = hight / 2;// 中间位置Y坐标
			for (int i = index + 1; i < lyrics.size(); i++) {
				// 得到每一句歌词
				String nextText = lyrics.get(i).getContent();
				tempY = tempY + textHeight;
				if (tempY > hight) {
					break;
				}
				canvas.drawText(nextText, width / 2, tempY, whitePaint);
			}

		} else {
			// 没有歌词
			canvas.drawText("没有歌词", width / 2, hight / 2, mpaint);
		}
		//invalidate();// 在主线程
	}

	/**
	 * 根据当前的位置，找出应该高亮哪一句歌词
	 * 
	 * @param currentPosition
	 */
	public void setShowNextLyric(int currentPosition) {
		this.currentPosition = currentPosition;
		if (lyrics != null && lyrics.size() > 0) {
			for (int i = 1; i < lyrics.size(); i++) {
				if (currentPosition < lyrics.get(i).getTimePoint()) {
					int tempIndex = i - 1;
					if (currentPosition >= lyrics.get(tempIndex).getTimePoint()) {
						index = tempIndex;
						hightLightTime = lyrics.get(index).getHighLightTime();
						timePoint = lyrics.get(index).getTimePoint();

					}
				}
			}
		}
		// 重绘
		invalidate();// 在主线程
		// postInvalidate();//在子线程
	}

}
