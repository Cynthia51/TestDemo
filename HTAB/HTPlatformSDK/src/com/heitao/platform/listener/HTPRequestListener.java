package com.heitao.platform.listener;

import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPHttpEntity;

public abstract class HTPRequestListener 
{
	public abstract void onCompleted(HTPHttpEntity httpEntity);
	public abstract void onFailed(HTPError error);
}
