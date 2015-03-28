package com.heitao.request;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.heitao.channel.HTChannelDispatcher;
import com.heitao.common.HTConsts;
import com.heitao.common.HTDataCenter;
import com.heitao.common.HTDevice;
import com.heitao.common.HTLog;
import com.heitao.common.HTUtils;
import com.heitao.listener.HTRequestListener;
import com.heitao.model.HTSDKInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class HTBaseRequest 
{
	protected Context mContext = HTDataCenter.getInstance().mContext;
	
	private ProgressDialog mProgressDialog = null;
	
	/**
	 * 显示Loading框
	 * @param string 提示文本
	 * */
	protected void doShowProgressDialog(final String string)
	{
		this.doCancelProgressDialog();
		
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				mProgressDialog = new ProgressDialog(mContext);
				mProgressDialog.setMessage(string);
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.setCancelable(false);
				mProgressDialog.show();
			}
		});
	}
	
	/**
	 * 关闭Loading框
	 * */
	protected void doCancelProgressDialog()
	{
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() 
		{
			@Override
			public void run() 
			{			
				if (mProgressDialog != null) 
				{
					mProgressDialog.cancel();
					mProgressDialog.hide();
					mProgressDialog = null;
				}
			}
		});
	}
	
	/**
	 * 添加公共参数
	 * @param map 原map
	 * @return 添加公共参数后的map
	 * */
	protected Map<String, String> addPublicParsMap(Map<String, String> map) 
	{
		Map<String, String> verifyMap = map;
		if (verifyMap == null) 
		{
			verifyMap = new HashMap<String, String>();
		}
		//	添加公共参数
		Context context = HTDataCenter.getInstance().mContext;
		try 
		{
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			
			verifyMap.put("vcode", packageInfo.versionCode + "");
			verifyMap.put("vname", packageInfo.versionName);				
		} 
		catch (NameNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			HTLog.e("添加version code/name 参数异常");
		}
		
		//	SDK版本号&渠道版本号
		verifyMap.put("vcc", HTChannelDispatcher.getInstance().getChannelSDKVersion());
		verifyMap.put("vhc", HTConsts.HTSDK_VERSION);
		verifyMap.put("os_platform", HTConsts.HTSDK_PLATFROM);
		
		verifyMap.put(HTConsts.KEY_UDID, HTDevice.getDeviceId());
		
		HTSDKInfo sdkInfo = HTDataCenter.getInstance().mSDKInfo;
		if (sdkInfo != null) 
		{
			verifyMap.put(HTConsts.KEY_CUSTOM_CHANNEL_ID, HTDataCenter.getInstance().mSDKInfo.customChannelId);
		}
		
		return verifyMap;
	}
	
	/**
	 * 创建get请求
	 * @param url 请求URL
	 * */
	protected void get(String url, final HTRequestListener listener) 
	{
		if (HTUtils.isNullOrEmpty(url)) 
		{
			HTLog.e("get请求URL为空");
			return;
		}
		
		if (listener == null) 
		{
			HTLog.e("get请求监听为空");
		}
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(HTConsts.REQUEST_TIME_OUT * 1000);
		client.get(url, new AsyncHttpResponseHandler() 
		{
			@Override
			public void onSuccess(int code, Header[] header, byte[] response) 
			{
				// TODO Auto-generated method stub
				if (listener != null) 
				{
					listener.onSuccess(new String(response));
				}
			}
			
			@Override
			public void onFailure(int code, Header[] header, byte[] response, Throwable error) 
			{
				// TODO Auto-generated method stub
				if (listener != null) 
				{
					listener.onFailure(error);
				}
			}
		});
	}
}
