package com.heitao.platform.pay.weixin;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.heitao.platform.common.HTPLog;
import com.heitao.platform.listener.HTPPayListener;
import com.heitao.platform.model.HTPPayInfo;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class HTPSDKWeiXinPay 
{
	public static final String APP_ID = "wxc4d71ad256b23976";
	public static final String PARTNER_ID = "1220213701";
	
	private static HTPSDKWeiXinPay mInstance = null;
	public HTPPayListener mPayListener = null;
	public IWXAPI mApi = null;
	
	public static HTPSDKWeiXinPay getInstance() 
	{
		if (mInstance == null) 
		{
			mInstance = new HTPSDKWeiXinPay();
		}
		
		return mInstance;
	}
	
	public void init(Context context) 
	{
		mApi = WXAPIFactory.createWXAPI(context, APP_ID);
	}
	
	public void pay(final Activity activity,  HTPPayInfo payInfo, JSONObject pars, HTPPayListener listener) 
	{
		mPayListener = listener;
		
		PayReq req = new PayReq();
		req.appId = APP_ID;
		req.partnerId = PARTNER_ID;
		
		try 
		{
			req.prepayId = pars.getString("prepayid");
			req.nonceStr = pars.getString("noncestr");
			req.timeStamp = pars.getString("timestamp");
			req.packageValue = Uri.decode(pars.getString("packageValue"));
			req.sign = pars.getString("sign");
			req.extData = ""; // optional，微信不处理该字段，会在PayResp结构体中回传该字段
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HTPLog.d("appId=" + req.appId);
		HTPLog.d("partnerId=" + req.partnerId);
		HTPLog.d("prepayId=" + req.prepayId);
		HTPLog.d("nonceStr=" + req.nonceStr);
		HTPLog.d("timeStamp=" + req.timeStamp);
		HTPLog.d("packageValue=" + req.packageValue);
		HTPLog.d("sign=" + req.sign);
		HTPLog.d("extData=" + req.extData);
		
		// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
		mApi.sendReq(req);
	}
}
