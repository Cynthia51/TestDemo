package com.heitao.notification;

import com.heitao.listener.HTNotificationListener;

public class HTNotification 
{
	public String key = null;
	public Object userObject = null;
	public HTNotificationListener listener = null;
	
	public HTNotification(String _key, Object _userObject, HTNotificationListener _listener) 
	{
		key = _key;
		userObject = _userObject;
		listener = _listener;
	}
}
