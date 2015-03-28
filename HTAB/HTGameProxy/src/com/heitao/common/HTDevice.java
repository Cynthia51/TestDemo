package com.heitao.common;

import com.heitao.openudid.HTOpenUDID_manager;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class HTDevice 
{
	private static Context mContext = HTDataCenter.getInstance().mContext;
	
	/**
	 * 获取设备唯一标识
	 * @return 设备唯一标识
	 * */
	public static String getDeviceId() 
	{
		if (mContext == null) 
		{
			HTLog.e("获取设备ID Context为空");
			return null;
		}
		
		String deviceId = null;
		try 
		{
			final TelephonyManager manager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
			if (manager != null) 
			{
				deviceId = manager.getDeviceId();
			}
			
			if (HTUtils.isNullOrEmpty(deviceId)) 
			{
				deviceId = HTOpenUDID_manager.getOpenUDID();
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			deviceId = null;
		}
		
		if (HTUtils.isNullOrEmpty(deviceId)) 
		{
			deviceId = "default_device_id";
		}

		return deviceId;
	}
	
	/**
	 * 获取MAC地址
	 * @return MAC地址
	 * */
	public static String getMacAddress() 
	{
		String macAddress = null;
		try 
		{
			WifiManager wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
			//判断WIFI是否开启
	        if (!wifi.isWifiEnabled()) 
	        {  
	        	wifi.setWifiEnabled(true);
	        }
			WifiInfo info = wifi.getConnectionInfo();
			macAddress = info.getMacAddress();
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			macAddress = null;
		}
		
		return macAddress;
	}
	
	/**
	 * 获取IP地址
	 * @return IP地址
	 * */
	public static String getIPAddress() 
	{
		String ipAddress = null;
		try 
		{
			WifiManager wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
			//判断WIFI是否开启
	        if (!wifi.isWifiEnabled()) 
	        {  
	        	wifi.setWifiEnabled(true);
	        }
			WifiInfo info = wifi.getConnectionInfo();
			ipAddress = intToIp(info.getIpAddress());
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			ipAddress = null;
		}
		
		return ipAddress;
	}
	
	private static String intToIp(int i) 
	{
		return (i & 0xFF ) + "." + 
				((i >> 8 ) & 0xFF) + "." + 
				((i >> 16 ) & 0xFF) + "." + 
				( i >> 24 & 0xFF);
	}
	
	/**
	 * 获取手机号
	 * @return 手机号
	 * */
	public static String getPhoneNumber() 
	{
		if (mContext == null) 
		{
			HTLog.e("获取设备ID Context为空");
			return "";
		}
		
		String phoneNumber = "";
		try 
		{
			TelephonyManager manager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
			phoneNumber = manager.getLine1Number();
			
			if (HTUtils.isNullOrEmpty(phoneNumber)) 
			{
				phoneNumber = "";
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			phoneNumber = "";
		}
		
		if (!HTUtils.isNullOrEmpty(phoneNumber)) 
		{
			String ref = "hfvtrwjolm";
			String result = "";
			
			for (int i = 0; i < phoneNumber.length(); i++) 
			{
				char c = phoneNumber.charAt(i);
				try 
				{
					int value = Integer.parseInt(c + "");
					result += ref.charAt(value);
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
					result += c;
				}
			}
			
			phoneNumber = result;
		}
		
		return phoneNumber;
	}
}
