package com.heitao.channel;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;

import com.heitao.common.HTDataCenter;
import com.heitao.common.HTKeys;
import com.heitao.common.HTLog;
import com.heitao.listener.HTExitListener;
import com.heitao.listener.HTLoginListener;
import com.heitao.common.HTUtils;
import com.heitao.listener.HTLoginVerifyListener;
import com.heitao.listener.HTLogoutListener;
import com.heitao.listener.HTPayListener;
import com.heitao.model.HTError;
import com.heitao.model.HTPayResult;
import com.heitao.model.HTUser;
import com.heitao.proxy.HTProxy;
import com.heitao.request.HTLoginVerify;

public class HTChannelDispose 
{
	/**SDK接入模式*/
	protected enum HTSDKJoinModel
	{
		orinigModel,	//源生模式(默认)
		nativeModel,	//C++模式
		bothModel		//混合模式
	}
	
	/**登录验证成功后用户*/
	protected HTUser mUser = null;
	/**登录验证成功后SDK自定义信息*/
	protected Map<String, String> mSDKCustomMap = null;
	/**登录listener*/
	public HTLoginListener mLoginListener = null;
	/**登出listener*/
	public HTLogoutListener mLogoutListener = null;
	/**支付listener*/
	public HTPayListener mPayListener = null;
	/**退出listener*/
	public HTExitListener mExitListener = null;
	/**自定义订单号*/
	protected String mCustomOrderNumber = null;
	/**自定义服务器ID*/
	protected String mCustomServerId = null;
	/**自定义生成订单号数据*/
	protected JSONObject mCustomOrderData = null;
	/**当前SDK接入模式(native/origin)*/
	protected HTSDKJoinModel mSDKJoinModel = HTSDKJoinModel.orinigModel;
	
	/**
	 * 登录成功
	 * @param verifyMap 验证参数
	 * @param customMap 自定义参数
	 * @param isVerify 是否验证
	 * */
	protected void doLoginCompleted(final Map<String, String> verifyMap, final Map<String, String> customMap, boolean isVerify)
	{
		if (isVerify) 
		{
			//	需要登录验证
			Context context = HTDataCenter.getInstance().mContext;
			HTUtils.doRunnableOnMainLooper(context, new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
                    
					HTLoginVerify loginVerify = new HTLoginVerify();
					loginVerify.doLoginVerify(verifyMap, new HTLoginVerifyListener() 
					{
						@Override
						public void onHTLoginVerifyCompleted(HTUser user, Map<String, String> sdkParsMap) 
						{
							// TODO Auto-generated method stub
							mUser = user;
							mSDKCustomMap = sdkParsMap;
							if (mUser == null) 
							{
								HTLog.e("登录验证成功用户信息为空");
							}
							
							//	origin
                            if (mLoginListener != null)
                            {
                                mLoginListener.onHTLoginCompleted(mUser, customMap);
                            }
                            else 
                            {
                            	HTLog.w("未设置登录监听");	
							}
                            
                            //	native
                            if (mSDKJoinModel == HTSDKJoinModel.nativeModel || mSDKJoinModel == HTSDKJoinModel.bothModel) 
                            {
                                mUser.custom = HTUtils.mapToParsString(customMap, true);
                                HTProxy.doNativeLoginCompleted(mUser == null ? "" : mUser.toEncodeString());
							}
						}
					});
				}
			});
		}
		else 
		{
			//	不需要登录验证
			mUser = new HTUser(customMap);
            
			//	origin
			if (mUser == null) 
			{
				HTLog.e("登录验证成功用户信息为空");
			}
			
			if (mLoginListener != null) 
			{
				mLoginListener.onHTLoginCompleted(mUser, customMap);
			}
			
            //	native
            if (mSDKJoinModel == HTSDKJoinModel.nativeModel || mSDKJoinModel == HTSDKJoinModel.bothModel) 
            {
            	mUser.custom = HTUtils.mapToParsString(customMap, true);
            	HTProxy.doNativeLoginCompleted(mUser == null ? "" : mUser.toEncodeString());
            }
		}
	}
	
	/**
	 * 登录失败
	 * @param error 错误
	 * */
	protected void doLoginFailed(HTError error)
	{
		//	origin
		if (mLoginListener != null) 
		{
			mLoginListener.onHTLoginFailed(error);
		}
		else 
		{
			HTLog.w("未设置登录监听");
		}
		
		//	native
        if (mSDKJoinModel == HTSDKJoinModel.nativeModel || mSDKJoinModel == HTSDKJoinModel.bothModel) 
        {
        	HTProxy.doNativeLoginFailed(error == null ? "" : error.toEncodeString());
        }
	}
	
	/**
	 * 注销成功
	 * @param customMap 自定义参数
	 * */
	protected void doLogoutCompleted(Map<String, String> customMap)
	{
		//	origin
		if (mLogoutListener != null) 
		{
			mLogoutListener.onHTLogoutCompleted(mUser, customMap);
		}
		else 
		{
			HTLog.w("未设置登出监听");
		}
		
		//	native
        if (mSDKJoinModel == HTSDKJoinModel.nativeModel || mSDKJoinModel == HTSDKJoinModel.bothModel) 
        {
        	HTProxy.doNativeLogoutCompleted(HTUtils.mapToParsString(customMap, true));
        }
	}
	
	/**
	 * 注销失败
	 * @param error 错误
	 * */
	protected void doLogoutFailed(HTError error)
	{
		//	origin
		if (mLogoutListener != null) 
		{
			mLogoutListener.onHTLogoutFailed(error);
		}
		else 
		{
			HTLog.w("未设置登出监听");
		}
		
		//	native
        if (mSDKJoinModel == HTSDKJoinModel.nativeModel || mSDKJoinModel == HTSDKJoinModel.bothModel) 
        {
        	HTProxy.doNativeLogoutFailed(error == null ? "" : error.toEncodeString());
        }
	}
	
	/**
	 * 支付成功
	 * @param result 支付结果
	 * */
	protected void doPayCompleted(HTPayResult result)
	{
		if (result != null) 
		{
			Map<String, String> map= new HashMap<String, String>();
			map.put(HTKeys.KEY_CUSTOM_ORDER_NUMBER, mCustomOrderNumber);
			map.put("custom_server_id", mCustomServerId);
			
			String custom = result.custom;
			if (HTUtils.isNullOrEmpty(custom)) 
			{
				custom = HTUtils.mapToParsString(map, false);
			}
			else 
			{
				custom += HTUtils.mapToParsString(map, false);
			}
			
			result.custom = custom;
		}
		
		//	origin
		if (mPayListener != null) 
		{
			mPayListener.onHTPayCompleted(result);
		}
		else 
		{
			HTLog.w("未设置支付监听");
		}
		
		//	native
        if (mSDKJoinModel == HTSDKJoinModel.nativeModel || mSDKJoinModel == HTSDKJoinModel.bothModel) 
        {
        	HTProxy.doNativePayCompleted(result == null ? "" : result.toEncodeString());
        }
	}
	
	/**
	 * 支付失败
	 * @param error 错误
	 * */
	protected void doPayFailed(HTError error)
	{
		//	origin
		if (mPayListener != null) 
		{
			mPayListener.onHTPayFailed(error);
		}
		else 
		{
			HTLog.w("未设置支付监听");	
		}
		
		//	native
        if (mSDKJoinModel == HTSDKJoinModel.nativeModel || mSDKJoinModel == HTSDKJoinModel.bothModel) 
        {
        	HTProxy.doNativePayFailed(error == null ? "" : error.toEncodeString());
        }
	}
	
	/**
	 * 游戏退出
	 * */
	protected void doGameExit()
	{
		//	origin
		if (mExitListener != null) 
		{
			mExitListener.onHTGameExit();
		}
		else 
		{
			HTLog.e("未设置退出监听");	
		}
		
		//	native
        if (mSDKJoinModel == HTSDKJoinModel.nativeModel || mSDKJoinModel == HTSDKJoinModel.bothModel) 
        {
        	HTProxy.doNativeGameExit();
        }
	}
	
	/**
	 * 第三方(SDK)退出
	 * */
	protected void doThirdPartyExit()
	{
		//	origin
		if (mExitListener != null) 
		{
			mExitListener.onHTThirdPartyExit();
		}
		else 
		{
			HTLog.w("未设置退出监听");	
		}
		
		//	native
        if (mSDKJoinModel == HTSDKJoinModel.nativeModel || mSDKJoinModel == HTSDKJoinModel.bothModel) 
        {
        	HTProxy.doNativeThirdPartyExit();
        }
	}
}
