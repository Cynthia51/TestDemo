package com.heitao.model;

import java.util.HashMap;
import java.util.Map;

import com.heitao.common.HTUtils;

public class HTGameInfo 
{
	public enum HTDirection
	{
		Landscape,								//横屏
		Portrait								//竖屏
	}
	
	public String name;							//游戏名称
	public String shortName;					//游戏简称 e.g. MHJ
	public HTDirection direction;				//游戏屏幕方向
	
	public static String KEY_NAME = "name";
	public static String KEY_SHORT_NAME = "short_name";
	public static String KEY_DIRECTION = "direction";
	
	public HTGameInfo()
	{
		name = "";
		shortName = "";
		direction = HTDirection.Landscape;
	}
	
	/**
	 * 初始化游戏信息
	 * @param _name 				游戏名
	 * @param _shortName			游戏名简写
	 * @param _direction 			游戏显示方向
	 * */
	public HTGameInfo(String _name, String _shortName, HTDirection _direction)
	{
		name = _name;
		shortName = _shortName;
		direction = _direction;
	}
	
	private Map<String, String> getPropertiesMap() 
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(KEY_NAME, name);;
		map.put(KEY_SHORT_NAME, shortName);
		map.put(KEY_DIRECTION, (direction == HTDirection.Landscape ? "landscape" : "portrait"));
		
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
