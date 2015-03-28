package com.heitao.listener;

import org.json.JSONObject;

import com.heitao.model.HTError;

public abstract class HTPayOrderListener 
{
	public abstract void onHTPayOrderNumberCompleted(String orderNumber, String customServerId, JSONObject customData);
	public abstract void onHTPayOrderNumberFailed(HTError error);
}
