package com.heitao.listener;

import com.heitao.model.HTError;
import com.heitao.model.HTPayResult;

public abstract class HTPayListener 
{
	public abstract void onHTPayCompleted(HTPayResult result);
	public abstract void onHTPayFailed(HTError error);
}
