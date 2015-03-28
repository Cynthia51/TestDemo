package com.heitao.platform.listener;

import com.heitao.platform.model.HTPError;

public abstract class HTPPayListener 
{
	public abstract void onPayCompleted();
	public abstract void onPayFailed(HTPError error);
}
