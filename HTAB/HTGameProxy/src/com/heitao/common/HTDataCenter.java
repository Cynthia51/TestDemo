package com.heitao.common;

import com.heitao.listener.HTAppUpdateListener;
import com.heitao.model.HTGameInfo;
import com.heitao.model.HTSDKInfo;

import android.content.Context;

public class HTDataCenter 
{
	private static HTDataCenter _instance = null;
	
	/**游戏上下文*/
	public Context mContext = null;
	/**游戏信息*/
	public HTGameInfo mGameInfo = null;
	/**SDK配置信息*/
	public HTSDKInfo mSDKInfo = null;
	/**更新监听*/
	public HTAppUpdateListener mAppUpdateListener = null;
	
	public static HTDataCenter getInstance() 
	{
		if (_instance == null) 
		{
			_instance = new HTDataCenter();
		}
		
		return _instance;
	}
}
