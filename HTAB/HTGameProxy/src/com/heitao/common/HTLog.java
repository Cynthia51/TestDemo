package com.heitao.common;

import android.util.Log;

public class HTLog 
{
	public static boolean mLogEnable = false;
	
	public static void e(String message) 
	{
		Log.e(HTConsts.HTSDK_TAG, message);
	}
	
	public static void i(String message) 
	{
		if (mLogEnable)
		{
			Log.i(HTConsts.HTSDK_TAG, message);
		}
	}
	
	public static void w(String message) 
	{
		if (mLogEnable)
		{
			Log.w(HTConsts.HTSDK_TAG, message);
		}
	}
	
	public static void d(String message) 
	{
		if (HTConsts.HTSDK_DEBUG)
		{
			Log.d(HTConsts.HTSDK_TAG, message);
		}
	}
}
