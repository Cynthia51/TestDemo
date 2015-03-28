package com.heitao.platform.common;

import com.heitao.platform.api.HTPlatform.HTPlatformDirection;
import com.heitao.platform.listener.HTPLoginListener;
import com.heitao.platform.listener.HTPPayListener;
import com.heitao.platform.model.HTPPayInfo;
import com.heitao.platform.model.HTPUser;

import android.content.Context;

public class HTPDataCenter 
{
	private static HTPDataCenter mInstance = null;
	
	/**游戏上下文*/
	public Context mContext = null;
	/**登录监听*/
	public HTPLoginListener mLoginListener = null;
	/**支付监听*/
	public HTPPayListener mPayListener = null;
	/**AppKey*/
	public String mAppKey = null;
	/**SecretKey*/
	public String mSecretKey = null;
	/**ChannelId*/
	public String mChannelId = "";
	/**用户信息*/
	public HTPUser mUser = null;
	/**支付信息*/
	public HTPPayInfo mPayInfo = null;
	/**屏幕方向*/
	public HTPlatformDirection mDirection = HTPlatformDirection.Portrait;
	
	public static HTPDataCenter getInstance() 
	{
		if (mInstance == null) 
		{
			mInstance = new HTPDataCenter();
		}
		
		return mInstance;
	}
	
	/**
	 * 重置
	 * */
	public void reset() 
	{
		mUser = null;
		mPayInfo = null;
	}
}
