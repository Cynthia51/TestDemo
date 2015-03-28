package com.heitao.model;

import java.util.HashMap;
import java.util.Map;

import com.heitao.common.HTUtils;

public class HTAppUpdateInfo 
{
	public String versionName;				//	版本名称
	public int versionCode;					//	版本代码
	public String content;					//	更新内容
	public String apkURL;					//	APK下载地址
	public boolean isForce;					//	是否强制更新
	
	public static String KEY_VERSION_NAME = "version_name";
	public static String KEY_VERSION_CODE = "version_code";
	public static String KEY_CONTENT = "content";
	public static String KEY_APK_URL = "apk_url";
	public static String KEY_IS_FORCE = "is_force";
	
	private Map<String, String> getPropertiesMap() 
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(KEY_VERSION_NAME, versionName);
		map.put(KEY_VERSION_CODE, versionCode + "");
		map.put(KEY_CONTENT, content);
		map.put(KEY_APK_URL, apkURL);
		map.put(KEY_IS_FORCE, isForce ? "1" : "0");
		
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
