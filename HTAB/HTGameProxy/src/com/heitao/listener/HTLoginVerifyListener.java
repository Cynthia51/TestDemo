package com.heitao.listener;

import java.util.Map;

import com.heitao.model.HTUser;

public abstract class HTLoginVerifyListener 
{
	public abstract void onHTLoginVerifyCompleted(HTUser user, Map<String, String> sdkParsMap);
}
