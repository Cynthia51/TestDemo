package com.heitao.platform.activity;

import java.util.HashMap;
import java.util.Map;

import com.heitao.platform.R;
import com.heitao.platform.common.HTPConsts;
import com.heitao.platform.common.HTPDBHelper;
import com.heitao.platform.common.HTPUtils;
import com.heitao.platform.listener.HTPRequestListener;
import com.heitao.platform.model.HTPDBUser;
import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPHttpEntity;
import com.heitao.platform.request.HTPNullResponeParser;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HTPChangePWDActivity extends HTPBaseActivity 
{
	private EditText mUserNameEdit;
	private EditText mOldPwdEdit;
	private EditText mNewPwdEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.htp_activity_modify_password);
		
		initChangePwdView();
	}

	private void initChangePwdView() 
	{
		Button cancel_btn = (Button) findViewById(R.id.cancel_btn);
		cancel_btn.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});

		mUserNameEdit = (EditText) findViewById(R.id.username_et);
		String _userName = this.getIntent().getStringExtra("USERNAME");
		mUserNameEdit.setText(_userName);
		
		mOldPwdEdit = (EditText) findViewById(R.id.old_pwd_et);
		
		final HTPDBUser dbUser = HTPDBHelper.getInstance().queryWithUserName(_userName);
		if (dbUser != null) 
		{
			mOldPwdEdit.setText(dbUser.password);
		}
		
		mNewPwdEdit = (EditText) findViewById(R.id.new_pwd_et);
		Button okBtn = (Button) findViewById(R.id.ok_btn);
		okBtn.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				final String userName = mUserNameEdit.getText().toString();
				final String oldPwd = mOldPwdEdit.getText().toString();
				final String newPwd = mNewPwdEdit.getText().toString();
				
				if (HTPUtils.isNullOrEmpty(userName)) 
				{
					Toast.makeText(getApplicationContext(), "账号必须填写", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (HTPUtils.isNullOrEmpty(oldPwd) || HTPUtils.isNullOrEmpty(newPwd)) 
				{
					Toast.makeText(getApplicationContext(), "密码必须填写", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (!newPwd.matches("^.{6,16}$")) 
				{
					Toast.makeText(getApplicationContext(), "密码长度必须在6~16位之间", Toast.LENGTH_SHORT).show();
					return;
				}
				
				HTPUtils.doShowProgressDialog(HTPChangePWDActivity.this, "密码修改中···");
				//修改密码
				
				Map<String, String> map = new HashMap<String, String>();
				if (dbUser != null && !HTPUtils.isNullOrEmpty(dbUser.userId)) 
				{
					map.put("uid", dbUser.userId);
				}
				map.put("username", userName);
				map.put("oldpwd", oldPwd);
				map.put("newpwd", newPwd);
				
				String url = HTPConsts.API_URL + "user/editpasswd";
				new HTPNullResponeParser().post(url, map, new HTPRequestListener() 
				{
					@Override
					public void onFailed(HTPError error) 
					{
						// TODO Auto-generated method stub
						HTPUtils.doCancelProgressDialog(HTPChangePWDActivity.this);
						
						HTPUtils.doShowToast(error.message);
					}
					
					@Override
					public void onCompleted(HTPHttpEntity httpEntity) 
					{
						// TODO Auto-generated method stub
						HTPUtils.doCancelProgressDialog(HTPChangePWDActivity.this);
						
						if (httpEntity.isCompleted) 
						{
							HTPUtils.doShowToast("修改密码成功");
							
							HTPDBUser saveToDBUser = new HTPDBUser();
							
							if (dbUser != null && !HTPUtils.isNullOrEmpty(dbUser.userId)) 
							{
								saveToDBUser.userId = dbUser.userId;
							}
							saveToDBUser.userName = userName;
							saveToDBUser.password = newPwd;
							
							HTPDBHelper.getInstance().update(saveToDBUser);
							
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
		
		Button cancelBtn = (Button) findViewById(R.id.cancel_btn);
		cancelBtn.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
	}
}
