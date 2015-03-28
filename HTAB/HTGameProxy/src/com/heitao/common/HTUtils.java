package com.heitao.common;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

public class HTUtils 
{
	/**
	 * 解析参数
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
		try 
		{
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
					map.put(key, HTUtils.isNullOrEmpty(value) ? "" : (isDecode ? Uri.decode(value) : value));
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
			HTLog.e("parsStringToMap异常，error=" + e.getMessage());
			map.clear();
		}
		
		return map;
	}
	
	/**
	 * 拼接参数
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
		if (string == null || string.length() <= 0 || string.equals("") || string.toLowerCase().equals("null")) 
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
		if (context == null) 
		{
			HTLog.e("初始化Context为空");
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
	 * @param message 显示消息
	 * */
	public static void doShowToast(final String message) 
	{
		final Context context = HTDataCenter.getInstance().mContext;
		HTUtils.doRunnableOnMainLooper(context, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * 显示Dialog
	 * @param message 显示消息
	 * */
	public static void doShowDialog(final String message) 
	{
		HTUtils.doShowDialog("提示", message);
	}
	
	/**
	 * 显示Dialog
	 * @param title 标题
	 * @param message 显示消息
	 * */
	public static void doShowDialog(final String title, final String message) 
	{
		final Context context = HTDataCenter.getInstance().mContext;
		HTUtils.doRunnableOnMainLooper(context, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Builder alertDialog = new AlertDialog.Builder(context);
				alertDialog.setTitle(title);
				alertDialog.setMessage(message);
				alertDialog.setCancelable(false);
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
	 * @param title 标题
	 * @param message 显示消息
	 * @param listener 监听
	 * */
	public static void doShowDialog(final String title, final String message, final DialogInterface.OnClickListener listener) 
	{
		final Context context = HTDataCenter.getInstance().mContext;
		HTUtils.doRunnableOnMainLooper(context, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Builder alertDialog = new AlertDialog.Builder(context);
				alertDialog.setTitle(title);
				alertDialog.setMessage(message);
				alertDialog.setCancelable(false);
				alertDialog.setPositiveButton("确定", listener);
				alertDialog.show();
			}
		});
	}
	
	/**
	 * 显示Dialog
	 * @param title 标题
	 * @param message 显示消息
	 * @param buttonTitle 按钮标题
	 * @param listener 监听
	 * */
	public static void doShowDialog(final String title, final String message, final String buttonTitle, final DialogInterface.OnClickListener listener) 
	{
		final Context context = HTDataCenter.getInstance().mContext;
		HTUtils.doRunnableOnMainLooper(context, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Builder alertDialog = new AlertDialog.Builder(context);
				alertDialog.setTitle(title);
				alertDialog.setMessage(message);
				alertDialog.setCancelable(false);
				alertDialog.setPositiveButton(buttonTitle, listener);
				alertDialog.show();
			}
		});
	}
	
	/**
	 * 打开URL
	 * @param url URL
	 * */
	public static void doOpenUrl(String url) 
	{
		if (HTUtils.isNullOrEmpty(url)) 
		{
			HTLog.e("doOpenUrl 打开URL为空");
			return;
		}
		
		Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
        it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
        HTDataCenter.getInstance().mContext.startActivity(it);
	}
}
