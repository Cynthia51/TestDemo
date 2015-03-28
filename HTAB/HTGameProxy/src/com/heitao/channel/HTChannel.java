package com.heitao.channel;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.heitao.common.HTConsts;
import com.heitao.common.HTDataCenter;
import com.heitao.common.HTLog;
import com.heitao.common.HTUtils;
import com.heitao.listener.HTPayOrderListener;
import com.heitao.model.HTError;
import com.heitao.model.HTPayResult;
import com.heitao.model.HTGameInfo.HTDirection;
import com.heitao.model.HTPayInfo;
import com.heitao.platform.api.HTPlatform;
import com.heitao.platform.api.HTPlatform.HTPlatformDirection;
import com.heitao.platform.listener.HTPLoginListener;
import com.heitao.platform.listener.HTPPayListener;
import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPPayInfo;
import com.heitao.platform.model.HTPUser;
import com.heitao.request.HTPayOrderNumber;

public class HTChannel extends HTBaseChannel
{
	/*自定义变量----开始*/
	
	/*自定义变量----结束*/
	
	/*自定义方法----开始*/
	
	/*自定义方法----结束*/
	
	/*Activity部分生命周期方法----开始*/
	
	public void onCreate(Context context) 
	{
		super.onCreate(context);
		
		HTPlatform.getInstance().init(mContext, 
				HTDataCenter.getInstance().mGameInfo.direction == HTDirection.Portrait ? HTPlatformDirection.Portrait : HTPlatformDirection.Landscape, 
						mSDKInfo.appKey, 
						mSDKInfo.secretKey, 
						mSDKInfo.customChannelId);
		HTPlatform.setDebugEnable(HTConsts.HTSDK_DEBUG);
		HTPlatform.setLogEnable(HTConsts.HTSDK_DEBUG);
	}
	
	public void onPause() 
	{
		super.onPause();
	}
	
	public void onResume() 
	{
		super.onResume();
	}
	
	public void onStop() 
	{
		super.onStop();
	}
	
	public void onDestroy() 
	{
		super.onDestroy();
	}
	
	public void onRestart() 
	{
		super.onRestart();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/*Activity部分生命周期方法----结束*/
	
	/**
	 * 获取渠道KEY
	 * @return 渠道KEY
	 * */
	public String getChannelKey() 
	{
		return "hta";
	}
	
	/**
	 * 获取渠道SDK版本
	 * @return 版本号
	 * */
	public String getChannelSDKVersion() 
	{
		return "2.0.1";
	}
	
	/**
	 * 登录
	 * */
	public void doLogin(Map<String, String> customParsMap) 
	{
		super.doLogin(customParsMap);
		
		HTPlatform.getInstance().doLogin(new HTPLoginListener() 
		{
			@Override
			public void onLoginFailed(HTPError error) 
			{
				// TODO Auto-generated method stub
				doLogoutFailed(HTError.getLoginFailError());
			}
			
			@Override
			public void onLoginCompleted(HTPUser user) 
			{
				// TODO Auto-generated method stub
				
				Map<String, String> verifyMap = new HashMap<String, String>();
				verifyMap.put("uid", user.userId);
				verifyMap.put("token", user.token);
				
				doLoginCompleted(verifyMap, null, true);
			}
		});
		System.out.println("onCreate");
	}
	
	/**
	 * 登出
	 * */
	public void doLogout(Map<String, String> customParsMap) 
	{
		super.doLogout(customParsMap);
		
		doLogoutCompleted(null);
		doLogin(null);
	}
	
	/**
	 * 支付
	 * */
	public void doPay(final HTPayInfo payInfo) 
	{
		super.doPay(payInfo);
		
		if (HTConsts.HTSDK_DEBUG) 
		{
			HTUtils.doShowToast("当前为Debug模式，选择金额为" + mPayInfo.price * mPayInfo.count + "元，" + "实际充值金额为" + HTConsts.PAY_DEBUG_MONEY + "元。");
			mPayInfo.price = HTConsts.PAY_DEBUG_MONEY;
			mPayInfo.count = 1;
		}
		
		final Map<String, String> parsMap = new HashMap<String, String>();
		parsMap.put("psid", mServerId == null ? "" : mServerId);
		parsMap.put("uid", mUser.userId);
		parsMap.put("amount", mPayInfo.price * mPayInfo.count + "");
		parsMap.put("extinfo", payInfo.cpExtendInfo);
		parsMap.put("psname", mServerName);
		parsMap.put("rolename", mRoleName);
		parsMap.put("level", mRoleLevel + "");
		parsMap.put("roleid", mRoleId);
		
		doRunnableOnMainLooper(new Runnable() 
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				HTPayOrderNumber payOrderNumber = new HTPayOrderNumber();
				payOrderNumber.doCreatePayOrderNumber(parsMap, new HTPayOrderListener() 
				{
					@Override
					public void onHTPayOrderNumberFailed(HTError error) 
					{
						// TODO Auto-generated method stub
						
						mCustomOrderNumber = null;
						mCustomServerId = null;
						mCustomOrderData = null;
						
						if (error != null && !HTUtils.isNullOrEmpty(error.message)) 
						{
							HTUtils.doShowToast(error.message);
						}
					}
					
					@Override
					public void onHTPayOrderNumberCompleted(String orderNumber, String customServerId, JSONObject customData) 
					{
						// TODO Auto-generated method stub
						mCustomOrderNumber = orderNumber;
						mCustomServerId = customServerId;
						mCustomOrderData = customData;
						
						HTPPayInfo payInfo = new HTPPayInfo();
						payInfo.setAmount((int) (mPayInfo.price * mPayInfo.count) + "");
						payInfo.setName(mPayInfo.name);
						payInfo.setServerId(mServerId);
						payInfo.setChannelId(mSDKInfo.extendKey);
						payInfo.setOrderId(mCustomOrderNumber);
						payInfo.setDescription(mPayInfo.description);
						payInfo.setExtendInfo(mCustomOrderNumber);
						
						HTPlatform.getInstance().doPay(payInfo, new HTPPayListener() 
						{
							@Override
							public void onPayFailed(HTPError error) 
							{
								// TODO Auto-generated method stub
								HTLog.e("支付失败，" + error.toString());
								doPayFailed(HTError.getPayFailError());
							}
							
							@Override
							public void onPayCompleted() 
							{
								// TODO Auto-generated method stub
								HTLog.d("支付成功");
								doPayCompleted(HTPayResult.getPayCompleteRestlt(null));
							}
						});
					}
				});
			}
		});
	}
	
	/**
	 * 退出
	 * */
	public void doExit() 
	{
		super.doExit();
		
		doGameExit();
	}
	
	/**
	 * 显示悬浮按钮
	 * */
	protected void doCreateFuntionMenu(boolean isShow) 
	{
		
	}
	
	/**
	 * 游戏登录验证成功
	 * */
	public void onEnterGame(Map<String, String> customParsMap) 
	{
		super.onEnterGame(customParsMap);
		
		//	统计
		Map<String, String> map = new HashMap<String, String>();
		map.put("sid", HTUtils.isNullOrEmpty(mServerId) ? "" : mServerId);
		map.put("sname", HTUtils.isNullOrEmpty(mServerName) ? "" : mServerName);
		map.put("roleid", HTUtils.isNullOrEmpty(mRoleId) ? "" : mRoleId);
		map.put("rolename", HTUtils.isNullOrEmpty(mRoleName) ? "" : mRoleName);
		map.put("rolelevel", mRoleLevel + "");
		map.put("isnew", HTUtils.isNullOrEmpty(mIsNewRole) ? "" : mIsNewRole);
		map.put("uid", mUser.platformUserId);
		
		HTPlatform.statistics("stat/role", map);
	}
	
	/**
	 * 游戏等级发生变化
	 * */
	public void onGameLevelChanged(int newLevel) 
	{
		super.onGameLevelChanged(newLevel);
	}
}
