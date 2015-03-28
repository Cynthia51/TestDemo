package com.heitao.platform.pay.weixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.heitao.platform.common.HTPLog;
import com.heitao.platform.listener.HTPPayListener;
import com.heitao.platform.model.HTPError;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler
{
	private HTPPayListener mPayListener = HTPSDKWeiXinPay.getInstance().mPayListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		HTPLog.d("WXPayEntryActivity->onCreate");
		HTPSDKWeiXinPay.getInstance().mApi.handleIntent(getIntent(), this);
	}
	
	@Override
	protected void onNewIntent(Intent intent) 
	{
		super.onNewIntent(intent);
		setIntent(intent);
		HTPSDKWeiXinPay.getInstance().mApi.handleIntent(getIntent(), this);
	}
	
	@Override
	public void onReq(BaseReq baseReq) 
	{
		// TODO Auto-generated method stub
		HTPLog.d("WXPayEntryActivity->onReq baseReq=" + baseReq.toString());
	}

	@Override
	public void onResp(BaseResp baseResp) 
	{
		// TODO Auto-generated method stub
		HTPLog.d("WXPayEntryActivity->onResp errCode=" + baseResp.errCode + " errStr=" + baseResp.errStr);
		if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) 
		{
			if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) 
			{
				if (mPayListener != null) 
				{
					mPayListener.onPayCompleted();
				}
			}
			else 
			{
				if (mPayListener != null) 
				{
					mPayListener.onPayFailed(new HTPError(baseResp.errCode, baseResp.errStr));
				}
			}
			
			finish();
		}
	}
}
