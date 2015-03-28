package com.heitao.platform.common;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.heitao.platform.openudid.HTPOpenUDID_manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.widget.Toast;

public class HTPUtils 
{
	private static ProgressDialog mProgressDialog = null;

	/**
	 * 解析参数
	 * 
	 * @param data key1=value1&key2=value2
	 * @param isDecode 是否decode
	 * @return map Map<String, String>
	 * */
	public static HashMap<String, String> parsStringToMap(String data, boolean isDecode) 
	{
		if (data == null || data.length() <= 0) 
		{
			return null;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String keyValues[] = data.split("&");
			for (String string : keyValues) 
			{
				String str[] = string.split("=");
				if (str.length == 1) 
				{
					String key = str[0];
					map.put(key, "");
				}
				if (str.length == 2) 
				{
					String key = str[0];
					String value = str[1];
					map.put(key, HTPUtils.isNullOrEmpty(value) ? "" : (isDecode ? Uri.decode(value) : value));
				} 
				else 
				{
					map.put(string, "");
				}
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			HTPLog.e("parsStringToMap异常，error=" + e.getMessage());
			map.clear();
		}

		return map;
	}

	/**
	 * 拼接参数
	 * 
	 * @param map Map<String, String>
	 * @param isEncode 是否encode
	 * @return data key1=value1&key2=value2
	 * */
	public static String mapToParsString(Map<String, String> map, boolean isEncode) 
	{
		if (map == null || map.isEmpty()) 
		{
			return null;
		}

		String string = "";

		int index = 0;
		for (String key : map.keySet()) 
		{
			string += (key + "=" + (isEncode ? Uri.encode(map.get(key)) : map.get(key)));
			if (index != map.size() - 1) 
			{
				string += "&";
			}
			index++;
		}

		return string;
	}

	/**
	 * 字符串是否为空
	 * */
	public static boolean isNullOrEmpty(String string) 
	{
		if (string == null || string.length() <= 0 || string.equals("")) 
		{
			return true;
		}

		return false;
	}

	/**
	 * 在主线程上面执行Runnable
	 * */
	public static void doRunnableOnMainLooper(Context context, Runnable runnable) 
	{
		if (context == null) {
			HTPLog.e("初始化Context为空");
			return;
		}

		Handler mainHandler = new Handler(context.getMainLooper());
		mainHandler.post(runnable);
	}

	/**
	 * MD5
	 * */
	public final static String getMD5(String string) 
	{
		byte[] unencodedPassword = string.getBytes();

		MessageDigest md = null;

		try 
		{
			md = MessageDigest.getInstance("MD5");
		} 
		catch (Exception e) 
		{
			return string;
		}

		md.reset();
		md.update(unencodedPassword);
		byte[] encodedPassword = md.digest();
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < encodedPassword.length; i++) 
		{
			if ((encodedPassword[i] & 0xff) < 0x10) 
			{
				buf.append("0");
			}
			buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
		}

		return buf.toString();
	}

	/**
	 * 显示Toast
	 * */
	public static void doShowToast(final String message) 
	{
		final Context context = HTPDataCenter.getInstance().mContext;
		HTPUtils.doRunnableOnMainLooper(context, new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 显示Dialog
	 * */
	public static void doShowDialog(final Context context, final String message) 
	{
		HTPUtils.doRunnableOnMainLooper(context, new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub

				Builder alertDialog = new AlertDialog.Builder(context);
				alertDialog.setTitle("提示");
				alertDialog.setMessage(message);
				alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				alertDialog.show();
			}
		});
	}
	
	/**
	 * 显示Dialog
	 * */
	public static void doShowDialog(final Context context, final String message, final DialogInterface.OnClickListener listener) 
	{
		HTPUtils.doRunnableOnMainLooper(context, new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub

				Builder alertDialog = new AlertDialog.Builder(context);
				alertDialog.setTitle("提示");
				alertDialog.setMessage(message);
				alertDialog.setPositiveButton("确定", listener);
				alertDialog.show();
			}
		});
	}
	
	/**
	 * 显示Loading框
	 * @param title 标题
	 * @param message 提示文本
	 * */
	public static void doShowProgressDialog(final Context context, final String title, final String message) 
	{
		doCancelProgressDialog(context);
		
		HTPUtils.doRunnableOnMainLooper(context, new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				mProgressDialog = new ProgressDialog(context);
				mProgressDialog.setTitle(title);
				mProgressDialog.setMessage(message);
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.setCancelable(false);
				mProgressDialog.show();
			}
		});
	}

	/**
	 * 显示Loading框
	 * 
	 * @param message 提示文本
	 * */
	public static void doShowProgressDialog(Context context, String message) 
	{
		doShowProgressDialog(context, null, message);
	}

	/**
	 * 关闭Loading框
	 * */
	public static void doCancelProgressDialog(Context context) 
	{
		if (mProgressDialog == null) 
		{
			return;
		}
		
		HTPUtils.doRunnableOnMainLooper(context, new Runnable() 
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
	 * 获取自动创建的用户名
	 * @return 用户名
	 * */
	public static String getAutoCreateUserName() 
	{
		String timeString = String.valueOf(System.currentTimeMillis());
		return ("ht" + timeString.substring(timeString.length() - 7, timeString.length()));
	}
	
	/**
	 * 生成随机密码
	 * @return 随机密码
	 * */
	public static String getRandomPassword() 
	{
		return ("" + (new Random().nextInt(900000) + 100000));
	}
	
	/**
	 * 生成token
	 * @param timeStamp 时间戳
	 * */
	public static String genTokenFromTimestamp(String timeStamp) 
	{
		String retValue = timeStamp + HTPDataCenter.getInstance().mSecretKey;
		retValue = HTPUtils.getMD5(retValue);
		retValue = retValue.substring(5, 21);
		retValue = HTPUtils.getMD5(retValue);
		
		return retValue;
	}
	
	/**
	 * 获取OpenUDID
	 * */
	public static String getUDID() 
	{
		return HTPOpenUDID_manager.getOpenUDID();
	}
	
	/**
	 * 获取devices info
	 * */
	@SuppressWarnings("deprecation")
	public static Map<String, String> getDevicesInfo(Context context) 
	{
		Map<String, String> map = new HashMap<String, String>();
		
		String imei = "";
		String resolution = "";
		String nettype = "";
		String carrier = "未知运营商";
		String displayname = "";
		String version = "";
		String identifier = "";
		
		try 
		{
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
			nettype = tm.getNetworkType() + "";
			
			Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
			resolution = display.getWidth() + "*" + display.getHeight();
			
			PackageManager packageManager = context.getPackageManager(); 
			ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
			displayname = (String)packageManager.getApplicationLabel(applicationInfo);
			
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			version = packageInfo.versionName;
			
			String IMSI = tm.getSubscriberId();
			if (IMSI != null) 
			{
				if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) 
				{  
					carrier = "中国移动";
		        } 
				else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) 
				{  
					carrier = "中国联通";
		        } 
				else if (IMSI.startsWith("46003") || IMSI.startsWith("46005")) 
				{  
					carrier = "中国电信";
		        }
				else 
				{
					carrier = "未知运营商";
				}
			}
			
			identifier = context.getPackageName();
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		}
		
		map.put("imei", imei);
		map.put("device", android.os.Build.MODEL);
		map.put("osversion", android.os.Build.VERSION.RELEASE);
		map.put("ispirated", "0");
		map.put("resolution", resolution);
		map.put("nettype", nettype);
		map.put("carrier", carrier);
		map.put("displayname", displayname);
		map.put("version", version);
		map.put("identifier", identifier);
		map.put("devicetoken", "");
		
		return map;
	}
	
	/**
	 * 获取设备唯一标识
	 * @return 设备唯一标识
	 * */
	public static String getDeviceId() 
	{
		Context context = HTPDataCenter.getInstance().mContext;
		if (context == null) 
		{
			HTPLog.e("获取设备ID Context为空");
			return "";
		}
		
		String deviceId = null;
		try 
		{
			final TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			if (manager != null) 
			{
				deviceId = manager.getDeviceId();
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			deviceId = null;
		}
		
		if (HTPUtils.isNullOrEmpty(deviceId)) 
		{
			deviceId = "";
		}

		return deviceId;
	}
}
