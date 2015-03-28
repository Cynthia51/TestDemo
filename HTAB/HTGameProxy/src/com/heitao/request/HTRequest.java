package com.heitao.request;

import java.util.Map;

import com.heitao.channel.HTChannelDispatcher;
import com.heitao.common.HTConsts;
import com.heitao.common.HTDataCenter;
import com.heitao.common.HTLog;
import com.heitao.common.HTUtils;
import com.heitao.listener.HTRequestListener;

public class HTRequest extends HTBaseRequest
{
	private HTRequestListener mRequestListener = null;
	
	public void get(String urlPath, Map<String, String> parsMap, HTRequestListener listener) 
	{
		this.get(urlPath, parsMap, listener, true);
	}
	
	public void get(String urlPath, Map<String, String> parsMap, HTRequestListener listener, boolean isAddPubPars) 
	{
		mRequestListener = listener;
		if (null == mRequestListener) 
		{
			HTLog.w("请求监听为空");
		}
		
		if (HTUtils.isNullOrEmpty(urlPath)) 
		{
			HTLog.e("请求Path为空");
			
			if (null != mRequestListener) 
			{
				mRequestListener.onFailure(new Throwable("请求Path为空"));
			}
			
			return;
		}
		
		this.doShowProgressDialog("请求中···");
		
		String baseLoginVerifyUrl = HTConsts.BASE_URL;
		if (HTUtils.isNullOrEmpty(baseLoginVerifyUrl)) 
		{
			HTLog.e("请先设置SDK请求URL");
			this.doCancelProgressDialog();
			return;
		}
		
		if (isAddPubPars) 
		{
			//	添加公共参数
			parsMap = this.addPublicParsMap(parsMap);
		}
		
		if (parsMap == null || parsMap.isEmpty()) 
		{
			HTLog.e("请求参数为空");
			if (null != mRequestListener) 
			{
				mRequestListener.onFailure(new Throwable("请求参数为空"));
			}
			this.doCancelProgressDialog();
			return;
		}
		
		String requestUrl = baseLoginVerifyUrl + HTDataCenter.getInstance().mGameInfo.shortName + "/" + urlPath + "/" + HTChannelDispatcher.getInstance().getChannelKey() + "?";
		requestUrl += HTUtils.mapToParsString(parsMap, true);
		HTLog.d("开始请求，requestUrl=" + requestUrl);
		
		this.get(requestUrl, new HTRequestListener() 
		{
			@Override
			public void onSuccess(String response) 
			{
				// TODO Auto-generated method stub
				HTLog.d("请求返回成功，response=" + response);
				doCancelProgressDialog();
				
				if (null != mRequestListener) 
				{
					mRequestListener.onSuccess(response);
				}
			}
			
			@Override
			public void onFailure(Throwable error) 
			{
				// TODO Auto-generated method stub
				doCancelProgressDialog();
				HTLog.e("请求失败，error=" + error.toString());
				
				if (null != mRequestListener) 
				{
					mRequestListener.onFailure(error);
				}
			}
		});
	}
}
