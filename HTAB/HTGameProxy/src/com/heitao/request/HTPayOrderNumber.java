package com.heitao.request;

import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

import com.heitao.channel.HTChannelDispatcher;
import com.heitao.common.HTConsts;
import com.heitao.common.HTDataCenter;
import com.heitao.common.HTJSONHelper;
import com.heitao.common.HTLog;
import com.heitao.common.HTUtils;
import com.heitao.listener.HTPayOrderListener;
import com.heitao.listener.HTRequestListener;
import com.heitao.model.HTError;

public class HTPayOrderNumber extends HTBaseRequest
{
	private Map<String, String> mParsMap = null;
	protected HTPayOrderListener mPayOrderNumberLisener = null;
	
	private int mErrorCode = -1;
	private String mErrorTitle = "提示";
	private String mErrorMessage = "生成订单号失败，请检查网络连接或稍后重试";
	
	public void doCreatePayOrderNumber(Map<String, String> parsMap, HTPayOrderListener lisener) 
	{
		mParsMap = parsMap;
		mPayOrderNumberLisener = lisener;
		
		if (mPayOrderNumberLisener == null) 
		{
			HTLog.e("未设置生成订单号回调监听");
		}
		
		this.doShowProgressDialog("订单号生成中···");
		
		String baseUrl = HTConsts.BASE_URL;
		if (HTUtils.isNullOrEmpty(baseUrl)) 
		{
			HTLog.e("请先设置SDK请求URL");
			this.doCancelProgressDialog();
			return;
		}
		
		if (parsMap == null || parsMap.isEmpty()) 
		{
			HTLog.e("生成订单号参数为空");
			this.doCancelProgressDialog();
			return;
		}
		
		//	添加公共参数
		parsMap = this.addPublicParsMap(parsMap);
		
		String requestUrl = baseUrl + HTDataCenter.getInstance().mGameInfo.shortName + "/order/" + HTChannelDispatcher.getInstance().getChannelKey() + "?";
		requestUrl += HTUtils.mapToParsString(parsMap, true);
		HTLog.d("生成订单号开始请求，requestUrl=" + requestUrl);
		
		//	发送请求
		this.get(requestUrl, new HTRequestListener() 
		{
			@Override
			public void onSuccess(String response) 
			{
				HTLog.d("生成订单号返回成功，response=" + response);
				doCancelProgressDialog();
				
				try {
					JSONObject responseObject = new JSONObject(response);
					
					//	all
					mErrorCode = HTJSONHelper.getInt(responseObject, HTConsts.KEY_ERROR_CODE);
					mErrorTitle = HTJSONHelper.getString(responseObject, HTConsts.KEY_TITLE);
					mErrorMessage = HTJSONHelper.getString(responseObject, HTConsts.KEY_ERROR_MESSAGE);
					
					if (mErrorCode == HTConsts.REQUEST_COMPLETED && !responseObject.isNull(HTConsts.KEY_ERROR_CODE)) 
					{
						JSONObject dataObject = HTJSONHelper.getJSONObject(responseObject, HTConsts.KEY_DATA);
						if (dataObject != null  && !responseObject.isNull(HTConsts.KEY_DATA)) 
						{
							String orderNumber = HTJSONHelper.getString(dataObject, HTConsts.KEY_ORDER_NUMBER);
							String customServerId = HTJSONHelper.getString(dataObject, HTConsts.KEY_SERVER_ID);
							JSONObject customData = HTJSONHelper.getJSONObject(dataObject, HTConsts.KEY_CUSTOM);
							
							if (mPayOrderNumberLisener != null) 
							{
								mPayOrderNumberLisener.onHTPayOrderNumberCompleted(orderNumber, customServerId, customData);
							}
						}
					}
					else 
					{
						doShowTryDialog(mErrorCode, mErrorTitle, mErrorMessage);
					}
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
					HTLog.e("生成订单号解析失败，error=" + e.toString());
					doCancelProgressDialog();
					if (mPayOrderNumberLisener != null) 
					{
						mPayOrderNumberLisener.onHTPayOrderNumberFailed(HTError.getParsError());
					}
				}
			}
			
			@Override
			public void onFailure(Throwable e) 
			{ 
				HTLog.e("生成订单号请求失败，error=" + e.toString());
				doCancelProgressDialog();
				if (mPayOrderNumberLisener != null) 
				{
					mPayOrderNumberLisener.onHTPayOrderNumberFailed(HTError.getNetworkError());
				}
			}
		});
	}
	
	/**
	 * 显示Dialog
	 * @param code 		错误码
	 * @param title 	提示标题
	 * @param message 	提示文本
	 * */
	private void doShowTryDialog(final int code, final String title, final String message) 
	{
		doCancelProgressDialog();
		
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() 
		{
			@Override
			public void run() 
			{
				String buttonTitle = "重试";
				
				if (code == HTConsts.PAY_CLOSED) 
				{
					HTLog.i("充值关闭");
					buttonTitle = "继续游戏";
				}
				
				Builder alertDialog = new AlertDialog.Builder(mContext);
				alertDialog.setTitle(title);
				alertDialog.setMessage(message);
				alertDialog.setPositiveButton(buttonTitle, new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						// TODO Auto-generated method stub
						dialog.dismiss();
						
						if (code == HTConsts.PAY_CLOSED) 
						{
							// 充值关闭
						}
						else 
						{
							doCreatePayOrderNumber(mParsMap, mPayOrderNumberLisener);
						}
					}
				});
				alertDialog.show();
			}
		});
	}
}
