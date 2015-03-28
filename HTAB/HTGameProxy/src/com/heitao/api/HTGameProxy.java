package com.heitao.api;

import java.util.Map;

import com.heitao.channel.HTChannelDispatcher;
import com.heitao.common.HTConsts;
import com.heitao.common.HTDataCenter;
import com.heitao.common.HTLog;
import com.heitao.common.HTUtils;
import com.heitao.listener.HTAppUpdateListener;
import com.heitao.listener.HTExitListener;
import com.heitao.listener.HTLoginListener;
import com.heitao.listener.HTLogoutListener;
import com.heitao.listener.HTPayListener;
import com.heitao.model.HTGameInfo;
import com.heitao.model.HTPayInfo;

import android.content.Context;
import android.content.Intent;

public class HTGameProxy 
{
	/**
	 * 初始化黑桃SDK
	 * @param context context
	 * @param gameInfo 游戏信息
	 * */
	public static void init(Context context, HTGameInfo gameInfo) 
	{
		if (context == null) 
		{
			HTLog.e("初始化Context为空");
		}
		
		if (gameInfo == null) 
		{
			HTLog.e("初始化GameInfo为空");
		}
		
		HTDataCenter.getInstance().mContext = context;
		HTDataCenter.getInstance().mGameInfo = gameInfo;
	}
	
	/**
	 * 显示悬浮按钮
	 * @param show 是否显示
	 * */
	public static void setShowFunctionMenu(boolean show) 
	{
		HTChannelDispatcher.getInstance().setShowFunctionMenu(show);
	}
	
	/**
	 * 获取SDK版本号
	 * @return 版本号
	 * */
	public static String getSDKVersion() 
	{
		return HTConsts.HTSDK_VERSION;
	}
	
	/**
	 * 获取SDK版本号
	 * @return 版本号
	 * */
	public static String getChannelSDKVersion() 
	{
		return HTChannelDispatcher.getInstance().getChannelSDKVersion();
	}
	
	/**
	 * Debug模式设置
	 * @param enable 是否打开
	 * */
	public static void setDebugEnable(boolean enable) 
	{
		HTConsts.HTSDK_DEBUG = enable;
	}
	
	/**
	 * 打印日志
	 * @param enable 是否打印
	 * */
	public static void setLogEnable(boolean enable) 
	{
		HTLog.mLogEnable = enable;
	}
	
	/*Activity部分生命周期方法----开始*/
	
	public static void onCreate(Context context) 
	{
		HTChannelDispatcher.getInstance().onCreate(context);
	}
	
	public static void onPause() 
	{
		HTChannelDispatcher.getInstance().onPause();
	}
	
	public static void onResume() 
	{
		HTChannelDispatcher.getInstance().onResume();
	}
	
	public static void onStop() 
	{
		HTChannelDispatcher.getInstance().onStop();
	}
	
	public static void onDestroy() 
	{
		HTChannelDispatcher.getInstance().onDestroy();
	}
	
	public static void onRestart() 
	{
		HTChannelDispatcher.getInstance().onRestart();
	}
	
	public static void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		HTChannelDispatcher.getInstance().onActivityResult(requestCode, resultCode, data);
	}
	
	/*Activity部分生命周期方法----结束*/
	
	/**
	 * 登录
	 * @param customMap 自定义参数Map
	 * @param listener 登录监听
	 * */
	public static void doLogin(Map<String, String> customMap, HTLoginListener listener) 
	{
		HTChannelDispatcher.getInstance().doLogin(HTUtils.mapToParsString(customMap, true), listener);
	}
	
	/**
	 * 登出
	 * @param customMap 自定义参数Map
	 * @param listener 登出监听
	 * */
	public static void doLogout(Map<String, String> customMap, HTLogoutListener listener) 
	{
		HTChannelDispatcher.getInstance().doLogout(HTUtils.mapToParsString(customMap, true), listener);
	}
	
	/**
	 * 支付
	 * @param payInfo 支付信息
	 * @param listener 支付监听
	 * */
	public static void doPay(HTPayInfo payInfo, HTPayListener listener) 
	{
		HTChannelDispatcher.getInstance().doPay(payInfo == null ? null : payInfo.toEncodeString(), listener);
	}
	
	/**
	 * 退出
	 * @param listener 退出监听
	 * */
	public static void doExit(HTExitListener listener) 
	{
		HTChannelDispatcher.getInstance().doExit(listener);
	}
	
	/**
	 * 开始游戏
	 * */
	public static boolean onStartGame() 
	{
		return HTChannelDispatcher.getInstance().onStartGame();
	}
	
	/**
	 * 进入游戏
	 * @param customMap 自定义参数Map
	 * */
	public static void onEnterGame(Map<String, String> customMap) 
	{
		HTChannelDispatcher.getInstance().onEnterGame(HTUtils.mapToParsString(customMap, true));
	}
	
	/**
	 * 游戏等级发生变化
	 * @param newLevel 新等级
	 * */
	public static void onGameLevelChanged(int newLevel) 
	{
		HTChannelDispatcher.getInstance().onGameLevelChanged(newLevel);
	}
	
	/**
	 * 设置登录监听
	 * @param listener 登录监听
	 * */
	public static void setLogoinListener(HTLoginListener listener) 
	{
		HTChannelDispatcher.getInstance().setLogoinListener(listener);
	}
	
	/**
	 * 设置登出监听
	 * @param listener 登出监听
	 * */
	public static void setLogoutListener(HTLogoutListener listener) 
	{
		HTChannelDispatcher.getInstance().setLogoutListener(listener);
	}
	
	/**
	 * 设置支付监听
	 * @param listener 支付监听
	 * */
	public static void setPayListener(HTPayListener listener) 
	{
		HTChannelDispatcher.getInstance().setPayListener(listener);
	}
	
	/**
	 * 设置退出监听
	 * @param listener 退出监听
	 * */
	public static void setExitListener(HTExitListener listener) 
	{
		HTChannelDispatcher.getInstance().setExitListener(listener);
	}
	
	/**
	 * 设置更新监听
	 * @param listener 更新监听
	 * */
	public static void setAppUpdateListener(HTAppUpdateListener listener) 
	{
		HTDataCenter.getInstance().mAppUpdateListener = listener;
	}
}
