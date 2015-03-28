package com.heitao.channel;

import android.content.Context;
import android.content.Intent;

import com.heitao.listener.HTExitListener;
import com.heitao.listener.HTLoginListener;
import com.heitao.listener.HTLogoutListener;
import com.heitao.listener.HTPayListener;
import com.heitao.common.HTUtils;
import com.heitao.model.HTPayInfo;
import com.heitao.openudid.HTOpenUDID_manager;

public class HTChannelDispatcher 
{
	private static HTChannelDispatcher mInstance = null;
	
	protected HTBaseChannel mChannel = null;
	protected Context mContext = null;
	
	private HTChannelDispatcher()
	{
		mChannel = new HTChannel();
	}
	
	public static HTChannelDispatcher getInstance() 
	{
		if (mInstance == null) 
		{
			mInstance = new HTChannelDispatcher();
		}
		
		return mInstance;
	}
	
	/**
	 * 获取当前渠道标识key
	 * */
	public String getChannelKey() 
	{
		return mChannel.getChannelKey();
	}
	
	/**
	 * 获取渠道SDK版本
	 * @return 版本号
	 * */
	public String getChannelSDKVersion() 
	{
		return mChannel.getChannelSDKVersion();
	}
	
	/*Activity部分生命周期方法----开始*/
	
	public void onCreate(Context context) 
	{
		mContext = context;
		
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				HTOpenUDID_manager.sync(mContext);
				HTOpenUDID_manager.isInitialized();
				mChannel.onCreate(mContext);
			}
		});
	}
	
	public void onPause() 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				mChannel.onPause();
			}
		});
	}
	
	public void onResume() 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				mChannel.onResume();
			}
		});
	}
	
	public void onStop() 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				mChannel.onStop();
			}
		});
	}
	
	public void onDestroy() 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				mChannel.onDestroy();
			}
		});
	}
	
	public void onRestart() 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				mChannel.onRestart();
			}
		});
	}
	
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				mChannel.onActivityResult(requestCode, resultCode, data);
			}
		});
	}
	
	/*Activity部分生命周期方法----结束*/
	
	/**
	 * 登录
	 * */
	public void doLogin(final String customParameters, final HTLoginListener listener) 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				if (listener != null) 
				{
					mChannel.mLoginListener = listener;
				}
				mChannel.doLogin(HTUtils.parsStringToMap(customParameters, true));
			}
		});
	}
	
	/**
	 * 登出
	 * */
	public void doLogout(final String customParameters, final HTLogoutListener listener) 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				if (listener != null) 
				{
					mChannel.mLogoutListener = listener;
				}
				mChannel.doLogout(HTUtils.parsStringToMap(customParameters, true));
			}
		});
	}
	
	/**
	 * 支付
	 * */
	public void doPay(final String customParameters, final HTPayListener listener) 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				if (listener != null) 
				{
					mChannel.mPayListener = listener;
				}
				mChannel.doPay(customParameters == null ? null : (new HTPayInfo(customParameters)));
			}
		});
	}
	
	/**
	 * 退出
	 * */
	public void doExit(final HTExitListener listener) 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				if (listener != null) 
				{
					mChannel.mExitListener = listener;
				}
				mChannel.doExit();
			}
		});
	}
	
	/**
	 * 显示悬浮按钮
	 * @param show 是否显示
	 * */
	public void setShowFunctionMenu(final boolean show) 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				mChannel.setShowFunctionMenu(show);
			}
		});
	}
	
	/**
	 * 开始游戏
	 * */
	public boolean onStartGame() 
	{
		return mChannel.onStartGame();
	}
	
	/**
	 * 进入游戏
	 * */
	public void onEnterGame(final String infos) 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				mChannel.onEnterGame(HTUtils.parsStringToMap(infos, true));
			}
		});
	}
	
	/**
	 * 游戏等级发生变化
	 * */
	public void onGameLevelChanged(final int newLevel) 
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				mChannel.onGameLevelChanged(newLevel);
			}
		});
	}
	
	/**
	 * 设置登录监听
	 * */
	public void setLogoinListener(HTLoginListener listener) 
	{
		mChannel.mLoginListener = listener;
	}
	
	/**
	 * 设置登出监听
	 * */
	public void setLogoutListener(HTLogoutListener listener) 
	{
		mChannel.mLogoutListener = listener;
	}
	
	/**
	 * 设置支付监听
	 * */
	public void setPayListener(HTPayListener listener) 
	{
		mChannel.mPayListener = listener;
	}
	
	/**
	 * 设置退出监听
	 * */
	public void setExitListener(HTExitListener listener) 
	{
		mChannel.mExitListener = listener;
	}
}
