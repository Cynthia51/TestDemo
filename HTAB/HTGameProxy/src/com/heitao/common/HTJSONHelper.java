package com.heitao.common;

import org.json.JSONObject;

public class HTJSONHelper 
{
	/**
	 * 从JSONObject获取整型值值
	 * @param jsonObject JSONObject
	 * @param key 键
	 * @return intValue
	 * */
	public static int getInt(JSONObject jsonObject, String key) 
	{
		if (jsonObject == null || HTUtils.isNullOrEmpty(key) || jsonObject.isNull(key)) 
		{
			return 0;
		}
		
		int retValue = 0;
		try 
		{
			retValue = jsonObject.getInt(key);
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			retValue = 0;
		}
		
		return retValue;
	}
	
	/**
	 * 从JSONObject获取字符串值
	 * @param jsonObject JSONObject
	 * @param key 键
	 * @return StringValue
	 * */
	public static String getString(JSONObject jsonObject, String key) 
	{
		if (jsonObject == null || HTUtils.isNullOrEmpty(key) || jsonObject.isNull(key)) 
		{
			return null;
		}
		
		String retValue = null;
		try 
		{
			retValue = jsonObject.getString(key);
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			retValue = null;
		}
		
		return retValue;
	}
	
	/**
	 * 从JSONObject获取JSON对象值
	 * @param jsonObject JSONObject
	 * @param key 键
	 * @return JSONObject
	 * */
	public static JSONObject getJSONObject(JSONObject jsonObject, String key) 
	{
		if (jsonObject == null || HTUtils.isNullOrEmpty(key) || jsonObject.isNull(key)) 
		{
			return null;
		}
		
		JSONObject retValue = null;
		try 
		{
			retValue = jsonObject.getJSONObject(key);
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			retValue = null;
		}
		
		return retValue;
	}
	
	/**
	 * JSONObject是否存在对应的值
	 * @param jsonObject JSONObject
	 * @param key 键
	 * @return boolean
	 * */
	public static boolean isNull(JSONObject jsonObject, String key) 
	{
		if (jsonObject == null || HTUtils.isNullOrEmpty(key)) 
		{
			return true;
		}
		
		return jsonObject.isNull(key);
	}
}
