package com.heitao.model;

import java.util.HashMap;
import java.util.Map;

import com.heitao.common.HTUtils;

public class HTSDKInfo 
{
	public String appId;				//AppId
	public String appKey;				//AppKey
	public String secretKey;			//SecretKey
	public String payKey;				//支付Key
	public String extendKey;			//扩展Key
	public String customChannelId;		//自定义渠道ID
	
	public static String KEY_APP_ID = "app_id";
	public static String KEY_APP_KEY = "app_key";
	public static String KEY_SECRET_KEY = "secret_key";
	public static String KEY_PAY_KEY = "pay_key";
	public static String KEY_EXTEND_KEY = "extend_key";
	public static String KEY_CUSTOM_CHANNEL_ID = "custom_channel_id";
	
	private Map<String, String> getPropertiesMap() 
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(KEY_APP_ID, appId);
		map.put(KEY_APP_KEY, appKey);
		map.put(KEY_SECRET_KEY, secretKey);
		map.put(KEY_EXTEND_KEY, extendKey);
		map.put(KEY_PAY_KEY, payKey);
		map.put(KEY_CUSTOM_CHANNEL_ID, customChannelId);
		
		return map;
	}
	
	public String toString() 
	{
		return HTUtils.mapToParsString(this.getPropertiesMap(), false);
	}
	
	public String toEncodeString() 
	{
		return HTUtils.mapToParsString(this.getPropertiesMap(), true);
	}
}
