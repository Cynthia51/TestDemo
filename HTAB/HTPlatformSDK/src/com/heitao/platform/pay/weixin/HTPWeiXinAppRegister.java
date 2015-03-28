package com.heitao.platform.pay.weixin;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HTPWeiXinAppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		final IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
		msgApi.registerApp(HTPSDKWeiXinPay.APP_ID);
	}
}
