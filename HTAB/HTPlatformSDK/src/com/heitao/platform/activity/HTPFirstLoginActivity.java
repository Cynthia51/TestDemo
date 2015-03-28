package com.heitao.platform.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.heitao.platform.R;
import com.heitao.platform.common.HTPConsts;
import com.heitao.platform.common.HTPDBHelper;
import com.heitao.platform.common.HTPDataCenter;
import com.heitao.platform.common.HTPLog;
import com.heitao.platform.common.HTPUtils;
import com.heitao.platform.listener.HTPLoginListener;
import com.heitao.platform.listener.HTPRequestListener;
import com.heitao.platform.model.HTPDBUser;
import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPHttpEntity;
import com.heitao.platform.model.HTPUser;
import com.heitao.platform.request.HTPLoginParser;
import com.heitao.platform.request.HTPRegisterParser;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HTPFirstLoginActivity extends HTPBaseActivity 
{
	public static String userName;
	private EditText mUserNameEditText;
	private EditText mPasswordEditText;
	
	private HTPLoginListener mLoginListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.htp_activity_first_login);
		
		HTPDataCenter.getInstance().mUser = null;
		mLoginListener = HTPDataCenter.getInstance().mLoginListener;
		
		this.initLoginView();
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		
		HTPUser user = HTPDataCenter.getInstance().mUser;
		if (user != null) 
		{
			HTPDBUser dbUser = HTPDBHelper.getInstance().queryWithUserId(user.userId);
			if (dbUser != null) 
			{
				mUserNameEditText.setText(dbUser.userName);
				mPasswordEditText.setText(dbUser.password);
			}
			else 
			{
				HTPLog.e("数据库查询失败，userId=" + user.userId);
			}
		}
		else 
		{
			List<HTPDBUser> list = HTPDBHelper.getInstance().query();
			if (list != null && list.size() != 0) 
			{
				HTPDBUser dbUser = list.get(list.size() - 1);
				
				mUserNameEditText.setText(dbUser.userName);
				mPasswordEditText.setText(dbUser.password);
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK)) 
		{
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 返回操作(未使用)
	 * */
	protected void doBack() 
	{
		if (mLoginListener != null && HTPDataCenter.getInstance().mUser == null) 
		{
			mLoginListener.onLoginFailed(HTPError.getLoginCancelError());
		}
	}
	
	private void initLoginView() 
	{
		TextView versionTextView = (TextView) findViewById(R.id.txt_version_first_login);
		versionTextView.setText("v" + HTPConsts.HTPlatformSDK_VERSION);
		
		mUserNameEditText = (EditText) findViewById(R.id.login_account_et);
		mPasswordEditText = (EditText) findViewById(R.id.login_password_et);

		// 登录
		Button loginButton = (Button) findViewById(R.id.login_login_btn);
		loginButton.setOnClickListener(new Button.OnClickListener() 
		{
			public void onClick(View arg0) 
			{
				// 隐藏虚拟键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
				
				final String userName = mUserNameEditText.getText().toString();
				final String password = mPasswordEditText.getText().toString();
				
				if (HTPUtils.isNullOrEmpty(userName)) 
				{
					HTPUtils.doShowToast("用户名不能为空");
					return;
				}
				
				if (HTPUtils.isNullOrEmpty(password)) 
				{
					HTPUtils.doShowToast("密码不能为空");
					return;
				}
				
				HTPUtils.doShowProgressDialog(HTPFirstLoginActivity.this, "登录中···");
				
				Map<String, String> map = new HashMap<String, String>();
				map.put("username", userName);
				map.put("password", password);
				
				String url = HTPConsts.API_URL + "user/login";
				new HTPLoginParser().get(url, map, new HTPRequestListener() 
				{
					@Override
					public void onFailed(HTPError error) 
					{
						// TODO Auto-generated method stub
						HTPUtils.doCancelProgressDialog(HTPFirstLoginActivity.this);
						HTPUtils.doShowToast(error.message);
						
						/*
						if (mLoginListener != null) 
						{
							mLoginListener.onLoginFailed(error);
						}
						*/
					}
					
					@Override
					public void onCompleted(HTPHttpEntity httpEntity) 
					{
						// TODO Auto-generated method stub
						HTPUtils.doCancelProgressDialog(HTPFirstLoginActivity.this);
						
						if (httpEntity.isCompleted) 
						{
							HTPUser user = (HTPUser)httpEntity.object;
							HTPDataCenter.getInstance().mUser = user;
							
							HTPDBUser dbUser = new HTPDBUser();
							dbUser.userId = user.userId;
							dbUser.userName = user.userName;
							dbUser.password = password;
							
							HTPDBHelper.getInstance().update(dbUser);
							
							if (mLoginListener != null) 
							{
								mLoginListener.onLoginCompleted(user);
							}
							
							finish();
						}
						else 
						{
							HTPUtils.doShowToast(httpEntity.message);
						}
					}
				});
			}
		});

		// 注册账号
		Button registerButton = (Button) findViewById(R.id.login_register_btn);
		registerButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) 
			{
				// 隐藏虚拟键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
				
				startActivity(new Intent(getApplicationContext(), HTPRegisterActivity.class));
			}
		});
		
		//	一键注册
		Button mRegisterLoginButton = (Button) findViewById(R.id.login_register_login_btn);
		mRegisterLoginButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				// 隐藏虚拟键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mUserNameEditText.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
				
				HTPUtils.doShowProgressDialog(HTPFirstLoginActivity.this, "一键注册登录中···");
				
				final String userName = HTPUtils.getAutoCreateUserName();
				final String password = HTPUtils.getRandomPassword();
				
				Map<String, String> map = new HashMap<String, String>();
				map.put("username", userName);
				map.put("password", password);
				
				String url = HTPConsts.API_URL + "user/reg";
				new HTPRegisterParser().get(url, map, new HTPRequestListener() 
				{
					@Override
					public void onFailed(HTPError error) 
					{
						// TODO Auto-generated method stub
						HTPUtils.doCancelProgressDialog(HTPFirstLoginActivity.this);
						HTPUtils.doShowToast(error.message);
						
						/*
						if (mLoginListener != null) 
						{
							mLoginListener.onLoginFailed(error);
						}
						*/
					}
					
					@Override
					public void onCompleted(HTPHttpEntity httpEntity) 
					{
						// TODO Auto-generated method stub
						HTPUtils.doCancelProgressDialog(HTPFirstLoginActivity.this);
						
						if (httpEntity.isCompleted) 
						{
							final HTPUser user = (HTPUser)httpEntity.object;
							HTPDataCenter.getInstance().mUser = user;
							
							mUserNameEditText.setText(userName);
							mPasswordEditText.setText(password);
							
							HTPDBUser dbUser = new HTPDBUser();
							dbUser.userId = user.userId;
							dbUser.userName = user.userName;
							dbUser.password = password;
							
							HTPDBHelper.getInstance().update(dbUser);
							
							String message = "请牢记您的账号和密码\n" + "账号：" + userName + "\n" + "密码：" + password + "\n";
							message += "为了您的账号安全，建议在下次登录时修改密码。";
							
							Builder alertDialog = new AlertDialog.Builder(HTPFirstLoginActivity.this);
							alertDialog.setTitle("提示");
							alertDialog.setMessage(message);
							alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									// TODO Auto-generated method stub
									
									if (mLoginListener != null) 
									{
										mLoginListener.onLoginCompleted(user);
									}
									
									finish();
								}
							});
							alertDialog.show();
						}
						else 
						{
							HTPUtils.doShowToast(httpEntity.message);
						}
					}
				});
			}
		});
	}
}
