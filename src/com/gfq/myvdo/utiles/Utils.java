package com.gfq.myvdo.utiles;

import android.content.Context;
import android.net.TrafficStats;

import java.util.Formatter;
import java.util.Locale;

public class Utils {

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public Utils() {
        // ת�����ַ�����ʱ��
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    }

    /**
     * �Ѻ���ת���ɣ�1:20:30������ʽ
     *
     * @param timeMs
     * @return
     */
    public String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;

        int minutes = (totalSeconds / 60) % 60;

        int hours = totalSeconds / 3600;

        int day =(totalSeconds / 3600) %24;
        mFormatBuilder.setLength(0);
        if (day > 0) {
            //return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
            //        .toString();
            return mFormatter.format("%02d:%02d", hours, minutes).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    /**
     * �ж��Ƿ����������Դ
     * @param uri
     * @return
     */
    public boolean isNetUri(String uri) {
        boolean reault = false;
        if (uri != null) {
            if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")) {
                reault = true;
            }
        }
        return reault;
    }


    /**
     * �õ������ٶ�
     * ÿ���������һ��
     * @param context
     * @return
     */
    public String getNetSpeed(Context context) {
        String netSpeed = "0 kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//תΪKB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//����ת��

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        netSpeed  = String.valueOf(speed) + " kb/s";
        return  netSpeed;
    }


}
