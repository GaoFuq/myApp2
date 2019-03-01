package com.gfq.myvdo.utiles;

import com.gfq.myvdo.service.MusicPlayerService;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * ª∫¥Êπ§æﬂ¿‡
 * @author Administrator
 *
 */
public class CacheUtil {

	public static void putString(Context context,String key,String value) {
		SharedPreferences sharedPreferences=context.getSharedPreferences("myApp2", Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(key, value).commit();
	}
	
	public static String getString(Context context,String key) {
		SharedPreferences sharedPreferences=context.getSharedPreferences("myApp2", Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, "");

	}
	
	public static void putInt(Context context,String key,int value) {
		SharedPreferences sharedPreferences=context.getSharedPreferences("myApp2", Context.MODE_PRIVATE);
		sharedPreferences.edit().putInt(key, value).commit();
	}
	
	public static int getInt(Context context,String key) {
		SharedPreferences sharedPreferences=context.getSharedPreferences("myApp2", Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, MusicPlayerService.REPEAT_NORMAL);

	}
}
