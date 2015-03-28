package com.heitao.model;

import java.util.HashMap;
import java.util.Map;

import com.heitao.common.HTConsts;
import com.heitao.common.HTUtils;

public class HTError 
{
	public String code;					//错误码
	public String message;				//错误消息
	
	public static String KEY_CODE = "code";
	public static String KEY_MESSAGE = "message";
	
	public HTError()
	{
		code = "";
		message = "";
	}
	
	/**
	 * 初始化错误信息
	 * @param _code 	错误代码
	 * @param _message 	错误信息
	 * */
	public HTError(String _code, String _message)
	{
		code = _code;
		message = _message;
	}
	
	/**
	 * 获取常见错误类型----解析错误
	 * */
	public static HTError getParsError() 
	{
		return new HTError(HTConsts.ERROR_PARSE + "", "解析错误");
	}
	
	/**
	 * 获取常见错误类型----网络错误
	 * */
	public static HTError getNetworkError() 
	{
		return new HTError(HTConsts.ERROR_NETWORK + "", "网络错误");
	}
	
	/**
	 * 获取常见错误类型----登录取消
	 * */
	public static HTError getLoginCancelError() 
	{
		return new HTError(HTConsts.LOGIN_CANCEL + "", "登录取消");
	}
	
	/**
	 * 获取常见错误类型----登录失败
	 * */
	public static HTError getLoginFailError() 
	{
		return new HTError(HTConsts.LOGIN_FAILED + "", "登录失败");
	}
	
	/**
	 * 获取常见错误类型----支付取消
	 * */
	public static HTError getPayCancelError() 
	{
		return new HTError(HTConsts.LOGIN_CANCEL + "", "支付取消");
	}
	
	/**
	 * 获取常见错误类型----支付失败
	 * */
	public static HTError getPayFailError() 
	{
		return new HTError(HTConsts.PAY_FAILED + "", "支付失败");
	}
	
	/**
	 * 获取自定义错误
	 * @param message 错误信息
	 * */
	public static HTError getCustomError(String message) 
	{
		return new HTError(HTConsts.ERROR_CUSTOM + "", message);
	}
	
	private Map<String, String> getPropertiesMap() 
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(KEY_CODE, code);;
		map.put(KEY_MESSAGE, message);
		
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
