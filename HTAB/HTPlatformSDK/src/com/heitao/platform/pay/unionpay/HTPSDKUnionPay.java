package com.heitao.platform.pay.unionpay;

import android.app.Activity;
import android.content.Intent;

import com.heitao.platform.listener.HTPPayListener;
import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPPayInfo;
//import com.unionpay.UPPayAssistEx;
//import com.unionpay.uppay.PayActivity;

public class HTPSDKUnionPay 
{
	private static HTPPayListener mPayListener = null;
	
	// 生产（需要正式商户号获取的tn才能调起插件）
	public static final String MODE_PRODUCT = "00";
	// 测试（正式商户号提交的tn此参数要改成00）
	public static final String MODE_TEST = "01";

	public static void pay(final Activity activity,  HTPPayInfo payInfo, String tn, HTPPayListener listener) 
	{
		mPayListener = listener;
		
//		UPPayAssistEx.startPayByJAR(activity, PayActivity.class, null, null, tn, MODE_PRODUCT);
	}
	
	public static void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		/*************************************************
		 * 
		 * 步骤3：处理银联手机支付控件返回的支付结果
		 * 
		 ************************************************/
		if (data == null) {
			return;
		}
		
		String msg = "";
		boolean isSecceed = false;
		/*
		 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
		String str = data.getExtras().getString("pay_result");
		if (str.equalsIgnoreCase("success")) 
		{
			msg = "支付成功";
			isSecceed = true;
		} 
		else if (str.equalsIgnoreCase("fail")) 
		{
			msg = "支付失败";
			isSecceed = false;
		} 
		else if (str.equalsIgnoreCase("cancel")) 
		{
			msg = "支付取消";
			isSecceed = false;
		}
		
		if (mPayListener != null) 
		{
			if (isSecceed) 
			{
				mPayListener.onPayCompleted();
			}
			else 
			{
				mPayListener.onPayFailed(HTPError.getCustomError(msg));
			}
		}
	}
}
