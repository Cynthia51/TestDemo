package com.heitao.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.heitao.channel.HTChannelDispatcher;
import com.heitao.common.HTConsts;
import com.heitao.common.HTDataCenter;
import com.heitao.common.HTDevice;
import com.heitao.common.HTJSONHelper;
import com.heitao.common.HTLog;
import com.heitao.common.HTUtils;
import com.heitao.listener.HTInitListener;
import com.heitao.listener.HTRequestListener;
import com.heitao.model.HTError;
import com.heitao.model.HTSDKInfo;

public class HTSDKInfoReader extends HTBaseRequest
{
	/**
	 * 从服务器获取SDK配置信息
	 * */
	public void getSDKInfo(final HTInitListener listener) 
	{
		if (listener == null) 
		{
			HTLog.e("未设置初始化回调监听");
		}
		
		this.doShowProgressDialog("平台初始化中···");
		
		String baseLoginVerifyUrl = HTConsts.BASE_URL;
		if (HTUtils.isNullOrEmpty(baseLoginVerifyUrl)) 
		{
			HTLog.e("请先设置SDK请求URL");
			this.doCancelProgressDialog();
			return;
		}
		
		String shortName = HTDataCenter.getInstance().mGameInfo.shortName;
		String channelKey = HTChannelDispatcher.getInstance().getChannelKey();
		String hthdMD5 = HTUtils.getMD5("heitaohudong");
		String joinMD5 = HTUtils.getMD5(shortName + channelKey + hthdMD5).substring(3, 17);
		String token = HTUtils.getMD5(joinMD5);
		
		Map<String, String> parsMap = new HashMap<String, String>();
		parsMap.put(HTConsts.KEY_UDID, HTDevice.getDeviceId());
		parsMap.put("token", token);
		
		String requestUrl = baseLoginVerifyUrl + shortName + "/init/" + channelKey + "?";
		requestUrl += HTUtils.mapToParsString(parsMap, true);
		HTLog.d("初始化开始请求，requestUrl=" + requestUrl);
		
		//	发送请求
		this.get(requestUrl, new HTRequestListener() 
		{
			@Override
			public void onSuccess(String response) 
			{
				HTLog.d("初始化返回成功，response=" + response);
				doCancelProgressDialog();
				
				try {
					JSONObject responseObject = new JSONObject(response);
					
					//	all
					int code = HTJSONHelper.getInt(responseObject, HTConsts.KEY_ERROR_CODE);
					String message = HTJSONHelper.getString(responseObject, HTConsts.KEY_ERROR_MESSAGE);
					
					if (code == HTConsts.REQUEST_COMPLETED && !responseObject.isNull(HTConsts.KEY_ERROR_CODE)) 
					{
						JSONObject dataObject = HTJSONHelper.getJSONObject(responseObject, HTConsts.KEY_DATA);
						if (dataObject != null  && !responseObject.isNull(HTConsts.KEY_DATA)) 
						{
							//	解析SDKInfo
							HTSDKInfo sdkInfo = new HTSDKInfo();
							
							sdkInfo.appId = HTJSONHelper.getString(dataObject, HTSDKInfo.KEY_APP_ID);
							sdkInfo.appKey = HTJSONHelper.getString(dataObject, HTSDKInfo.KEY_APP_KEY);
							sdkInfo.secretKey = HTJSONHelper.getString(dataObject, HTSDKInfo.KEY_SECRET_KEY);
							sdkInfo.payKey = HTJSONHelper.getString(dataObject, HTSDKInfo.KEY_PAY_KEY);
							sdkInfo.extendKey = HTJSONHelper.getString(dataObject, HTSDKInfo.KEY_EXTEND_KEY);
							sdkInfo.customChannelId = HTJSONHelper.getString(dataObject, HTSDKInfo.KEY_CUSTOM_CHANNEL_ID);
							
							if (listener != null) 
							{
								listener.onHTInitCompleted(sdkInfo);
							}
							
							HTLog.d("初始化解析完成");
						}
					}
					else 
					{
						if (listener != null) 
						{
							listener.onHTInitFailed(new HTError(code + "", message));
						}
						
						HTLog.d("初始化解析失败，error=" + message);
					}
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
					HTLog.e("初始化解析失败，error=" + e.toString());
					if (listener != null) 
					{
						listener.onHTInitFailed(HTError.getParsError());
					}
				}
			}
			
			@Override
			public void onFailure(Throwable e) 
			{ 
				HTLog.e("初始化请求失败，error=" + e.toString());
				doCancelProgressDialog();
				if (listener != null) 
				{
					listener.onHTInitFailed(HTError.getNetworkError());
				}
			}
		});
	}
	
	
	/**
	 * 从文件获取SDK参数信息
	 * */
	public HTSDKInfo getSDKInfoFromFile() 
	{
		String jsonValue = this.getJsonValue();
		if (HTUtils.isNullOrEmpty(jsonValue)) 
		{
			HTLog.w("读取SDK配置信息为空");
		}
		else 
		{
			HTSDKInfo sdkInfo = null;
			try 
			{
				sdkInfo = new HTSDKInfo();
				
				/*
				String password = HTChannelDispatcher.getInstance().getChannelKey();
				
				JSONObject configJsonObject = new JSONObject(jsonValue);
				sdkInfo.appId = HTDESHelper.decrypt(HTJSONHelper.getString(configJsonObject, HTSDKInfo.KEY_APP_ID), password);
				sdkInfo.appKey = HTDESHelper.decrypt(HTJSONHelper.getString(configJsonObject, HTSDKInfo.KEY_APP_KEY), password);
				sdkInfo.payKey = HTDESHelper.decrypt(HTJSONHelper.getString(configJsonObject, HTSDKInfo.KEY_PAY_KEY), password);
				sdkInfo.extendKey = HTDESHelper.decrypt(HTJSONHelper.getString(configJsonObject, HTSDKInfo.KEY_EXTEND_KEY), password);
				sdkInfo.customChannelId = HTDESHelper.decrypt(HTJSONHelper.getString(configJsonObject, HTSDKInfo.KEY_CUSTOM_CHANNEL_ID), password);
				*/
				
				JSONObject configJsonObject = new JSONObject(jsonValue);
				sdkInfo.appId = HTJSONHelper.getString(configJsonObject, HTSDKInfo.KEY_APP_ID);
				sdkInfo.appKey = HTJSONHelper.getString(configJsonObject, HTSDKInfo.KEY_APP_KEY);
				sdkInfo.secretKey = HTJSONHelper.getString(configJsonObject, HTSDKInfo.KEY_SECRET_KEY);
				sdkInfo.payKey = HTJSONHelper.getString(configJsonObject, HTSDKInfo.KEY_PAY_KEY);
				sdkInfo.extendKey = HTJSONHelper.getString(configJsonObject, HTSDKInfo.KEY_EXTEND_KEY);
				sdkInfo.customChannelId = HTJSONHelper.getString(configJsonObject, HTSDKInfo.KEY_CUSTOM_CHANNEL_ID);
			} 
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				sdkInfo = null;
				HTLog.e("解析SDK配置信息异常");
			}
			
			return sdkInfo;
		}
		
		return null;
	}
	
	/**
	 * 获取JSON文件值
	 * */
	private String getJsonValue()
	{
		String jsonValue = "";
		try 
		{
			Context mContext = HTDataCenter.getInstance().mContext;
			
			InputStreamReader inputReader = new InputStreamReader(mContext.getResources().getAssets().open("ht_proxy_config.json"));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String result = "";
			while ((line = bufReader.readLine()) != null)
			{
				result += line;
			}
			jsonValue = result;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			
			jsonValue = "";
			HTLog.w("读取SDK配置信息文件失败，error=" + e.getMessage());
		}
		
		return jsonValue;
	}
}
