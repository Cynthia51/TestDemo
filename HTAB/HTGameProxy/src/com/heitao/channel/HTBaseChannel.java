package com.heitao.channel;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.heitao.common.HTConsts;
import com.heitao.common.HTDataCenter;
import com.heitao.common.HTKeys;
import com.heitao.common.HTLog;
import com.heitao.common.HTMapHelper;
import com.heitao.common.HTUtils;
import com.heitao.model.HTPayInfo;
import com.heitao.model.HTSDKInfo;
import com.heitao.request.HTSDKInfoReader;
import com.heitao.request.HTStatistics;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class HTBaseChannel extends HTChannelDispose
{
	protected Context mContext = null;
	
	/**登录参数map*/
	protected Map<String, String> mLoginCustomMap = null;
	/**登出参数map*/
	protected Map<String, String> mLogoutCustomMap = null;
	/**支付信息*/
	protected HTPayInfo mPayInfo = null;
	/**是否显示悬浮功能按钮*/
	protected boolean mIsShowFunctionMenu = true;
	/**角色等级*/
	protected int mRoleLevel;
	/**角色ID*/
	protected String mRoleId;
	/**是否新角色*/
	protected String mIsNewRole;
	/**服务器ID*/
	protected String mServerId = null;
	/**服务器名称*/
	protected String mServerName = null;
	/**角色名称*/
	protected String mRoleName = null;
	/**进入游戏参数map*/
	protected Map<String, String> mEnterGameParsMap;
	/**登录状态*/
	protected boolean mIsLogined = false;
	/**SDK参数信息*/
	protected HTSDKInfo mSDKInfo = null;
	
	/*Activity部分生命周期方法----开始*/
	
	public void onCreate(Context context) 
	{
		mContext = context;
		HTLog.d("onCreate=>");
		
		HTSDKInfoReader sdInfoReader = new HTSDKInfoReader();
		mSDKInfo = sdInfoReader.getSDKInfoFromFile();
		
		if (mSDKInfo == null) 
		{
			HTLog.w("读取SDK配置信息失败，如果配置信息位于manifest文件请忽略，否则将导致程序无法正常运行");
		}
		else 
		{
			HTDataCenter.getInstance().mSDKInfo = mSDKInfo;
			HTLog.d("SDKInfo=" + mSDKInfo.toString());
		}
		
		try 
		{
			ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
			String joinModel = appInfo.metaData.getString(HTConsts.KEY_JOIN_MODEL);
			if (!HTUtils.isNullOrEmpty(joinModel.trim())) 
			{
				if (joinModel.trim().equals("origin")) 
				{
					mSDKJoinModel = HTSDKJoinModel.orinigModel;
				}
				else if (joinModel.trim().equals("native")) 
				{
					mSDKJoinModel = HTSDKJoinModel.nativeModel;
				}
				else if (joinModel.trim().equals("origin|native")) 
				{
					mSDKJoinModel = HTSDKJoinModel.bothModel;
				}
			}
			
			HTLog.d("接入模式=" + joinModel);
		} 
		catch (NameNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			HTLog.e("解析接入模式异常，error=" + e.getMessage());
			mSDKJoinModel = HTSDKJoinModel.orinigModel;
		}
	}
	
	public void onPause() 
	{
		HTLog.d("onPause=>");
	}
	
	public void onResume() 
	{
		HTLog.d("onResume=>");
	}
	
	public void onStop() 
	{
		HTLog.d("onStop=>");
	}
	
	public void onDestroy() 
	{
		HTLog.d("onDestroy=>");
	}
	
	public void onRestart() 
	{
		HTLog.d("onRestart=>");
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		HTLog.d("onActivityResult=> requestCode=" + requestCode + "	resultCode=" + resultCode + " data=" + data);
	}
	
	/*Activity部分生命周期方法----结束*/
	
	/**
	 * 获取渠道KEY
	 * @return 渠道KEY
	 * */
	public String getChannelKey() 
	{
		HTLog.d("getChannelKey=>");
		return "base";
	}
	
	/**
	 * 获取渠道SDK版本
	 * @return 版本号
	 * */
	public String getChannelSDKVersion() 
	{
		HTLog.d("getChannelSDKVersion=>");
		return "base";
	}
	
	/**
	 * 登录
	 * */
	public void doLogin(Map<String, String> customParsMap) 
	{
		HTLog.d("doLogin=>");
		if (customParsMap != null) 
		{
			mLoginCustomMap = customParsMap;
		}
	}
	
	/**
	 * 登出
	 * */
	public void doLogout(Map<String, String> customParsMap) 
	{
		HTLog.d("doLogout=>");
		if (customParsMap != null) 
		{
			mLogoutCustomMap = customParsMap;
		}
		
		mIsLogined = false;
		this.doClear();
	}
	
	/**
	 * 支付
	 * */
	public void doPay(HTPayInfo payInfo) 
	{
		if (payInfo != null) 
		{
			mPayInfo = payInfo;
			
			//	设置默认回调地址
			if (HTUtils.isNullOrEmpty(mPayInfo.callbackUrl)) 
			{
				mPayInfo.callbackUrl = HTConsts.BASE_URL + HTDataCenter.getInstance().mGameInfo.shortName + "/pay/" + this.getChannelKey();
			}
			
			if (mPayInfo.rate <= 0) 
			{
				mPayInfo.rate = 10;
			}
			
			if (HTUtils.isNullOrEmpty(mPayInfo.productId)) 
			{
				mPayInfo.productId = HTDataCenter.getInstance().mGameInfo.shortName + "_" + (int) (mPayInfo.price * mPayInfo.count);
			}
			
			if (HTUtils.isNullOrEmpty(mPayInfo.name)) 
			{
				boolean isMonthPay = false;
				try 
				{
					JSONObject customJsonObject = new JSONObject(mPayInfo.custom);
					isMonthPay = customJsonObject.getInt(HTKeys.KEY_IS_PAY_MONTH) == 1 ? true : false;
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					isMonthPay = false;
				}
				
				if (isMonthPay) 
				{
					mPayInfo.name = (int) (mPayInfo.price * mPayInfo.count) + "元月卡";
				}
				else 
				{
					mPayInfo.name = (int) (mPayInfo.price * mPayInfo.count * mPayInfo.rate) + mPayInfo.unitName;
				}
			}
			
			if (HTUtils.isNullOrEmpty(mPayInfo.description)) 
			{
				mPayInfo.description = "购买" + (int) (mPayInfo.price * mPayInfo.count * mPayInfo.rate) + mPayInfo.unitName;
			}
		}
		
		HTLog.d("doPay=>" + "	payInfo=" + payInfo.toString());
	}
	
	/**
	 * 退出
	 * */
	public void doExit() 
	{
		HTLog.d("doExit=>");
	}
	
	/**
	 * 显示悬浮按钮
	 * @param show 是否显示
	 * */
	public void setShowFunctionMenu(boolean show) 
	{
		HTLog.d("setShowFunctionMenu=>" + "		show=" + show);
		mIsShowFunctionMenu = show;
		
		if (mIsLogined) 
		{
			this.doCreateFuntionMenu(mIsShowFunctionMenu);
		}
	}
	
	/**
	 * 显示悬浮按钮
	 * */
	protected void doCreateFuntionMenu(boolean isShow) 
	{
		//	子类重写
	}
	
	/**
	 * 进入游戏
	 * */
	public void onEnterGame(Map<String, String> customParsMap) 
	{
		HTLog.d("onEnterGame=>" + (customParsMap != null ? customParsMap.toString() : ""));
		mEnterGameParsMap = customParsMap;
		
		if (mEnterGameParsMap != null) 
		{
			mServerId = HTMapHelper.getString(mEnterGameParsMap, HTKeys.KEY_CP_SERVER_ID);
			mServerName = HTMapHelper.getString(mEnterGameParsMap, HTKeys.KEY_CP_SERVER_NAME);
			mRoleId = HTMapHelper.getString(mEnterGameParsMap, HTKeys.KEY_ROLE_ID);
			mRoleName = HTMapHelper.getString(mEnterGameParsMap, HTKeys.KEY_ROLE_NAME);
			mRoleLevel = HTMapHelper.getInt(mEnterGameParsMap, HTKeys.KEY_ROLE_LEVEL);
			mIsNewRole = HTMapHelper.getString(mEnterGameParsMap, HTKeys.KEY_IS_NEW_ROLE);
		}
		
		mIsLogined = true;
		if (mIsShowFunctionMenu) 
		{
			//	显示悬浮按钮
			this.doCreateFuntionMenu(true);
		}
		
		//	统计角色信息
		Map<String, String> parsMap = new HashMap<String, String>();
		parsMap.put("psid", mServerId);
		parsMap.put("psname", mServerName);
		parsMap.put("uid", mUser == null ? "" : mUser.userId);
		parsMap.put("puid", mUser == null ? "" : mUser.platformUserId);
		parsMap.put("rolename", mRoleName);
		parsMap.put("level", mRoleLevel + "");
		parsMap.put("roleid", mRoleId);
		parsMap.put("isnew", mIsNewRole);
		
		new HTStatistics().doStatisticsRoleInfo(parsMap);
	}
	
	/**
	 * 开始游戏
	 * */
	public boolean onStartGame() 
	{
		HTLog.d("onStartGame=>");
		
		if (mUser == null) 
		{
			doRunnableOnMainLooper(new Runnable() 
			{
				@Override
				public void run() 
				{
					// TODO Auto-generated method stub
					doLogin(null);
				}
			});
			return false;
		}
		
		return true;
	}
	
	/**
	 * 游戏等级发生变化
	 * */
	public void onGameLevelChanged(int newLevel) 
	{
		HTLog.d("onGameLevelChanged=>	newLevel=" + newLevel);
		mRoleLevel = newLevel;
	}
	
	/*SDK相关方法及成员变量----开始*/
	
	/**
	 * 在主线程上面执行Runnable
	 * */
	protected void doRunnableOnMainLooper(Runnable runnable)
	{
		HTUtils.doRunnableOnMainLooper(mContext, runnable);
	}
	
	/**
	 * 清理数据
	 * */
	protected void doClear() 
	{
		mUser = null;
		mSDKCustomMap = null;
		
		mLoginCustomMap = null;
		mLogoutCustomMap = null;
		mEnterGameParsMap = null;
		
		mPayInfo = null;
		
		mRoleId = null;
		mRoleLevel = 0;
		mRoleName = null;
		mIsNewRole = null;
		mServerId = null;
	}
	
	/*SDK相关方法及成员变量----结束*/
}
