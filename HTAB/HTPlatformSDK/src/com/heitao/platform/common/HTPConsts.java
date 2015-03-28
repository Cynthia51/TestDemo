package com.heitao.platform.common;

public class HTPConsts 
{
	/**SDK tag*/
	public final static String HTPlatformSDK_TAG = "HTPlatformSDK";
	/**SDK 版本*/
	public final static String HTPlatformSDK_VERSION = "2.0.1";
	/**SDK debug*/
	public static boolean HTPlatformSDK_DEBUG = true;
	/**平台ID*/
	public final static String PLATFORM_ID = "22";
	
	//	错误码
	/**自定义错误*/
	public final static int ERROR_CUSTOM = -10000;
	/**未登录*/
	public final static int ERROR_NOT_LOGIN = -10001;
	/**解析错误*/
	public final static int ERROR_REQUEST_PARSE = -10002;
	/**请求失败*/
	public final static int ERROR_REQUEST_FAILED = -10003;
	
	//	登录错误码
	/**登录取消*/
	public final static int ERROR_LOGIN_CANCEL = -20000;
	
	//	支付错误码
	/**支付取消*/
	public final static int ERROR_PAY_CANCEL = -30000;
	/**支付信息错误*/
	public final static int ERROR_PAY_INFO = -30001;
	/**支付失败*/
	public final static int ERROR_PAY_FAIL = -30002;
	/**支付结果确认中*/
	public final static int ERROR_PAY_CONFIRMING = -30003;
	
	/**请求API*/
	public final static String API_URL = "http://api.heitao.com/";
	/**支付关闭URL*/
	public final static String PAY_CLOSE_URL = "http://pay.heitao.com/pay/finishclose";
	/**请求超时时间*/
	public final static int REQUEST_TIME_OUT = 10;
	/**支付控制中心URL*/
	public final static String PAY_CONTROL_URL = "http://pay.heitao.com/mpay/index";
}
