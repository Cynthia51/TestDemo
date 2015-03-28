package com.heitao.model;

import java.util.HashMap;
import java.util.Map;

import com.heitao.common.HTConsts;
import com.heitao.common.HTUtils;

public class HTPayResult 
{
	public String code;							//错误码
	public String message;						//错误信息
	public String custom;						//自定义信息
	
	public static String KEY_CODE = "code";
	public static String KEY_MESSAGE = "message";
	public static String KEY_CUSTOM = "custom";
	
	public HTPayResult()
	{
		init();
	}
	
	/**
	 * @param _code 	错误码
	 * @param _message 	错误信息
	 * @param _custom 	自定义信息
	 * */
	public HTPayResult(String _code, String _message, String _custom)
	{
		code = _code;
		message = _message;
		custom = _custom;
	}
	
	private void init()
	{
		code = "";
		message = "";
		custom = "";
	}
	
	/**
	 * 获取常见支付结果类型----支付成功
	 * */
	public static HTPayResult getPayCompleteRestlt(Map<String, String> customMap) 
	{
		return new HTPayResult(HTConsts.PAY_COMPLETED + "", "支付完成", HTUtils.mapToParsString(customMap, true));
	}
	
	private Map<String, String> getPropertiesMap() 
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(KEY_CODE, code);;
		map.put(KEY_MESSAGE, message);
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
