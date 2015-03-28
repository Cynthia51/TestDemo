package com.heitao.htgameproxydemo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.heitao.api.HTGameProxy;
import com.heitao.common.HTKeys;
import com.heitao.listener.HTAppUpdateListener;
import com.heitao.listener.HTExitListener;
import com.heitao.listener.HTLoginListener;
import com.heitao.listener.HTLogoutListener;
import com.heitao.listener.HTPayListener;
import com.heitao.model.HTError;
import com.heitao.model.HTGameInfo;
import com.heitao.model.HTGameInfo.HTDirection;
import com.heitao.model.HTAppUpdateInfo;
import com.heitao.model.HTPayInfo;
import com.heitao.model.HTPayResult;
import com.heitao.model.HTUser;
import com.heitao.mhj.demo.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	private int newLevel = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((Button) findViewById(R.id.btn_login)).setOnClickListener(mClickListener);
		((Button) findViewById(R.id.btn_logout)).setOnClickListener(mClickListener);
		((Button) findViewById(R.id.btn_pay)).setOnClickListener(mClickListener);
		((Button) findViewById(R.id.btn_exit)).setOnClickListener(mClickListener);
		((Button) findViewById(R.id.btn_startGame)).setOnClickListener(mClickListener);
		((Button) findViewById(R.id.btn_levelChanged)).setOnClickListener(mClickListener);
		
		//	初始化黑桃SDK
		HTGameInfo gameInfo = new HTGameInfo("七龙珠", "qlz", HTDirection.Portrait);
		HTGameProxy.init(this, gameInfo);
		
		HTGameProxy.setLogEnable(true);
		HTGameProxy.setDebugEnable(true);
		HTGameProxy.setShowFunctionMenu(true);
		
		HTGameProxy.setLogoinListener(mLoginListener);
		HTGameProxy.setLogoutListener(mLogoutListener);
		HTGameProxy.setExitListener(mExitListener);
		HTGameProxy.setAppUpdateListener(mAppUpdateListener);
		
		HTGameProxy.onCreate(this);
	}
	
	@Override
	protected void onPause() 
	{
		// TODO Auto-generated method stub
		super.onPause();
		
		HTGameProxy.onPause();
	}
	
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		
		HTGameProxy.onResume();
	}
	
	@Override
	protected void onStop() 
	{
		// TODO Auto-generated method stub
		super.onStop();
		
		HTGameProxy.onStop();
	}
	
	@Override
	protected void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		
		HTGameProxy.onDestroy();
	}
	
	@Override
	protected void onRestart() 
	{
		// TODO Auto-generated method stub
		super.onRestart();
		
		HTGameProxy.onRestart();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		HTGameProxy.onActivityResult(requestCode, resultCode, data);
	}

	private OnClickListener mClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View view) 
		{
			// TODO Auto-generated method stub
			switch (view.getId()) 
			{
			case R.id.btn_login: 
			{
				//	登录
				HTGameProxy.doLogin(null, mLoginListener);
			}
				break;
			case R.id.btn_logout: 
			{
				//	登出
				HTGameProxy.doLogout(null, mLogoutListener);
			}
				break;
			case R.id.btn_pay: 
			{
				JSONObject customJsonObject = new JSONObject();
				
				/*
				try 
				{
					customJsonObject.put(HTKeys.KEY_USER_BALANCE, "用户余额");
					customJsonObject.put(HTKeys.KEY_VIP_LEVEL, "VIP等级");
					customJsonObject.put(HTKeys.KEY_ROLE_LEVEL, "角色等级");
					customJsonObject.put(HTKeys.KEY_USER_PARTY, "公会、帮派");
					customJsonObject.put(HTKeys.KEY_IS_PAY_MONTH, "是否月卡支付");		//	1:是 0:不是
					customJsonObject.put(HTKeys.KEY_COIN_IMAGE_NAME, "gold.png");	//	货币图片文件名
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				
				//	支付
				float price = 1.0f;											//单价(元)
				int rate = 10;												//兑换比例
				int count = 1;												//个数
				int fixedMoney = 1;											//是否定额
				String unitName = "金币";									//货币单位
				String productId = null;									//产品ID
				String serverId = "s0";										//服务器ID
				String name = "金币x" + price * count *rate;					//商品名称
				String callbackUrl = null;									//回调地址(传null黑桃SDK会自己生成)
				String description = "购买" + price * count *rate + "金币";	//商品描述
				String cpExtendInfo = null;									//CP扩展信息
				String custom = customJsonObject.toString();				//自定义信息
				
				HTPayInfo payInfo = new HTPayInfo(price, rate, count, fixedMoney, unitName, productId, serverId, name, callbackUrl, description, cpExtendInfo, custom);
				HTGameProxy.doPay(payInfo, mPayListener);
			}
				break;
			case R.id.btn_exit: 
			{
				//	退出
				HTGameProxy.doExit(mExitListener);
			}
				break;
			case R.id.btn_startGame: 
			{
				//	开始游戏
				if (!HTGameProxy.onStartGame()) 
				{
					showToast("不可以开始游戏");
				}
				else 
				{
					showToast("进入游戏");
					
					//	进入游戏
					Map<String, String> parsMap = new HashMap<String, String>();
					
					/*
					parsMap.put(HTKeys.KEY_CP_SERVER_ID, "服务器ID");
					parsMap.put(HTKeys.KEY_CP_SERVER_NAME, "服务器名称"); 
					parsMap.put(HTKeys.KEY_ROLE_ID, "玩家角色 ID");
					parsMap.put(HTKeys.KEY_ROLE_NAME, "玩家角色名"); 
					parsMap.put(HTKeys.KEY_ROLE_LEVEL, "玩家角色等级"); 
					parsMap.put(HTKeys.KEY_IS_NEW_ROLE, "是否为新角色");
					*/
					
					HTGameProxy.onEnterGame(parsMap);
				}
			}
				break;
			case R.id.btn_levelChanged:
			{
				newLevel++;
				HTGameProxy.onGameLevelChanged(newLevel);
			}
				break;
			default:
				break;
			}
		}
	};

	private HTLoginListener mLoginListener = new HTLoginListener() {
		@Override
		public void onHTLoginFailed(HTError error) {
			// TODO Auto-generated method stub
			// 登录失败
			showToast("登录失败，error=" + error.message);
		}

		@Override
		public void onHTLoginCompleted(HTUser user,
				Map<String, String> customParsMap) {
			// TODO Auto-generated method stub
			// 登录成功
			showToast("登录成功");
		}
	};

	private HTLogoutListener mLogoutListener = new HTLogoutListener() {
		@Override
		public void onHTLogoutFailed(HTError error) {
			// TODO Auto-generated method stub
			// 注销失败
			showToast("注销失败，error=" + error.message);
		}

		@Override
		public void onHTLogoutCompleted(HTUser user, Map<String, String> customParsMap) {
			// TODO Auto-generated method stub
			// 注销成功
			showToast("注销成功");
		}
	};

	private HTPayListener mPayListener = new HTPayListener() {
		@Override
		public void onHTPayFailed(HTError error) {
			// TODO Auto-generated method stub
			// 支付失败
			showToast("支付失败，error=" + error.message);
		}

		@Override
		public void onHTPayCompleted(HTPayResult result) {
			// TODO Auto-generated method stub
			// 支付成功
			showToast("支付成功");
		}
	};

	private HTExitListener mExitListener = new HTExitListener() {
		@Override
		public void onHTThirdPartyExit() {
			// TODO Auto-generated method stub
			// 第三方 SDK 退出
			exitGame();
		}

		@Override
		public void onHTGameExit() {
			// TODO Auto-generated method stub
			// 游戏退出
			showExitGameDialog();
		}
	};
	
	private HTAppUpdateListener mAppUpdateListener = new HTAppUpdateListener() {
		
		@Override
		public void onHTAppUpdate(HTAppUpdateInfo appUpdateInfo) {
			// TODO Auto-generated method stub
			Log.d("HTProxyDemo", "appUpdateInfo=" + appUpdateInfo);
			showToast("客户端收到更新，" + appUpdateInfo);
		}
	};
	
	private void showToast(final String message) 
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void showExitGameDialog() 
	{
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage("退出要退出游戏吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() 
        {
			@Override
			public void onClick(DialogInterface dialog, int index) 
			{
				// TODO Auto-generated method stub
				exitGame();
			}
		});
        builder.setNegativeButton("取消", null);
        builder.create().show();
	}
	
	private void exitGame() 
	{
		this.finish();
		System.exit(0);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
		{
			showExitGameDialog();
		}
		
		return false;
	}
}
