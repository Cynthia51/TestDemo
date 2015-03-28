package com.heitao.listener;

import com.heitao.model.HTError;
import com.heitao.model.HTSDKInfo;

public abstract class HTInitListener 
{
	public abstract void onHTInitCompleted(HTSDKInfo sdkInfo);
	public abstract void onHTInitFailed(HTError error);
}
