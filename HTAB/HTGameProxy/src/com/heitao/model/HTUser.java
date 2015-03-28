package com.heitao.model;

import java.util.HashMap;
import java.util.Map;

import com.heitao.common.HTUtils;

public class HTUser 
{
	public String userId;						//黑桃用户ID
	public String platformUserId;				//平台用户ID
	public String platformId;					//平台ID
	public String token;						//登录Token
	public String custom;						//自定义参数信息
	
	public static String KEY_USER_ID = "user_id";
	public static String KEY_PLATFORM_USER_ID = "platform_user_id";
	public static String KEY_PLATFORM_ID = "platform_id";
	public static String KEY_TOKEN = "token";
	public static String KEY_CUSTOM = "custom";
	
	public HTUser()
	{
		init();
	}
	
	/**
	 * 初始化用户信息
	 * @param _userId 			用户id
	 * @param _platformUserId 	平台用户ID
	 * @param _platformId 		平台ID
	 * @param _token 			用户token
	 * @param _custom 			其他参数
	 * */
	public HTUser(String _userId, String _platformUserId, String _platformId, String _token, String _custom)
	{
		userId = _userId;
		platformUserId = _platformUserId;
		platformId = _platformId;
		token = _token;
		custom = _custom;
	}
	
	/**
	 * 初始化用户信息
	 * @param pars 参数集合
	 */
	public HTUser(String pars)
	{
		Map<String, String> map = HTUtils.parsStringToMap(pars, true);
		if (map != null) 
		{
			userId = map.get(KEY_USER_ID);
			platformUserId = map.get(KEY_PLATFORM_USER_ID);
			platformId = map.get(KEY_PLATFORM_ID);
			token = map.get(KEY_TOKEN);
			custom = map.get(KEY_CUSTOM);
		}
	}
	
	/**
	 * 初始化用户信息
	 * @param map 参数集合
	 */
	public HTUser(Map<String, String> map)
	{
		if (map != null) 
		{
			userId = map.get(KEY_USER_ID);
			platformUserId = map.get(KEY_PLATFORM_USER_ID);
			platformId = map.get(KEY_PLATFORM_ID);
			token = map.get(KEY_TOKEN);
			custom = map.get(KEY_CUSTOM);
		}
	}
	
	private void init()
	{
		userId = "";
		platformUserId = "";
		platformId = "";
		token = "";
		custom = "";
	}
	
	private Map<String, String> getPropertiesMap() 
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(KEY_USER_ID, userId);;
		map.put(KEY_PLATFORM_USER_ID, platformUserId);
		map.put(KEY_PLATFORM_ID, platformId);
		map.put(KEY_TOKEN, token);
		map.put(KEY_CUSTOM, custom);
		
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
