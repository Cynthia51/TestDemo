package com.heitao.platform.activity;

import java.util.ArrayList;
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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class HTPLoginActivity extends HTPBaseActivity 
{
	private EditText mUserNameEditText;
	private EditText mPasswordEditText;
	private PopupWindow mPopView;
	
	private HTPLoginListener mLoginListener = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.htp_activity_login);
		
		mLoginListener = HTPDataCenter.getInstance().mLoginListener;
		
		initLoginPage();
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK)) 
		{
			return false;
		}
	
		return super.onKeyDown(keyCode, event);
	}

	private void initPopView(List<HTPDBUser> list) 
	{
		List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();
		for (HTPDBUser dbUser : list) 
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", dbUser.userName);
			map.put("drawable", R.drawable.htp_del_acount);
			listMap.add(map);
		}
		
		MyAdapter dropDownAdapter = new MyAdapter(this, 
				listMap, 
				R.layout.htp_dropdown_item, 
				new String[] { "name", "drawable" }, 
				new int[] { R.id.textview, R.id.delete });
		ListView listView = new ListView(this);
		listView.setAdapter(dropDownAdapter);
		listView.setDivider(getResources().getDrawable(R.color.PopViewDividerColor));
		listView.setDividerHeight(1);
		listView.setCacheColorHint(0x00000000);
		mPopView = new PopupWindow(listView, mUserNameEditText.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
		mPopView.setFocusable(true);
		mPopView.setOutsideTouchable(true);
		mPopView.setBackgroundDrawable(getResources().getDrawable(R.drawable.htp_popview_bg));
	}

	private void initLoginPage() 
	{
		TextView versionTextView = (TextView) findViewById(R.id.txt_version_login);
		versionTextView.setText("v" + HTPConsts.HTPlatformSDK_VERSION);
		
		mUserNameEditText = (EditText) findViewById(R.id.login_account_et);
		mPasswordEditText = (EditText) findViewById(R.id.login_password_et);
		
		final Button mArrowBtn = (Button) findViewById(R.id.login_arrow_btn);
		
		// 清除帐号框内的输入
		mArrowBtn.setOnClickListener(new Button.OnClickListener() 
		{
			public void onClick(View arg0) 
			{
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mUserNameEditText.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
				
				// 如果有已经登录过账号
				List<HTPDBUser> list = HTPDBHelper.getInstance().query();
				if (list != null && list.size() != 0) 
				{
					initPopView(list);
					if (!mPopView.isShowing()) 
					{
						mPopView.showAsDropDown(mUserNameEditText);
					} 
					else 
					{
						mPopView.dismiss();
					}
				}
				
				/*
				if (mPopView != null) 
				{
					if (!mPopView.isShowing()) 
					{
						mPopView.showAsDropDown(mUserNameEditText);
					} 
					else 
					{
						mPopView.dismiss();
					}
				} 
				else 
				{
					// 如果有已经登录过账号
					List<HTPDBUser> list = HTPDBHelper.getInstance().query();
					if (list != null && list.size() != 0) 
					{
						initPopView(list);
						if (!mPopView.isShowing()) 
						{
							mPopView.showAsDropDown(mUserNameEditText);
						} 
						else 
						{
							mPopView.dismiss();
						}
					}
				}
				*/
			}
		});

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
				
				HTPUtils.doShowProgressDialog(HTPLoginActivity.this, "登录中···");
				
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
						
						HTPUtils.doCancelProgressDialog(HTPLoginActivity.this);
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
						HTPUtils.doCancelProgressDialog(HTPLoginActivity.this);
						
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
		
		// 修改密码
		Button changepwdBtn = (Button) findViewById(R.id.login_changepwd_btn);
		changepwdBtn.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				// 隐藏虚拟键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);

				Intent intent = new Intent(getApplicationContext(), HTPChangePWDActivity.class);
				String _userName = mUserNameEditText.getText().toString();
				if (!HTPUtils.isNullOrEmpty(_userName)) 
				{
					intent.putExtra("USERNAME", _userName);
				}
				startActivity(intent);
			}
		});

		// 注册
		Button registerButton = (Button) findViewById(R.id.login_register_btn);
		registerButton.setOnClickListener(new Button.OnClickListener() 
		{
			public void onClick(View arg0) 
			{
				// 隐藏虚拟键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
				
				startActivity(new Intent(getApplicationContext(), HTPRegisterActivity.class));
			}
		});
	}
	
	class MyAdapter extends SimpleAdapter 
	{
		private List<HashMap<String, Object>> data;
		public MyAdapter(Context context, List<HashMap<String, Object>> data, int resource, String[] from, int[] to) 
		{
			super(context, data, resource, from, to);
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) 
		{
			ViewHolder holder;
			if (convertView == null) 
			{
				holder = new ViewHolder();
				convertView = LayoutInflater.from(HTPLoginActivity.this).inflate(R.layout.htp_dropdown_item, null);
				holder.btn = (ImageButton) convertView.findViewById(R.id.delete);
				holder.tv = (TextView) convertView.findViewById(R.id.textview);
				convertView.setTag(holder);
			} 
			else 
			{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv.setText(data.get(position).get("name").toString());
			holder.tv.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					String userName = (String) ((TextView)v).getText();
					HTPDBUser dbUser = HTPDBHelper.getInstance().queryWithUserName(userName);
					if (dbUser != null) 
					{
						mUserNameEditText.setText(dbUser.userName);
						mPasswordEditText.setText(dbUser.password);
					}
					mPopView.dismiss();
				}
			});
			holder.btn.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					 mPopView.dismiss();
					 
					 HTPDBUser dbUser = HTPDBHelper.getInstance().queryWithUserName(data.get(position).get("name").toString());
					 
					 if (dbUser != null) 
					 {
						int returnId = HTPDBHelper.getInstance().delete(dbUser);
						if (returnId == -1)
						{
							HTPUtils.doShowToast("账号删除失败");
						}
						else
						{
							List<HTPDBUser> list = HTPDBHelper.getInstance().query();
							initPopView(list);
							
							HTPUtils.doShowToast("账号删除成功");
							
							if (list.size() == 0)
							{
								//删到最后一条记录
								mUserNameEditText.setText("");
								mPasswordEditText.setText("");
							}
							else 
							{
								HTPDBUser currentUser = list.get(list.size() - 1);
								
								mUserNameEditText.setText(currentUser.userName);
								mPasswordEditText.setText(currentUser.password);
							}
						}
					 }
				}
			});
			return convertView;
		}
	}

	class ViewHolder 
	{
		private TextView tv;
		private ImageButton btn;
	}
}