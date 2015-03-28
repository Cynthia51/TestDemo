package com.heitao.request;

import java.util.Map;

import org.json.JSONObject;

import com.heitao.listener.HTLoginVerifyListener;

public class HTLoginVerify extends HTBaseLoginVerify
{
	/**
	 * 登录验证
	 * */
	public void doLoginVerify(Map<String, String> parsMap, HTLoginVerifyListener listener)
	{
		super.doLoginVerify(parsMap, listener);
	}
	
	/**
	 * 解析完成
	 * */
	protected void parseCompleted(JSONObject sdkJSONObject) 
	{
		// TODO::	解析非公共部分
		
		if (mLoginVerifyListener != null) 
		{
			mLoginVerifyListener.onHTLoginVerifyCompleted(mUser, null);
		}
	}
}
