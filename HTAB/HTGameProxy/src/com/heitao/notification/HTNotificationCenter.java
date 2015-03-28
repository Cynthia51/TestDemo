package com.heitao.notification;

import java.util.ArrayList;
import java.util.List;

import com.heitao.listener.HTNotificationListener;

public class HTNotificationCenter 
{
	private static HTNotificationCenter mInstance = null;
	private List<HTNotification> mNotifications = new ArrayList<HTNotification>();
	
	public static HTNotificationCenter getInstance() 
	{
		if (null == mInstance) 
		{
			mInstance = new HTNotificationCenter();
		}
		
		return mInstance;
	}
	
	/**
	 * 注册通知
	 * @param key		key
	 * @param listener	监听
	 * */
	public void register(String key, HTNotificationListener listener) 
	{
		HTNotification notification = new HTNotification(key, null, listener);
		remove(key);
		mNotifications.add(notification);
	}
	
	/**
	 * 移除通知
	 * @param key		key
	 * */
	public void remove(String key) 
	{
		if (null != mNotifications && mNotifications.size() > 0 && (null != key && key.length() > 0)) 
		{
			for (HTNotification item : mNotifications) 
			{
				if (item.key.equals(key)) 
				{
					mNotifications.remove(item);
					break;
				}
			}
		}
	}
	
	/**
	 * 移除所有通知
	 * */
	public void removeAll() 
	{
		mNotifications.clear();
	}
	
	/**
	 * 发送通知
	 * @param key		key
	 * @param object	用户数据
	 * */
	public void postNotification(String key, Object object) 
	{
		HTNotification notification = notificationForKey(key);
		if (null != notification) 
		{
			notification.userObject = object;
			notification.listener.onHTNotificationReceived(notification);
		}
	}
	
	private HTNotification notificationForKey(String key) 
	{
		if (null != key && key.length() > 0) 
		{
			for (HTNotification item : mNotifications) 
			{
				if (item.key.equals(key)) 
				{
					return item;
				}
			}
		}
		
		return null;
	}
}
