package com.heitao.request;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.widget.EditText;

import com.heitao.channel.HTChannelDispatcher;
import com.heitao.common.HTConsts;
import com.heitao.common.HTDataCenter;
import com.heitao.common.HTDevice;
import com.heitao.common.HTJSONHelper;
import com.heitao.common.HTLog;
import com.heitao.common.HTUtils;
import com.heitao.listener.HTAppUpdateListener;
import com.heitao.listener.HTLoginVerifyListener;
import com.heitao.listener.HTRequestListener;
import com.heitao.model.HTAppUpdateInfo;
import com.heitao.model.HTUser;
import com.heitao.update.HTAPKUpdateHelper;

public class HTBaseLoginVerify extends HTBaseRequest
{
	private Map<String, String> mParsMap = null;
	protected HTLoginVerifyListener mLoginVerifyListener = null;
	protected HTAppUpdateListener mAppUpdateListener = null;
	private HTAppUpdateInfo mAppUpdateInfo = null;
	
	private int mErrorCode = -1;
	private String mErrorTitle = "提示";
	private String mErrorMessage = "登录验证失败，请检查网络连接或稍后重试";
	private boolean mIsForce = false;
	private String mApkUpdateUrl = null;
	protected HTUser mUser = null;
	private JSONObject mSDKJSONObject = null;
	private int mOpenType = 0;	//0:浏览器 1:内置下载
	private EditText mActivationCodeEditText = null;	//	激活码输入框
	private OnClickListener mActivationlistener = null;	//	激活码对话框监听
	
	/**
	 * 登录验证
	 * */
	public void doLoginVerify(Map<String, String> parsMap, HTLoginVerifyListener listener)
	{
		mParsMap = parsMap;
		mLoginVerifyListener = listener;
		mAppUpdateListener = HTDataCenter.getInstance().mAppUpdateListener;
		
		if (mLoginVerifyListener == null) 
		{
			HTLog.e("未设置登录验证回调监听");
		}
		
		this.doShowProgressDialog("登录验证中···");
		
		String baseLoginVerifyUrl = HTConsts.BASE_URL;
		if (HTUtils.isNullOrEmpty(baseLoginVerifyUrl)) 
		{
			HTLog.e("请先设置SDK请求URL");
			this.doCancelProgressDialog();
			return;
		}
		
		if (parsMap == null || parsMap.isEmpty()) 
		{
			HTLog.e("登录验证参数为空");
			this.doCancelProgressDialog();
			return;
		}
		
		//	添加公共参数
		parsMap = this.addPublicParsMap(parsMap);
		parsMap.put("pn", HTDevice.getPhoneNumber());
		
		String requestUrl = baseLoginVerifyUrl + HTDataCenter.getInstance().mGameInfo.shortName + "/login/" + HTChannelDispatcher.getInstance().getChannelKey() + "?";
		requestUrl += HTUtils.mapToParsString(parsMap, true);
		HTLog.d("登录验证开始请求，requestUrl=" + requestUrl);
		
		this.get(requestUrl, new HTRequestListener() 
		{
			@Override
			public void onSuccess(String response) 
			{
				// TODO Auto-generated method stub
				HTLog.d("登录验证返回成功，response=" + response);
				doCancelProgressDialog();
				
				try {
					JSONObject responseObject = new JSONObject(response);
					
					//	all
					mErrorCode = HTJSONHelper.getInt(responseObject, HTConsts.KEY_ERROR_CODE);
					mErrorTitle = HTJSONHelper.getString(responseObject, HTConsts.KEY_TITLE);
					mErrorMessage = HTJSONHelper.getString(responseObject, HTConsts.KEY_ERROR_MESSAGE);
					mIsForce = HTJSONHelper.getInt(responseObject, HTConsts.KEY_IS_FORCE) == 1 ? true : false;
					mApkUpdateUrl = HTJSONHelper.getString(responseObject, HTConsts.KEY_APK_UPDATE_URL);
					boolean isUpdateBySelf = HTJSONHelper.getInt(responseObject, "is_update_by_self") == 1 ? true : false;
					mOpenType = HTJSONHelper.getInt(responseObject, "open_type");
					String versionName = HTJSONHelper.getString(responseObject, "vname");
					int versionCode = HTJSONHelper.getInt(responseObject, "vcode");
					
					if (mErrorCode == HTConsts.REQUEST_COMPLETED && !responseObject.isNull(HTConsts.KEY_ERROR_CODE)) 
					{
						JSONObject dataObject = HTJSONHelper.getJSONObject(responseObject, HTConsts.KEY_DATA);
						if (dataObject != null  && !responseObject.isNull(HTConsts.KEY_DATA)) 
						{
							//	解析user
							mUser = new HTUser();
							mUser.userId = HTJSONHelper.getString(dataObject, "uid");
							mUser.platformUserId = HTJSONHelper.getString(dataObject, "puid");
							mUser.platformId = HTJSONHelper.getString(dataObject, "pfid");
							mUser.token = HTJSONHelper.getString(dataObject, "token");
							mUser.custom = HTJSONHelper.getString(dataObject, "custom");
							
							//	解析SDK
							mSDKJSONObject = HTJSONHelper.getJSONObject(dataObject, HTConsts.KEY_SDK);
							
							JSONObject activateObject = HTJSONHelper.getJSONObject(dataObject, "activate");
							if (null != activateObject) 
							{
								int activateStatus = HTJSONHelper.getInt(activateObject, "status");
								String activateTitle = HTJSONHelper.getString(activateObject, "title");
								String activateConfirm = HTJSONHelper.getString(activateObject, "ok_btn_text");
								String activateOtherButton = HTJSONHelper.getString(activateObject, "cancel_btn_text");
								String activateMessage = HTJSONHelper.getString(activateObject, "message");
								final String activateURL = HTJSONHelper.getString(activateObject, "url");
								
								if (1 == activateStatus) 
								{
									//	处理激活码
									doActivate(dataObject, activateTitle, activateMessage, activateConfirm, activateOtherButton, activateURL);
									return;
								}
							}
							
							//	解析公共部分完成
							parseCompleted(mSDKJSONObject);
						}
					}
					else if (mErrorCode == HTConsts.LOGIN_NEW_VERSION && !responseObject.isNull(HTConsts.KEY_ERROR_CODE)) 
					{
						JSONObject dataObject = HTJSONHelper.getJSONObject(responseObject, HTConsts.KEY_DATA);
						if (dataObject != null  && !responseObject.isNull(HTConsts.KEY_DATA)) 
						{
							//	解析user
							mUser = new HTUser();
							mUser.userId = HTJSONHelper.getString(dataObject, "uid");
							mUser.platformUserId = HTJSONHelper.getString(dataObject, "puid");
							mUser.platformId = HTJSONHelper.getString(dataObject, "pfid");
							mUser.token = HTJSONHelper.getString(dataObject, "token");
							mUser.custom = HTJSONHelper.getString(dataObject, "custom");
							
							//	解析SDK
							mSDKJSONObject = HTJSONHelper.getJSONObject(dataObject, HTConsts.KEY_SDK);
							
							mErrorMessage = mErrorMessage.replace("|", "\n");
							
							mAppUpdateInfo = new HTAppUpdateInfo();
							mAppUpdateInfo.versionName = versionName;
							mAppUpdateInfo.versionCode = versionCode;
							mAppUpdateInfo.content = mErrorMessage;
							mAppUpdateInfo.apkURL = mApkUpdateUrl;
							mAppUpdateInfo.isForce = mIsForce;
							
							HTLog.d("mAppUpdateInfo=" + mAppUpdateInfo.toString());
							
							if (isUpdateBySelf) 
							{
								doShowDialog(mErrorCode, mErrorTitle, mErrorMessage);
							}
							else 
							{
								if (null == mAppUpdateListener) 
								{
									doShowDialog(mErrorCode, mErrorTitle, mErrorMessage);
									
									HTLog.e("客户端请先设置更新监听");
									return;
								}
								
								mAppUpdateListener.onHTAppUpdate(mAppUpdateInfo);
							}
						}
					}
					else 
					{
						mErrorMessage = mErrorMessage.replace("|", "\n");
						doShowDialog(mErrorCode, mErrorTitle, mErrorMessage);
					}
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
					HTLog.e("登录验证解析失败，error=" + e.toString());
					doShowDialog(mErrorCode, mErrorTitle, mErrorMessage);
				}
			}
			
			@Override
			public void onFailure(Throwable error) 
			{
				// TODO Auto-generated method stub
				HTLog.e("登录验证请求失败，error=" + error.toString());
				doShowDialog(mErrorCode, mErrorTitle, mErrorMessage);
			}
		});
	}
	
	/**
	 * 显示Dialog
	 * @param code 		错误码
	 * @param title 	提示标题
	 * @param message 	提示文本
	 * */
	private void doShowDialog(final int code, final String title, final String message) 
	{
		doCancelProgressDialog();
		
		HTUtils.doRunnableOnMainLooper(mContext, new Runnable() 
		{
			@Override
			public void run() 
			{
				String buttonTitle = "重试";
				
				if (code == HTConsts.LOGIN_TOKEN_TIME_OUT) 
				{
					HTLog.i("登录token超时");
					HTChannelDispatcher.getInstance().doLogin(null, null);
					return;
				}
				else if (code == HTConsts.LOGIN_LIMIT) 
				{
					HTLog.i("登录限制");
				}
				else if (code == HTConsts.LOGIN_NEW_VERSION) 
				{
					HTLog.i("有新版本");
					buttonTitle = "立即更新";
				}
				
				Builder alertDialog = new AlertDialog.Builder(mContext);
				alertDialog.setTitle(title);
				alertDialog.setMessage(message);
				alertDialog.setCancelable(false);
				alertDialog.setPositiveButton(buttonTitle, new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						// TODO Auto-generated method stub
						dialog.dismiss();
						
						if (code == HTConsts.LOGIN_NEW_VERSION && !HTUtils.isNullOrEmpty(mApkUpdateUrl))
						{
							if (0 == mOpenType) 
							{
								HTUtils.doOpenUrl(mApkUpdateUrl);
							}
							else if (1 == mOpenType) 
							{
								String apkSaveDir = Environment.getExternalStorageDirectory() + "/HTDownload/";
								File file = new File(apkSaveDir);
								if (!file.exists()) 
								{
									file.mkdir();
								}
								
								String apkSavePath = apkSaveDir + HTDataCenter.getInstance().mGameInfo.shortName + "_update.apk"; 
								HTAPKUpdateHelper.downloadAPK(mContext, apkSavePath, mAppUpdateInfo);
							}
						}
						else 
						{
							doLoginVerify(mParsMap, mLoginVerifyListener);
						}
					}
				});
				if (!mIsForce && code == HTConsts.LOGIN_NEW_VERSION) 
				{
					alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() 
					{
						
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							// TODO Auto-generated method stub
							dialog.dismiss();
							HTLog.i("取消更新");
							
							//	解析公共部分完成
							parseCompleted(mSDKJSONObject);
						}
					});
				}
				alertDialog.show();
			}
		});
	}
	
	/**
	 * 解析完成
	 * */
	protected void parseCompleted(JSONObject sdkJSONObject) 
	{
		//	子类重写
	}
	
	/**
	 * 处理激活码
	 * */
	private void doActivate(final JSONObject dataObject, final String title, final String message, final String confirm, final String otherButton, final String url) 
	{
		mActivationlistener = new OnClickListener() 
		{
			@Override
			public void onClick(final DialogInterface dialog, int which) 
			{
				// TODO Auto-generated method stub
				
				dialog.dismiss();
				HTLog.d("doActivate which=" + which);
				
				switch (which) 
				{
				case -1:
				{
					String code = mActivationCodeEditText.getText().toString();
					if (HTUtils.isNullOrEmpty(code)) 
					{
						HTUtils.doShowDialog("提示", "激活码输入不能为空！", new OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								// TODO Auto-generated method stub
								dialog.dismiss();
								
								doActivateDialog(title, message, confirm, otherButton, mActivationlistener);
							}
						});
						return;
					}
					
					Map<String, String> parsMap = new HashMap<String, String>();
					parsMap.put("cardno", code);
					parsMap.put("puid", mUser.platformUserId);
					
					new HTRequest().get("activate", parsMap, new HTRequestListener() 
					{
						@Override
						public void onSuccess(String response) 
						{
							// TODO Auto-generated method stub
							try 
							{
								JSONObject jsonObject = new JSONObject(response);
								int errorCode = HTJSONHelper.getInt(jsonObject, HTConsts.KEY_ERROR_CODE);
								String errorMessage = HTJSONHelper.getString(jsonObject, HTConsts.KEY_ERROR_MESSAGE);
								
								if (0 == errorCode) 
								{
									//	解析SDK
									mSDKJSONObject = HTJSONHelper.getJSONObject(dataObject, HTConsts.KEY_SDK);
									
									//	解析公共部分完成
									parseCompleted(mSDKJSONObject);
								}
								else 
								{
									HTUtils.doShowDialog("提示", errorMessage, new OnClickListener() 
									{
										@Override
										public void onClick(DialogInterface dialog, int which) 
										{
											// TODO Auto-generated method stub
											dialog.dismiss();
											doActivateDialog(title, message, confirm, otherButton, mActivationlistener);
										}
									});
								}
							} 
							catch (JSONException e) 
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
								
								HTUtils.doShowToast(e.getMessage());
							}
						}
						
						@Override
						public void onFailure(Throwable error) 
						{
							// TODO Auto-generated method stub
							
							HTUtils.doShowToast(error.getMessage());
						}
					});
				} break;
				case -2:
				{
					if (HTUtils.isNullOrEmpty(url)) 
					{
						System.exit(0);
					}
					else 
					{
						HTUtils.doOpenUrl(url);
					}
				} break;
				default:
					break;
				}
			}
		};
		
		doActivateDialog(title, message, confirm, otherButton, mActivationlistener);
	}
	
	/**
	 * 显示输入激活码对话框
	 * */
	private void doActivateDialog(String title, String message, final String confirm, final String otherButton, OnClickListener listener) 
	{
		mActivationCodeEditText = null;
		mActivationCodeEditText = new EditText(mContext);
		mActivationCodeEditText.setMaxLines(1);
		
		Builder alertDialog = new AlertDialog.Builder(mContext); 
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setView(mActivationCodeEditText);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton(HTUtils.isNullOrEmpty(confirm) ? "确定" : confirm, listener);
		alertDialog.setNegativeButton(HTUtils.isNullOrEmpty(otherButton) ? "取消" : otherButton, listener);
		alertDialog.show();
	}
}
