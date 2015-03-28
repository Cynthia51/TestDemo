package com.heitao.platform.listener;

import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPUser;

public abstract class HTPLoginListener 
{
	public abstract void onLoginCompleted(HTPUser user);
	public abstract void onLoginFailed(HTPError error);
}
