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
 * �Զ������ʾ��ʿؼ�
 * 
 * @author Administrator
 *
 */
public class ShowLyricView extends TextView {

	private ArrayList<Lyric> lyrics;// ����б�
	private Paint mpaint;
	private int width;
	private int hight;
	private int index;// �ڼ�����
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
		mpaint.setTextAlign(Paint.Align.CENTER);// �������ֶ��뷽ʽ

		whitePaint = new Paint();
		whitePaint.setColor(Color.WHITE);
		whitePaint.setTextSize(Dp2PxUtil.dip2px(context, 20));
		whitePaint.setAntiAlias(true);
		whitePaint.setTextAlign(Paint.Align.CENTER);// �������ֶ��뷽ʽ
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (lyrics != null && lyrics.size() > 0) {

			float dy = 0;
			if (hightLightTime == 0) {
				dy = 0;
			} else {
				// ��ǰ��������ʱ�� :����ʱ�� = �ƶ��ľ��� :�ܾ���(�и�)
				// �ƶ��ľ��� = (��ǰ��������ʱ�� :����ʱ��)*�ܾ���(�и�)
				// float dt=((currentPosition-timePoint)/hightLightTime)*textHeight;
				// ��Ļ�ϵ����� =�и� + �ƶ��ľ���
				dy = textHeight + ((currentPosition - timePoint) / hightLightTime) * textHeight;
			}
			//Log.d("dy==", dy+"");
			//Log.d("hightLightTime==", hightLightTime+"");
			// ��ʻ��������ƶ�
			canvas.translate(0, -dy);
			// 1.���Ƹ��--��ǰ��
			String currentText = lyrics.get(index).getContent();
			canvas.drawText(currentText, width / 2, hight / 2, mpaint);
			// 2.����ǰ�沿�ָ��
			float tempY = hight / 2;// �м�λ��Y����
			for (int i = index - 1; i >= 0; i--) {
				// �õ�ÿһ����
				String preText = lyrics.get(i).getContent();
				tempY = tempY - textHeight;
				if (tempY < 0) {
					break;
				}
				canvas.drawText(preText, width / 2, tempY, whitePaint);
			}
			// 3.���ƺ��沿�ָ��
			tempY = hight / 2;// �м�λ��Y����
			for (int i = index + 1; i < lyrics.size(); i++) {
				// �õ�ÿһ����
				String nextText = lyrics.get(i).getContent();
				tempY = tempY + textHeight;
				if (tempY > hight) {
					break;
				}
				canvas.drawText(nextText, width / 2, tempY, whitePaint);
			}

		} else {
			// û�и��
			canvas.drawText("û�и��", width / 2, hight / 2, mpaint);
		}
		//invalidate();// �����߳�
	}

	/**
	 * ���ݵ�ǰ��λ�ã��ҳ�Ӧ�ø�����һ����
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
		// �ػ�
		invalidate();// �����߳�
		// postInvalidate();//�����߳�
	}

}
