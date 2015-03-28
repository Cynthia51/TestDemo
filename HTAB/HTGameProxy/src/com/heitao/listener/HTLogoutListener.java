package com.heitao.listener;

import java.util.Map;

import com.heitao.model.HTError;
import com.heitao.model.HTUser;

public abstract class HTLogoutListener 
{
	public abstract void onHTLogoutCompleted(HTUser user, Map<String, String> customParsMap);
	public abstract void onHTLogoutFailed(HTError error);
}
