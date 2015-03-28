package com.heitao.platform.model;

import com.heitao.platform.common.HTPConsts;

public class HTPError 
{
	public int code;
	public String message;
	
	public HTPError(){}
	
	public HTPError(int _code, String _message)
	{
		code = _code;
		message = _message;
	}
	
	/**
	 * 自定义错误
	 * */
	public static HTPError getCustomError(String message) 
	{
		return new HTPError(HTPConsts.ERROR_CUSTOM, message);
	}
	
	/**
	 * 用户未登录错误
	 * */
	public static HTPError getNotLoginError() 
	{
		return new HTPError(HTPConsts.ERROR_NOT_LOGIN, "未登录");
	}
	
	/**
	 * 用户登录取消
	 * */
	public static HTPError getLoginCancelError() 
	{
		return new HTPError(HTPConsts.ERROR_LOGIN_CANCEL, "登录取消");
	}
	
	/**
	 * 用户支付取消
	 * */
	public static HTPError getPayCancelError() 
	{
		return new HTPError(HTPConsts.ERROR_PAY_CANCEL, "支付取消");
	}
	
	/**
	 * 支付信息错误
	 * */
	public static HTPError getPayInfoError() 
	{
		return new HTPError(HTPConsts.ERROR_PAY_INFO, "支付信息错误");
	}
	
	/**
	 * 支付信息错误
	 * */
	public static HTPError getPayFailError() 
	{
		return new HTPError(HTPConsts.ERROR_PAY_FAIL, "支付失败");
	}
	
	/**
	 * 支付信息错误
	 * */
	public static HTPError getPayConfirmingError() 
	{
		return new HTPError(HTPConsts.ERROR_PAY_CONFIRMING, "支付结果确认中");
	}
	
	/**
	 * 解析错误
	 * */
	public static HTPError getRequestParseError() 
	{
		return new HTPError(HTPConsts.ERROR_REQUEST_PARSE, "解析错误");
	}
	
	/**
	 * 请求错误
	 * */
	public static HTPError getRequestFailedError() 
	{
		return new HTPError(HTPConsts.ERROR_REQUEST_FAILED, "请求失败");
	}
	
	@Override
	public String toString() 
	{
		// TODO Auto-generated method stub
		return "code=" + code + "&message=" + message;
	}
}
