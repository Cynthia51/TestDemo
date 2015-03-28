package com.heitao.proxy;

import com.heitao.channel.HTChannelDispatcher;

public class HTProxy 
{
	/**
	 * 登录
	 * */
	public static void doProxyLogin(String customParameters) 
	{
		HTChannelDispatcher.getInstance().doLogin(customParameters, null);
	}
	
	/**
	 * 登出
	 * */
	public static void doProxyLogout(String customParameters) 
	{
		HTChannelDispatcher.getInstance().doLogout(customParameters, null);
	}
	
	/**
	 * 支付
	 * */
	public static void doProxyPay(String customParameters) 
	{
		HTChannelDispatcher.getInstance().doPay(customParameters, null);
	}
	
	/**
	 * 退出
	 * */
	public static void doProxyExit() 
	{
		HTChannelDispatcher.getInstance().doExit(null);
	}
	
	/**
	 * 开始游戏
	 * */
	public static boolean onProxyStartGame() 
	{
		return HTChannelDispatcher.getInstance().onStartGame();
	}
	
	/**
	 * 进入游戏
	 * */
	public static void onProxyEnterGame(String infos) 
	{
		HTChannelDispatcher.getInstance().onEnterGame(infos);
	}
	
	/**
	 * 游戏等级发生变化
	 * */
	public static void onProxyGameLevelChanged(int newLevel) 
	{
		HTChannelDispatcher.getInstance().onGameLevelChanged(newLevel);
	}
	
	/*** 登录成功(native) */
	public static native void doNativeLoginCompleted(String data);
	/*** 登录失败(native) */
	public static native void doNativeLoginFailed(String data);
	/*** 注销成功(native) */
	public static native void doNativeLogoutCompleted(String data);
	/*** 注销失败(native) */
	public static native void doNativeLogoutFailed(String data);
	/*** 支付成功(native) */
	public static native void doNativePayCompleted(String data);
	/*** 支付失败(native) */
	public static native void doNativePayFailed(String data);
	/*** 自定义支付(native) */
	public static native void doNativeCustomPay(String data);
	/*** 游戏本身退出(native) */
	public static native void doNativeGameExit();
	/*** 第三方退出(native) */
	public static native void doNativeThirdPartyExit();
}
