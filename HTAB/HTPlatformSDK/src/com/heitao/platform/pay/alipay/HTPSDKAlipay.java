package com.heitao.platform.pay.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

//import com.alipay.sdk.app.PayTask;
import com.heitao.platform.common.HTPLog;
import com.heitao.platform.common.HTPUtils;
import com.heitao.platform.listener.HTPPayListener;
import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPPayInfo;

public class HTPSDKAlipay 
{
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCl1cdV/BP3Mutegi2Fxs7Vy1Tn" + 
											"A8AjM6WKZC7okj2WpICBxxSSMSAuKFQMf39Xe5Jf9Dq3vwNzu5uz20aByc3HT3lR" + 
											"1CaZs7LW80uyTmiJlD3X4VZvtg+E33K6aoQxNXCKaCp/1i3ML2C/vUoIoW61XZUg" + 
											"B/pCrHSgxlK2bA6/GwIDAQAB";

	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;
	
	private static Activity mActivity = null;
	private static HTPPayListener mPayListener = null;

	public static void pay(final Activity activity, HTPPayInfo payInfo, final String orderInfo, HTPPayListener listener) 
	{
		mActivity = activity;
		mPayListener = listener;

		new Thread() 
		{
			public void run() 
			{
//				PayTask alipay = new PayTask(mActivity);
//				String result = alipay.pay(orderInfo);
//				
//				Message msg = new Message();
//				msg.what = SDK_PAY_FLAG;
//				msg.obj = result;
//				mHandler.sendMessage(msg);
			}
		}.start();
	}
	
	private static Handler mHandler = new Handler() 
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
				case SDK_PAY_FLAG: 
				{
					HTPLog.d("msg.what case SDK_PAY_FLAG");
					
					Result resultObj = new Result((String) msg.obj);
					String resultStatus = resultObj.resultStatus;
					
					HTPLog.d("支付宝回调，resultStatus=" + resultStatus);
	
					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) 
					{
						if (mPayListener != null) 
						{
							mPayListener.onPayCompleted();
						}
					} 
					else 
					{
						// 判断resultStatus 为非“9000”则代表可能支付失败
						// “8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) 
						{
							if (mPayListener != null) 
							{
								mPayListener.onPayFailed(HTPError.getPayConfirmingError());
							}
						} 
						else 
						{
							if (mPayListener != null) 
							{
								mPayListener.onPayFailed(HTPError.getPayFailError());
							}
						}
					}
					break;
				}
				case SDK_CHECK_FLAG: 
				{
					HTPLog.d("msg.what case SDK_CHECK_FLAG");
					
					HTPUtils.doShowToast("检查结果为：" + msg.obj);
					break;
				}
				default:
					break;
			}
		};
	};
}
