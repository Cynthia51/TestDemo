package com.heitao.listener;

import java.util.Map;

import com.heitao.model.HTError;
import com.heitao.model.HTUser;

public abstract class HTLoginListener 
{
	public abstract void onHTLoginCompleted(HTUser user, Map<String, String> customParsMap);
	public abstract void onHTLoginFailed(HTError error);
}
