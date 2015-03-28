package com.heitao.common;

public class HTConsts 
{
	/**SDK tag*/
	public final static String HTSDK_TAG = "HTGameProxySDK";
	/**SDK 版本*/
	public final static String HTSDK_VERSION = "1.0.5";
	/**SDK debug*/
	public static boolean HTSDK_DEBUG = false;
	/**SDK 平台标记*/
	public final static String HTSDK_PLATFROM = "android";
	
	//	状态码
	/**请求成功*/
	public final static int REQUEST_COMPLETED = 0;
	
	/**登录成功*/
	public final static int LOGIN_COMPLETED = 10;
	/**登录失败*/
	public final static int LOGIN_FAILED = 11;
	/**登录取消*/
	public final static int LOGIN_CANCEL = 12;
	
	/**token超时*/
	public final static int LOGIN_TOKEN_TIME_OUT = 30000;
	/**登录限制*/
	public final static int LOGIN_LIMIT = 30001;
	/**有新版本*/
	public final static int LOGIN_NEW_VERSION = 30002;
	/**充值关闭*/
	public final static int PAY_CLOSED = 30010;
	
	//	支付状态码
	/**支付成功*/
	public final static int PAY_COMPLETED = 20;
	/**支付失败*/
	public final static int PAY_FAILED = 21;
	/**支付取消*/
	public final static int PAY_CANCEL = 22;
	/**支付进行中*/
	public final static int PAY_WAITING = 23;
	/**Debug支付金额*/
	public final static int PAY_DEBUG_MONEY = 1;
	
	//	常用key字符串
	/**错误码 key*/
	public final static String KEY_ERROR_CODE = "errno";
	/**标题（登录对话框） key*/
	public final static String KEY_TITLE = "title";
	/**错误信息 key*/
	public final static String KEY_ERROR_MESSAGE = "errmsg";
	/**SDK信息 key*/
	public final static String KEY_SDK = "sdk";
	/**用户信息 key*/
	public final static String KEY_USER = "user";
	/**数据 key*/
	public final static String KEY_DATA = "data";
	/**是否强制更新 key*/
	public final static String KEY_IS_FORCE = "isforce";
	/**APK更新URL key*/
	public final static String KEY_APK_UPDATE_URL = "updateurl";
	/**订单号 key*/
	public final static String KEY_ORDER_NUMBER = "orderno";
	/**服务器 key*/
	public final static String KEY_SERVER_ID = "sid";
	/**自定义信息 key*/
	public final static String KEY_CUSTOM = "sdk";
	/**UDID key*/
	public final static String KEY_UDID = "udid";
	/**自定义渠道ID key*/
	public final static String KEY_CUSTOM_CHANNEL_ID = "chid";
	
	/**请求URL*/
	public final static String BASE_URL = "http://smi.heitao.com/";
	/**请求超时时间(s)*/
	public final static int REQUEST_TIME_OUT = 10;
	
	//	通用错误码
	/**解析错误*/
	public final static int ERROR_PARSE = 30;
	/**网络错误*/
	public final static int ERROR_NETWORK = 31;
	/**自定义错误*/
	public final static int ERROR_CUSTOM = 32;
	
	//	SDK初始化相关
	/**SDK初始化自动重试次数*/
	public final static int SDK_INIT_AUTO_RETRY_MAX_COUNT = 3;
	/**SDK初始化失败提示标题*/
	public final static String SDK_INIT_FAIL_TITLE = "提示";
	/**SDK初始化失败提示语*/
	public final static String SDK_INIT_FAIL_MESSAGE = "游戏初始化失败，请检查网络连接是否正常";
	/**SDK初始化失败提示按钮标题*/
	public final static String SDK_INIT_FAIL_BUTTON_TITLE = "重试";
	
	//	manifest键
	/**SDK接入模式*/
	public final static String KEY_JOIN_MODEL = "ht_proxy_join_model";
}
