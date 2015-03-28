package com.heitao.platform.request;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.heitao.platform.common.HTPConsts;
import com.heitao.platform.common.HTPDataCenter;
import com.heitao.platform.common.HTPLog;
import com.heitao.platform.common.HTPUtils;
import com.heitao.platform.listener.HTPRequestListener;
import com.heitao.platform.model.HTPError;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HTPHttpRequest 
{
	protected HTPRequestListener mListener = null;
	private boolean mIsAddPubPars = true;
	
	public void get(String url, Map<String, String> map, boolean isAddPubPars, final HTPRequestListener listener) 
	{
		mIsAddPubPars = isAddPubPars;
		this.get(url, map, listener);
	}
	
	public void get(String url, Map<String, String> map, final HTPRequestListener listener) 
	{
		mListener = listener;
		
		if (HTPUtils.isNullOrEmpty(url)) 
		{
			HTPLog.e("get请求URL为空");
			return;
		}
		
		if (mListener == null) 
		{
			HTPLog.e("get请求监听为空");
		}
		
		if (mIsAddPubPars) 
		{
			map = addPublicParameter(map);
		}
		
		String requestURL = url + "?" + HTPUtils.mapToParsString(map, true);
		
		HTPLog.d("requestURL=" + requestURL);
		HTPLog.d("map=" + map);
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(HTPConsts.REQUEST_TIME_OUT * 1000);
		client.get(requestURL, new AsyncHttpResponseHandler() 
		{
			@Override
			public void onSuccess(int code, Header[] header, byte[] response) 
			{
				// TODO Auto-generated method stub
				HTPLog.d("get response=" + new String(response));
				
				JSONObject jsonObject = null;
				try 
				{
					jsonObject = new JSONObject(new String(response));
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					jsonObject = null;
				}
				
				if (jsonObject != null) 
				{
					onParse(jsonObject);
				}
				else 
				{
					if (mListener != null) 
					{
						mListener.onFailed(HTPError.getRequestParseError());
					}
				}
			}
			
			@Override
			public void onFailure(int code, Header[] header, byte[] response, Throwable error) 
			{
				// TODO Auto-generated method stub
				if (mListener != null) 
				{
					mListener.onFailed(HTPError.getRequestFailedError());
				}
			}
		});
	}
	
	public void post(String url, Map<String, String> map, boolean isAddPubPars, final HTPRequestListener listener) 
	{
		mIsAddPubPars = isAddPubPars;
		this.post(url, map, listener);
	}
	
	public void post(String url, Map<String, String> map, final HTPRequestListener listener) 
	{
		mListener = listener;
		
		if (HTPUtils.isNullOrEmpty(url)) 
		{
			HTPLog.e("post请求URL为空");
			return;
		}
		
		if (mListener == null) 
		{
			HTPLog.e("post请求监听为空");
		}
		
		if (mIsAddPubPars) 
		{
			map = addPublicParameter(map);
		}
		
		HTPLog.d("requestURL=" + url);
		HTPLog.d("map=" + map);
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(HTPConsts.REQUEST_TIME_OUT * 1000);
		client.post(url, new RequestParams(map), new AsyncHttpResponseHandler() 
		{
			@Override
			public void onSuccess(int code, Header[] header, byte[] response) 
			{
				// TODO Auto-generated method stub
				HTPLog.d("post response=" + new String(response));
				
				JSONObject jsonObject = null;
				try 
				{
					jsonObject = new JSONObject(new String(response));
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					jsonObject = null;
				}
				
				if (jsonObject != null) 
				{
					onParse(jsonObject);
				}
				else 
				{
					if (mListener != null) 
					{
						mListener.onFailed(HTPError.getRequestParseError());
					}
				}
			}
			
			@Override
			public void onFailure(int code, Header[] header, byte[] response, Throwable error) 
			{
				// TODO Auto-generated method stub
				if (mListener != null) 
				{
					mListener.onFailed(HTPError.getRequestFailedError());
				}
			}
		});
	}
	
	private Map<String, String> addPublicParameter(Map<String, String> map) 
	{
		if (map == null) 
		{
			map = new HashMap<String, String>();
		}
		
		//添加必填公共参数
		String time = System.currentTimeMillis() + "";
		
		map.put("appkey", HTPDataCenter.getInstance().mAppKey);
		map.put("time", time);
		map.put("token", HTPUtils.genTokenFromTimestamp(time));
		map.put("platform", HTPConsts.PLATFORM_ID);
		map.put("udid", HTPUtils.getUDID());
		map.put("chid", HTPDataCenter.getInstance().mChannelId);
		map.put("sdkversion", HTPConsts.HTPlatformSDK_VERSION);
		map.put("imei", HTPUtils.getDeviceId());
		
		return map;
	}
	
	protected void onParse(JSONObject jsonObject) 
	{
		HTPLog.e("子类必须重写该方法");
	}
}
