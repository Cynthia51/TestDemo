package com.heitao.platform.pay.mo9;

import android.app.Activity;
import android.content.Intent;

import com.heitao.platform.activity.HTPWebViewActivity;
import com.heitao.platform.listener.HTPPayListener;
import com.heitao.platform.model.HTPPayInfo;

public class HTPSDKMo9Pay 
{
	public static void pay(final Activity activity,  HTPPayInfo payInfo, String url, HTPPayListener listener) 
	{
		Intent intent = new Intent(activity, HTPWebViewActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("URL", url);
		activity.startActivity(intent);
	}
}
