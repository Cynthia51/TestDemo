package com.heitao.platform.pay.center;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.heitao.platform.common.HTPDataCenter;
import com.heitao.platform.common.HTPLog;
import com.heitao.platform.common.HTPUtils;
import com.heitao.platform.listener.HTPPayListener;
import com.heitao.platform.listener.HTPRequestListener;
import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPHttpEntity;
import com.heitao.platform.model.HTPPayInfo;
import com.heitao.platform.pay.alipay.HTPSDKAlipay;
import com.heitao.platform.pay.mo9.HTPSDKMo9Pay;
import com.heitao.platform.pay.unionpay.HTPSDKUnionPay;
import com.heitao.platform.pay.weixin.HTPSDKWeiXinPay;
import com.heitao.platform.request.HTPNullResponeParser;

public class HTPSDKPayCenter 
{
	/**
	 * 支付渠道
	 * */
	public enum PayChannel
	{
		none,
		alipay,
		unionpay,
		mo9,
		yeepay,
		weixin,
		heitao
	}
	
	/**
	 * 易宝支付渠道
	 * */
	public enum YeepayChannel
	{
		none,
		CHA,		//	中国电信
		CHU,		//	中国联通
		CHL			//	中国移动
	}
	
	private static HTPSDKPayCenter mInstance = null;
	
	private PayChannel mPayChannel = PayChannel.none;
	
	private YeepayChannel mYeepayChannel = YeepayChannel.none;
	private String mCardNo = null;
	private String mCardPWD = null;
	private int mCardValue = 0;
	
	/**
	 * 获取支付中心实例
	 * */
	public static HTPSDKPayCenter getInstance() 
	{
		if (mInstance == null) 
		{
			mInstance = new HTPSDKPayCenter();
		}
		
		return mInstance;
	}
	
	/**
	 * 初始化
	 * */
	public void init(Context context) 
	{
		HTPSDKWeiXinPay.getInstance().init(context);
	}
	
	/**
	 * 通用支付入口
	 * @param activity activity
	 * @param channel 支付渠道
	 * @param payInfo 支付信息
	 * @param listener 支付监听(支付SDK->黑桃SDK)
	 * */
	public void pay(final Activity activity, PayChannel channel, final HTPPayInfo payInfo, final HTPPayListener listener) 
	{
		mPayChannel = channel;
		
		if (mPayChannel == PayChannel.none) 
		{
			HTPLog.e("支付中心-支付渠道为空");
			return;
		}
		
		//	支付类型 1 黑桃币 2 支付宝 3 银联 4 易宝(移动) 5 易宝(联通) 6 易宝(电信) 7 微信 8 mo9支付
		int intPayChannel = 0;
		if (mPayChannel == PayChannel.alipay) 
		{
			//	支付宝
			intPayChannel = 2;
		}
		else if (mPayChannel == PayChannel.unionpay) 
		{
			//	银联支付
			intPayChannel = 3;
		}
		else if (mPayChannel == PayChannel.mo9) 
		{
			//	先玩后付
			intPayChannel = 8;
		}
		else if (mPayChannel == PayChannel.yeepay) 
		{
			//	易宝支付
			if (mYeepayChannel == YeepayChannel.CHA) 
			{
				intPayChannel = 6;
			}
			else if (mYeepayChannel == YeepayChannel.CHU) 
			{
				intPayChannel = 5;
			}
			else if (mYeepayChannel == YeepayChannel.CHL) 
			{
				intPayChannel = 4;
			}
			else 
			{
				intPayChannel = 0;
			}
		}
		else if (mPayChannel == PayChannel.weixin) 
		{
			//	微信支付
			intPayChannel = 7;
		}
		else if (mPayChannel == PayChannel.heitao) 
		{
			//	黑桃支付
			intPayChannel = 1;
		}
		
		HTPUtils.doShowProgressDialog(activity, "订单请求中···");
		
		//	URL
		String url = "http://pay.heitao.com/mpay/order";
		
		//	pars
		String publicKey = HTPDataCenter.getInstance().mAppKey;
		String uid = HTPDataCenter.getInstance().mUser.getUserId();
		String time = System.currentTimeMillis() + "";
		String tokenFrom = time + payInfo.getAmount() + uid + payInfo.getServerId() + publicKey + HTPUtils.getUDID();
		String token = HTPUtils.getMD5(tokenFrom);
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("pub_key", publicKey);
		map.put("psid", payInfo.getServerId());
		map.put("uid", uid);
		map.put("mid", HTPUtils.getUDID());
		map.put("amount", payInfo.getAmount());
		map.put("chid", HTPDataCenter.getInstance().mChannelId);
		map.put("time", time);
		map.put("token", token);
		map.put("extra", payInfo.getExtendInfo());
		map.put("orderno_app", payInfo.getOrderId());
		map.put("type", intPayChannel + "");
		if (mPayChannel == PayChannel.yeepay) 
		{
			map.put("card_val", mCardValue + "");
		}
		
		if (mPayChannel == PayChannel.yeepay && mYeepayChannel != YeepayChannel.none) 
		{
			map.put("card_num", mCardNo);
			map.put("card_key", mCardPWD);
		}
		
		HTPLog.d("pay map=" + map.toString());
		
		new HTPNullResponeParser().get(url, map, false, new HTPRequestListener() 
		{
			@Override
			public void onFailed(HTPError error) 
			{
				// TODO Auto-generated method stub
				HTPUtils.doCancelProgressDialog(activity);
				
				if (listener != null) 
				{
					listener.onPayFailed(HTPError.getCustomError(error.message));
				}
			}
			
			@Override
			public void onCompleted(HTPHttpEntity httpEntity) 
			{
				// TODO Auto-generated method stub
				HTPUtils.doCancelProgressDialog(activity);
				if (httpEntity.isCompleted) 
				{
					Object params = null;
					try 
					{
						params = httpEntity.dataObject.get("params");
					} 
					catch (JSONException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						params = null;
					}
					
					if (mPayChannel != PayChannel.yeepay && params == null) 
					{
						HTPLog.e("params is null");
						return;
					}
					else 
					{
						HTPLog.d("params=" + params);
					}
					
					if (mPayChannel == PayChannel.alipay) 
					{
						//	支付宝
						HTPSDKAlipay.pay(activity, payInfo, (String) params, listener);
					}
					else if (mPayChannel == PayChannel.unionpay) 
					{
						//	银联支付
						HTPSDKUnionPay.pay(activity, payInfo, (String) params, listener);
					}
					else if (mPayChannel == PayChannel.mo9) 
					{
						//	先玩后付
						HTPSDKMo9Pay.pay(activity, payInfo, (String) params, listener);
					}
					else if (mPayChannel == PayChannel.yeepay) 
					{
						//	易宝支付
						if (listener != null) 
						{
							listener.onPayCompleted();
						}
					}
					else if (mPayChannel == PayChannel.weixin) 
					{
						//	微信支付
						HTPSDKWeiXinPay.getInstance().pay(activity, payInfo, (JSONObject) params, listener);
					}
					else if (mPayChannel == PayChannel.heitao) 
					{
						//	黑桃支付
					}
				}
				else 
				{
					if (listener != null) 
					{
						listener.onPayFailed(HTPError.getCustomError(httpEntity.message));
					}
				}
			}
		});
	}
	
	/**
	 * 易宝支付
	 * @param activity activity
	 * @param yeepayChannel 易宝充值卡渠道
	 * @param cardNo 卡号
	 * @param cardPWD 卡密码
	 * @param payInfo 支付信息
	 * @param listener 支付监听(支付SDK->黑桃SDK)
	 * */
	public void yeepay(final Activity activity, YeepayChannel yeepayChannel, String cardNo, String cardPWD, int cardValue, HTPPayInfo payInfo, HTPPayListener listener) 
	{
		mYeepayChannel = yeepayChannel;
		mCardNo = cardNo;
		mCardPWD = cardPWD;
		mCardValue = cardValue;
		
		this.pay(activity, PayChannel.yeepay, payInfo, listener);
	}
	
	public void onActivityResult(PayChannel channel, int requestCode, int resultCode, Intent data) 
	{
		if (mPayChannel == PayChannel.alipay) 
		{
			//	支付宝
		}
		else if (mPayChannel == PayChannel.unionpay) 
		{
			//	银联支付
			HTPSDKUnionPay.onActivityResult(requestCode, resultCode, data);
		}
		else if (mPayChannel == PayChannel.mo9) 
		{
			//	先玩后付
		}
		else if (mPayChannel == PayChannel.yeepay) 
		{
			//	易宝支付
		}
		else if (mPayChannel == PayChannel.weixin) 
		{
			//	微信支付
		}
		else if (mPayChannel == PayChannel.heitao) 
		{
			//	黑桃支付
		}
	}
}
