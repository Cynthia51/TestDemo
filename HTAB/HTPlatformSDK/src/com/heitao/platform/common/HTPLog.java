package com.heitao.platform.common;

import android.util.Log;

public class HTPLog 
{
	public static boolean mLogEnable = false;
	
	public static void e(String message) 
	{
		Log.e(HTPConsts.HTPlatformSDK_TAG, message);
	}
	
	public static void i(String message) 
	{
		if (mLogEnable)
		{
			Log.i(HTPConsts.HTPlatformSDK_TAG, message);
		}
	}
	
	public static void w(String message) 
	{
		if (mLogEnable)
		{
			Log.i(HTPConsts.HTPlatformSDK_TAG, message);
		}
	}
	
	public static void d(String message) 
	{
		if (HTPConsts.HTPlatformSDK_DEBUG)
		{
			Log.d(HTPConsts.HTPlatformSDK_TAG, message);
		}
	}
}
