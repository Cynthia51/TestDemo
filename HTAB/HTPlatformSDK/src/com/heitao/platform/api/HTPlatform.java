package com.heitao.platform.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.heitao.platform.activity.HTPFirstLoginActivity;
import com.heitao.platform.activity.HTPLoginActivity;
import com.heitao.platform.activity.HTPPayActivity;
import com.heitao.platform.common.HTPConsts;
import com.heitao.platform.common.HTPDBHelper;
import com.heitao.platform.common.HTPDataCenter;
import com.heitao.platform.common.HTPLog;
import com.heitao.platform.common.HTPUtils;
import com.heitao.platform.listener.HTPLoginListener;
import com.heitao.platform.listener.HTPPayListener;
import com.heitao.platform.listener.HTPRequestListener;
import com.heitao.platform.model.HTPDBUser;
import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPHttpEntity;
import com.heitao.platform.model.HTPPayInfo;
import com.heitao.platform.openudid.HTPOpenUDID_manager;
import com.heitao.platform.pay.center.HTPSDKPayCenter;
import com.heitao.platform.request.HTPNullResponeParser;
import com.heitao.platform.statistics.HTPStatistics;

import android.content.Context;
import android.content.Intent;

public class HTPlatform 
{
	public enum HTPlatformDirection
	{
		Landscape,								//横屏
		Portrait								//竖屏
	}
	
	private static HTPlatform mInstance = null;
	
	private Context mContext = null;
	
	/**
	 * 获取实例
	 * */
	public static HTPlatform getInstance() 
	{
		if (mInstance == null) 
		{
			mInstance = new HTPlatform();
		}
		
		return mInstance;
	}
	
	/**
	 * 初始化
	 * @param context context
	 * @param direction 屏幕方向
	 * @param appKey appKey
	 * @param secretKey secretKey
	 * */
	public void init(Context context, HTPlatformDirection direction, String appKey, String secretKey) 
	{
		mContext = context;
		this.initOpenUDID();
		
		HTPDataCenter.getInstance().mContext = context;
		HTPDataCenter.getInstance().mDirection = direction;
		HTPDataCenter.getInstance().mAppKey = appKey;
		HTPDataCenter.getInstance().mSecretKey = secretKey;
		
		HTPSDKPayCenter.getInstance().init(context);
		
		this.active();
	}
	
	/**
	 * 初始化
	 * @param context context
	 * @param direction 屏幕方向
	 * @param appKey appKey
	 * @param secretKey secretKey
	 * @param channelId 渠道ID
	 * */
	public void init(Context context, HTPlatformDirection direction, String appKey, String secretKey, String channelId) 
	{
		mContext = context;
		this.initOpenUDID();
		
		HTPDataCenter.getInstance().mContext = context;
		HTPDataCenter.getInstance().mDirection = direction;
		HTPDataCenter.getInstance().mAppKey = appKey;
		HTPDataCenter.getInstance().mSecretKey = secretKey;
		HTPDataCenter.getInstance().mChannelId = channelId;
		
		HTPSDKPayCenter.getInstance().init(context);
		
		this.active();
	}
	
	/**
	 * 登录
	 * @param listener 登录监听
	 * */
	public void doLogin(HTPLoginListener listener) 
	{
		HTPDataCenter.getInstance().mLoginListener = listener;
		
		HTPUtils.doRunnableOnMainLooper(mContext, new Runnable() 
		{	
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				List<HTPDBUser> list = HTPDBHelper.getInstance().query();
				if (list == null || list.size() == 0) 
				{
					Intent intent = new Intent(mContext, HTPFirstLoginActivity.class);
					mContext.startActivity(intent);
				} 
				else 
				{
					Intent intent = new Intent(mContext, HTPLoginActivity.class);
					mContext.startActivity(intent);
				}
			}
		});
	}
	
	/**
	 * 登出
	 * */
	public void doLogout()
	{
		HTPDataCenter.getInstance().reset();
	}
	
	/**
	 * 支付
	 * @param listener 支付监听
	 * */
	public void doPay(HTPPayInfo info, HTPPayListener listener) 
	{
		if (HTPDataCenter.getInstance().mUser == null) 
		{
			HTPLog.e("未登录状态不能进行支付");
			
			if (listener != null) 
			{
				listener.onPayFailed(HTPError.getNotLoginError());
				return;
			}
		}
		
		if (info == null) 
		{
			HTPLog.e("支付信息为空");
			if (listener != null) 
			{
				listener.onPayFailed(HTPError.getPayInfoError());
			}
			return;
		}
		
		HTPUtils.doShowProgressDialog(mContext, "初始化支付中···");
		
		HTPDataCenter.getInstance().mPayInfo = info;
		HTPDataCenter.getInstance().mPayListener = listener;
		
		HTPUtils.doRunnableOnMainLooper(mContext, new Runnable() 
		{	
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				
				String pub_key = HTPDataCenter.getInstance().mAppKey;
				String time = System.currentTimeMillis() + "";
				String token = HTPUtils.getMD5(time + HTPDataCenter.getInstance().mAppKey);
				
				Map<String, String> map = new HashMap<String, String>();
				map.put("pub_key", pub_key);
				map.put("time", time);
				map.put("token", token);
				
				new HTPNullResponeParser().get(HTPConsts.PAY_CONTROL_URL, map, false, new HTPRequestListener() 
				{
					@Override
					public void onFailed(HTPError error) 
					{
						// TODO Auto-generated method stub
						HTPLog.i("index error," + error.toString());
						HTPUtils.doCancelProgressDialog(mContext);
						HTPUtils.doShowToast(error != null ? error.message : "初始化支付失败");
						
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								HTPUtils.doShowToast("网络异常，请检查网络后重试");
							}
						});
					}
					
					@Override
					public void onCompleted(final HTPHttpEntity httpEntity) 
					{
						// TODO Auto-generated method stub
						HTPLog.i("index complete");
						HTPUtils.doCancelProgressDialog(mContext);
						
						if (httpEntity.isCompleted) 
						{
							Intent intent = new Intent(mContext, HTPPayActivity.class);
							intent.putExtra("data", httpEntity.dataObject.toString());
							mContext.startActivity(intent);
						}
						else 
						{
							HTPUtils.doShowToast("初始化失败");
						}
					}
				});
			}
		});
	}
	
	/**
	 * 设置开启打印日志
	 * @param isEnable 是否开启
	 * */
	public static void setLogEnable(boolean isEnable) 
	{
		HTPLog.mLogEnable = isEnable;
	}
	
	/**
	 * 设置打开调试模式
	 * @param isEnable 是否开启
	 * */
	public static void setDebugEnable(boolean isEnable) 
	{
		HTPConsts.HTPlatformSDK_DEBUG = isEnable;
	}
	
	/**
	 * 统计
	 * @param label 标签
	 * @param map 统计数据
	 * */
	public static void statistics(String label, Map<String, String> map) 
	{
		HTPStatistics.statistics(label, map);
	}
	
	private void initOpenUDID() 
	{
		HTPOpenUDID_manager.sync(mContext);
		HTPOpenUDID_manager.isInitialized();
	}
	
	private void active() 
	{
		HTPUtils.doRunnableOnMainLooper(mContext, new Runnable() 
		{	
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				
				String url = HTPConsts.API_URL + "stat/active";
				new HTPNullResponeParser().post(url, HTPUtils.getDevicesInfo(mContext), new HTPRequestListener() 
				{
					@Override
					public void onFailed(HTPError error) 
					{
						// TODO Auto-generated method stub
						HTPLog.i("active error," + error.toString());
					}
					
					@Override
					public void onCompleted(HTPHttpEntity httpEntity) 
					{
						// TODO Auto-generated method stub
						HTPLog.i("active complete");
					}
				});
			}
		});
	}
}
