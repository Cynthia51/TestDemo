package com.heitao.common;

import java.util.Map;

public class HTMapHelper 
{
	/**
	 * 从map中取字符串
	 * */
	public static String getString(Map<String, String> map, String key) 
	{
		if (map == null || map.isEmpty() || HTUtils.isNullOrEmpty(key)) 
		{
			return "";
		}
		
		String retValue = map.get(key);
		return HTUtils.isNullOrEmpty(retValue) ? "" : retValue;
	}
	
	/**
	 * 从map中取整形
	 * */
	public static int getInt(Map<String, String> map, String key) 
	{
		String stringValue = getString(map, key);
		if (HTUtils.isNullOrEmpty(stringValue)) 
		{
			return 0;
		}
		
		int retValue = 0;
		try 
		{
			retValue = Integer.parseInt(stringValue);
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			retValue = 0;
		}
		
		return retValue;
	}
}
