package com.heitao.listener;

import com.heitao.notification.HTNotification;

public interface HTNotificationListener 
{
	public abstract void onHTNotificationReceived(HTNotification notification);
}
