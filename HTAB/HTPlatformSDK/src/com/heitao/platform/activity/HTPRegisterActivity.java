package com.heitao.platform.activity;

import java.util.HashMap;
import java.util.Map;

import com.heitao.platform.R;
import com.heitao.platform.common.HTPConsts;
import com.heitao.platform.common.HTPDBHelper;
import com.heitao.platform.common.HTPDataCenter;
import com.heitao.platform.common.HTPUtils;
import com.heitao.platform.listener.HTPRequestListener;
import com.heitao.platform.model.HTPDBUser;
import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPHttpEntity;
import com.heitao.platform.model.HTPUser;
import com.heitao.platform.request.HTPRegisterParser;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class HTPRegisterActivity extends HTPBaseActivity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.htp_activity_register);

		initRegisterPage();
	}

	private void initRegisterPage() 
	{
		Button leftBtn = (Button) findViewById(R.id.register_back_btn);
		leftBtn.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});

		final EditText userName = (EditText) findViewById(R.id.login_username_et);
		userName.setText(HTPUtils.getAutoCreateUserName());
		
		final EditText loginPassworded = (EditText) findViewById(R.id.login_password_et);
		loginPassworded.setText(HTPUtils.getRandomPassword());
		
		Button mLogin = (Button) findViewById(R.id.register_login_btn);
		loginPassworded.setOnTouchListener(new EditText.OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				loginPassworded.setCursorVisible(true);
				return false;
			}
		});

		mLogin.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				// 隐藏虚拟键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(loginPassworded.getWindowToken(), 0);
				checkRegisterInputPrams(userName, loginPassworded, getApplicationContext());
			}
		});
	}

	private void checkRegisterInputPrams(EditText emailAutoCompletatv, EditText loginPassworded, Context context) 
	{
		final String userName = emailAutoCompletatv.getText().toString();
		final String password = loginPassworded.getText().toString();
		
		if (HTPUtils.isNullOrEmpty(userName)) 
		{
			HTPUtils.doShowToast("账号不能为空");
			return;
		}
		
		if (HTPUtils.isNullOrEmpty(password)) 
		{
			HTPUtils.doShowToast("密码必须填写");
			return;
		}

		if (!userName.matches("^[a-z|A-Z]{1}.{0,}$")) 
		{
			HTPUtils.doShowToast("账号必须以字母开头");
			return;
		}
		
		if (!userName.matches("^[a-z|A-Z|0-9|_|.|-]{1,}$")) 
		{
			HTPUtils.doShowToast("账号须由字母和数字组成");
			return;
		}

		if (!userName.matches("^.{4,16}$")) 
		{
			HTPUtils.doShowToast("账号长度必须在4~16位");
			return;
		}
		
		if (!password.matches("^.{6,16}$")) 
		{
			HTPUtils.doShowToast("密码长度必须在6~16位");
			return;
		}
		
		HTPUtils.doShowProgressDialog(HTPRegisterActivity.this, "注册中···");
		
		//	开始注册
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
				HTPUtils.doCancelProgressDialog(HTPRegisterActivity.this);
				HTPUtils.doShowToast(error.message);
			}
			
			@Override
			public void onCompleted(HTPHttpEntity httpEntity) 
			{
				// TODO Auto-generated method stub
				HTPUtils.doCancelProgressDialog(HTPRegisterActivity.this);
				
				if (httpEntity.isCompleted) 
				{
					HTPUser user = (HTPUser)httpEntity.object;
					HTPDataCenter.getInstance().mUser = user;
					
					HTPDBUser dbUser = new HTPDBUser();
					dbUser.userId = user.userId;
					dbUser.userName = user.userName;
					dbUser.password = password;
					
					HTPDBHelper.getInstance().update(dbUser);
					
					finish();
				}
				else 
				{
					HTPUtils.doShowToast(httpEntity.message);
				}
			}
		});
	}
}
