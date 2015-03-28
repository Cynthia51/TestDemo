package com.heitao.platform.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

//import com.alipay.sdk.app.PayTask;
import com.heitao.platform.R;
import com.heitao.platform.api.HTPlatform.HTPlatformDirection;
import com.heitao.platform.common.HTPDataCenter;
import com.heitao.platform.common.HTPLog;
import com.heitao.platform.common.HTPUtils;
import com.heitao.platform.listener.HTPPayListener;
import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPPayInfo;
import com.heitao.platform.pay.center.HTPSDKPayCenter;
import com.heitao.platform.pay.center.HTPSDKPayCenter.PayChannel;
import com.heitao.platform.pay.center.HTPSDKPayCenter.YeepayChannel;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
@SuppressWarnings("deprecation")
public class HTPPayActivity extends HTPBaseActivity
{	
	private HTPPayListener mPayListener = null;
	private HTPPayInfo mPayInfo = null;
	private PayChannel mPayChannel = PayChannel.none;
	private YeepayChannel mYeepayChannel = YeepayChannel.none;
	private int mCardValue = 20;
	private boolean mIsPayCompleted = false;
	private PopupWindow mPopupWindow = null;
	
	private List<HTPPayButton> mButtonList = new ArrayList<HTPPayButton>();
	private ArrayList<Integer> mSupportList = null;
	private Button nextButton = null;
	private boolean mPriorityUseAliay = false;
	private static final int ALIPAY_SDK_CHECK_FLAG = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mPayListener = HTPDataCenter.getInstance().mPayListener;
		mPayInfo = HTPDataCenter.getInstance().mPayInfo;
		
		try 
		{
			if (mSupportList != null) 
			{
				mSupportList.clear();
				mSupportList = null;
			}
			
			mSupportList = new ArrayList<Integer>();
			
			JSONObject object = new JSONObject(getIntent().getExtras().getString("data"));
			String support = object.getString("pay_ch_config");
			String supports[] = support.split(",");
			for (String string : supports) 
			{
				mSupportList.add(Integer.parseInt(string));
			}
			
			HTPLog.d("mSupportList=" + mSupportList);
			
			mPriorityUseAliay = object.getInt("priority_use_aliay") == 1 ? true : false;
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		initPayView();
		
		//	优先使用支付宝
		if (mPriorityUseAliay) 
		{
			HTPLog.d("mPriorityUseAliay");
			Runnable checkRunnable = new Runnable() 
			{
				@Override
				public void run() 
				{
//					PayTask payTask = new PayTask(HTPPayActivity.this);
//					boolean isExist = payTask.checkAccountIfExist();
//					HTPLog.d("checkAccountIfExist=" + isExist);
//					
//					if (isExist) 
//					{
//						Message message = new Message();
//						message.what = ALIPAY_SDK_CHECK_FLAG;
//						mHandler.sendMessage(message);
//					}
				}
			};
			Thread checkThread = new Thread(checkRunnable);
			checkThread.start();
		}
	}
	
	@Override
	protected void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		
		if (PayChannel.mo9 == mPayChannel) 
		{
			finish();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
		{
			showExitPayDialog();
		}
		
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		HTPSDKPayCenter.getInstance().onActivityResult(mPayChannel, requestCode, resultCode, data);
	}
	
	private Handler mHandler = new Handler()
	{
        @Override
        public void handleMessage(Message msg) 
        {
        	//mOnClickListener.onClick(nextButton);
        	HTPSDKPayCenter.getInstance().pay(HTPPayActivity.this, mPayChannel, mPayInfo, mSDKPayListener);
        }
	};
	
	/**
	 * 显示退出支付确认框
	 * */
	private void showExitPayDialog() 
	{
		Builder alertDialog = new AlertDialog.Builder(HTPPayActivity.this);
		alertDialog.setTitle("提示");
		alertDialog.setMessage("确定要放弃支付吗？");
		
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// TODO Auto-generated method stub
				
				HTPLog.d("which=" + which);
				dialog.dismiss();
				
				if (-1 == which) 
				{
					if (mPayListener != null && !mIsPayCompleted) 
					{
						mPayListener.onPayFailed(HTPError.getPayCancelError());
					}
					
					finish();
				}
			}
		};
		
		alertDialog.setPositiveButton("确定", listener);
		alertDialog.setNegativeButton("取消", listener);
		alertDialog.show();
	}
	
	/**
	 * 初始化PayView
	 * */
	private void initPayView() 
	{
		if (HTPDataCenter.getInstance().mUser == null) 
		{
			HTPUtils.doShowToast("请先登录");
			
			if (HTPDataCenter.getInstance().mPayListener != null) 
			{
				HTPDataCenter.getInstance().mPayListener.onPayFailed(HTPError.getNotLoginError());
			}
			
			finish();
			return;
		}
		
		//支付页面
		Button leftButton = null;
		Button rightButton = null;
		
		TextView productNameTextView = null;
		TextView productDescriptionTextView = null;
		TextView productAmountTextView = null;
		
		HTPPayButton alipayPayButton = null;
		HTPPayButton unionPayButton = null;
		HTPPayButton mo9PayButton = null;
		HTPPayButton yeepayPayButton = null;
		HTPPayButton weixinPayButton = null;
		HTPPayButton heitaoPayButton = null;
		
		HTPlatformDirection direction = HTPDataCenter.getInstance().mDirection;
		if (direction == HTPlatformDirection.Portrait) 
		{
			setContentView(R.layout.htp_activity_pay_portrait);
			
			leftButton = (Button) findViewById(R.id.btn_pay_left_portrait);
			rightButton = (Button) findViewById(R.id.btn_pay_right_portrait);
			
			productNameTextView = (TextView) findViewById(R.id.txt_product_name_portrait);
			productDescriptionTextView = (TextView) findViewById(R.id.txt_product_description_portrait);
			productAmountTextView = (TextView) findViewById(R.id.txt_product_amount_portrait);
			
			alipayPayButton = (HTPPayButton) findViewById(R.id.htppbtn_alipay_portrait);
			unionPayButton = (HTPPayButton) findViewById(R.id.htppbtn_unionpay_portrait);
			mo9PayButton = (HTPPayButton) findViewById(R.id.htppbtn_mo9_portrait);
			yeepayPayButton = (HTPPayButton) findViewById(R.id.htppbtn_yeepay_portrait);
			weixinPayButton = (HTPPayButton) findViewById(R.id.htppbtn_weixinpay_portrait);
			heitaoPayButton = (HTPPayButton) findViewById(R.id.htppbtn_heitao_portrait);
			
			nextButton = (Button) findViewById(R.id.btn_pay_next_portrait);
		}
		else 
		{
			setContentView(R.layout.htp_activity_pay);
			
			leftButton = (Button) findViewById(R.id.btn_pay_left);
			rightButton = (Button) findViewById(R.id.btn_pay_right);
			
			productNameTextView = (TextView) findViewById(R.id.txt_product_name);
			productDescriptionTextView = (TextView) findViewById(R.id.txt_product_description);
			productAmountTextView = (TextView) findViewById(R.id.txt_product_amount);
			
			alipayPayButton = (HTPPayButton) findViewById(R.id.htppbtn_alipay);
			unionPayButton = (HTPPayButton) findViewById(R.id.htppbtn_unionpay);
			mo9PayButton = (HTPPayButton) findViewById(R.id.htppbtn_mo9);
			yeepayPayButton = (HTPPayButton) findViewById(R.id.htppbtn_yeepay);
			weixinPayButton = (HTPPayButton) findViewById(R.id.htppbtn_weixinpay);
			heitaoPayButton = (HTPPayButton) findViewById(R.id.htppbtn_heitao);
			
			nextButton = (Button) findViewById(R.id.btn_pay_next);
		}
		
		leftButton.setOnClickListener(mOnClickListener);
		rightButton.setOnClickListener(mOnClickListener);
		
		productNameTextView.setText("产品名称：" + mPayInfo.name);
		productDescriptionTextView.setText("描述：" + mPayInfo.description);
		productAmountTextView.setText("充值金额：" + mPayInfo.amount + "元");
		
		{
			alipayPayButton.setImageResource(R.drawable.htp_pay_alipay_icon);
			alipayPayButton.setText("支付宝");
			alipayPayButton.setOnClickListener(mOnClickListener);
			alipayPayButton.setVisibility(!isValidPayChannel(0) ? View.GONE : View.VISIBLE);
			mButtonList.add(alipayPayButton);
			
			unionPayButton.setImageResource(R.drawable.htp_pay_unionpay_icon);
			unionPayButton.setText("银联支付");
			unionPayButton.setOnClickListener(mOnClickListener);
			unionPayButton.setVisibility(!isValidPayChannel(1) ? View.GONE : View.VISIBLE);
			mButtonList.add(unionPayButton);
			
			mo9PayButton.setImageResource(R.drawable.htp_pay_mo9_icon);
			mo9PayButton.setText("先玩后付");
			mo9PayButton.setOnClickListener(mOnClickListener);
			mo9PayButton.setVisibility(!isValidPayChannel(2) ? View.GONE : View.VISIBLE);
			mButtonList.add(mo9PayButton);
			
			yeepayPayButton.setImageResource(R.drawable.htp_pay_yeepay_icon);
			yeepayPayButton.setText("易宝支付");
			yeepayPayButton.setOnClickListener(mOnClickListener);
			yeepayPayButton.setVisibility(!isValidPayChannel(3) ? View.GONE : View.VISIBLE);
			mButtonList.add(yeepayPayButton);
			
			weixinPayButton.setImageResource(R.drawable.htp_pay_weixin_icon);
			weixinPayButton.setText("微信支付");
			weixinPayButton.setOnClickListener(mOnClickListener);
			weixinPayButton.setVisibility(!isValidPayChannel(4) ? View.GONE : View.VISIBLE);
			//weixinPayButton.setEnabled(false);
			mButtonList.add(weixinPayButton);
			
			heitaoPayButton.setImageResource(R.drawable.htp_pay_heitao_icon);
			heitaoPayButton.setText("黑桃支付");
			heitaoPayButton.setOnClickListener(mOnClickListener);
			heitaoPayButton.setVisibility(!isValidPayChannel(5) ? View.GONE : View.VISIBLE);
			//heitaoPayButton.setEnabled(false);
			mButtonList.add(heitaoPayButton);
			
			//	默认选中
			setSelected(R.id.htppbtn_alipay, R.id.htppbtn_alipay_portrait);
			mPayChannel = PayChannel.alipay;
		}
		
		nextButton.setOnClickListener(mOnClickListener);
	}
	
	/**
	 * 设置渠道按钮选中状态
	 * @param id1 ID1
	 * @param id2 ID2
	 * */
	private void setSelected(int id1, int id2) 
	{
		for (HTPPayButton button : mButtonList) 
		{
			if (button.getId() == id1 || button.getId() == id2) 
			{
				button.setSelected(true);
			}
			else 
			{
				button.setSelected(false);
			}
		}
	}
	
	/**
	 * 充值方式是否有效
	 * */
	private boolean isValidPayChannel(Integer customId) 
	{
		//0:支付宝	1:银联	2:先玩后付	3:易宝支付	4:微信支付	5:黑桃支付
		//1 黑桃币 2 支付宝 3 银联 4 易宝(移动) 5 易宝(联通) 6 易宝(电信) 7 微信 8 mo9支付
		if (null == mSupportList || mSupportList.size() <= 0) 
		{
			return true;
		}
		
		for (Integer support : mSupportList) 
		{
			if (support == 1 && customId == 5) 
			{
				return true;
			}
			else if (support == 2 && customId == 0) 
			{
				return true;
			}
			else if (support == 3 && customId == 1) 
			{
				return true;
			}
			else if ((support == 4 || support == 5 || support == 6) && customId == 3) 
			{
				return true;
			}
			else if (support == 7 && customId == 4) 
			{
				return true;
			}
			else if (support == 8 && customId == 2) 
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 渠道按钮点击回调
	 * */
	private OnClickListener mOnClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View view) 
		{
			// TODO Auto-generated method stub
			if (view.getId() == R.id.btn_pay_left || view.getId() == R.id.btn_pay_left_portrait) 
			{
				//	返回按钮
				showExitPayDialog();
			}
			else if (view.getId() == R.id.btn_pay_right || view.getId() == R.id.btn_pay_right_portrait) 
			{
				// 右边按钮
				
			}
			else if (view.getId() == R.id.htppbtn_alipay || view.getId() == R.id.htppbtn_alipay_portrait)
			{
				// 支付宝
				HTPLog.d("支付宝");
				setSelected(R.id.htppbtn_alipay, R.id.htppbtn_alipay_portrait);
				mPayChannel = PayChannel.alipay;
			}
			else if (view.getId() == R.id.htppbtn_unionpay || view.getId() == R.id.htppbtn_unionpay_portrait)
			{
				//	银联支付
				HTPLog.d("银联支付");
				setSelected(R.id.htppbtn_unionpay, R.id.htppbtn_unionpay_portrait);
				mPayChannel = PayChannel.unionpay;
			}
			else if (view.getId() == R.id.htppbtn_mo9 || view.getId() == R.id.htppbtn_mo9_portrait)
			{
				//	先玩后付
				HTPLog.d("先玩后付");
				setSelected(R.id.htppbtn_mo9, R.id.htppbtn_mo9_portrait);
				mPayChannel = PayChannel.mo9;
			}
			else if (view.getId() == R.id.htppbtn_yeepay || view.getId() == R.id.htppbtn_yeepay_portrait)
			{
				//	易宝支付
				HTPLog.d("易宝支付");
				setSelected(R.id.htppbtn_yeepay, R.id.htppbtn_yeepay_portrait);
				mPayChannel = PayChannel.yeepay;
			}
			else if (view.getId() == R.id.htppbtn_weixinpay || view.getId() == R.id.htppbtn_weixinpay_portrait)
			{
				//	微信支付
				HTPLog.d("微信支付");
				setSelected(R.id.htppbtn_weixinpay, R.id.htppbtn_weixinpay_portrait);
				mPayChannel = PayChannel.weixin;
			}
			else if (view.getId() == R.id.htppbtn_heitao || view.getId() == R.id.htppbtn_heitao_portrait)
			{
				//	黑桃支付
				HTPLog.d("黑桃支付");
				setSelected(R.id.htppbtn_heitao, R.id.htppbtn_heitao_portrait);
				mPayChannel = PayChannel.heitao;
			}
			else if (view.getId() == R.id.btn_pay_next || view.getId() == R.id.btn_pay_next_portrait)
			{
				//	下一步
				HTPLog.d("下一步");
				if (mPayChannel != PayChannel.none) 
				{
					if (mPayChannel == PayChannel.yeepay) 
					{
						showPopupWindow();
					}
					else 
					{
						HTPSDKPayCenter.getInstance().pay(HTPPayActivity.this, mPayChannel, mPayInfo, mSDKPayListener);
					}
				}
				else 
				{
					HTPUtils.doShowToast("请选择支付方式");
				}
			}
		}
	};
	
	/**
	 * 渠道SDK支付结果回调(from:HTPlatformSDKPayCenter)
	 * */
	private HTPPayListener mSDKPayListener = new HTPPayListener() 
	{
		@Override
		public void onPayFailed(HTPError error) 
		{
			// TODO Auto-generated method stub
			HTPUtils.doShowToast(error.message);
			
			if (mPayChannel == PayChannel.alipay) 
			{
				//	支付宝
				HTPLog.e("支付宝支付失败，msg=" + error.message);
			}
			else if (mPayChannel == PayChannel.unionpay) 
			{
				//	银联支付
				HTPLog.e("银联支付失败，msg=" + error.message);
			}
			else if (mPayChannel == PayChannel.mo9) 
			{
				//	先玩后付
				HTPLog.e("先玩后付支付失败，msg=" + error.message);
			}
			else if (mPayChannel == PayChannel.yeepay) 
			{
				//	易宝支付
				HTPLog.e("易宝支付失败，msg=" + error.message);
			}
			else if (mPayChannel == PayChannel.weixin) 
			{
				//	微信支付
				HTPLog.e("微信支付失败，msg=" + error.message);
			}
			else if (mPayChannel == PayChannel.heitao) 
			{
				//	黑桃支付
				HTPLog.e("黑桃支付失败，msg=" + error.message);
			}
			
			mIsPayCompleted = false;
		}
		
		@Override
		public void onPayCompleted() 
		{
			// TODO Auto-generated method stub
			if (mPayChannel == PayChannel.alipay) 
			{
				//	支付宝
				HTPLog.d("支付宝支付成功");
			}
			else if (mPayChannel == PayChannel.unionpay) 
			{
				//	银联支付
				HTPLog.d("银联支付成功");
			}
			else if (mPayChannel == PayChannel.mo9) 
			{
				//	先玩后付
				HTPLog.d("先玩后付支付成功");
			}
			else if (mPayChannel == PayChannel.yeepay) 
			{
				//	易宝支付
				HTPLog.d("易宝支付成功----信息提交成功");
				
				if (mPopupWindow != null) 
				{
					mPopupWindow.dismiss();
				}
				
				HTPUtils.doShowToast("信息已提交，请等待确认后发货");
			}
			else if (mPayChannel == PayChannel.weixin) 
			{
				//	微信支付
				HTPLog.d("微信支付成功");
			}
			else if (mPayChannel == PayChannel.heitao) 
			{
				//	黑桃支付
				HTPLog.d("黑桃支付成功");
			}
			
			mIsPayCompleted = true;
			
			if (mPayListener != null) 
			{
				mPayListener.onPayCompleted();
			}
			
			finish();
		}
	};
	
	/**易宝支付	begin	*/
	
	/**
	 * 易宝充值卡对话框
	 * */ 
	private void showPopupWindow() 
    {
		if (mPopupWindow != null && mPopupWindow.isShowing()) 
		{
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
		
        final WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.5f; //0.0-1.0
    	
    	LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件  
        View view = inflater.inflate(R.layout.htp_pay_view_yeepay, null);
        // 创建PopupWindow对象  LayoutParams.WRAP_CONTENT
        mPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
        // 需要设置一下此参数，点击外边可消失
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        //设置点击窗口外边窗口消失
        mPopupWindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击 
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAtLocation(view,  Gravity.CENTER,  Gravity.CENTER,  Gravity.CENTER);
        mPopupWindow.setOnDismissListener(new OnDismissListener() 
        {
			@Override
			public void onDismiss() 
			{
				// TODO Auto-generated method stub
				layoutParams.alpha = 1.0f;
				getWindow().setAttributes(layoutParams);
			}
		});
        getWindow().setAttributes(layoutParams);
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        mPopupWindow.update();
        
    	final Button CHLButton = (Button) view.findViewById(R.id.btn_yeepay_CHL);
    	final Button CHUButton = (Button) view.findViewById(R.id.btn_yeepay_CHU);
    	final Button CHAButton = (Button) view.findViewById(R.id.btn_yeepay_CHA);
    	
    	final EditText cardNoEditText = (EditText) view.findViewById(R.id.txt_card_no);
    	final EditText cardPWDEditText = (EditText) view.findViewById(R.id.txt_card_pwd);
    	final Button cardMonryButton = (Button) view.findViewById(R.id.btn_card_money);
    	cardMonryButton.setText(mCardValue + "");
    	
    	//	卡面值选择
    	cardMonryButton.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View view) 
			{
				// TODO Auto-generated method stub
				
				String []items = null;
				if (mYeepayChannel == YeepayChannel.CHL) 
				{
					String CHLs[] = {"10", "20", "30", "50", "100", "300", "500"};
					items = CHLs;
				}
				else if (mYeepayChannel == YeepayChannel.CHU) 
				{
					String CHUs[] = {"20", "30", "50", "100", "300", "500"};
					items = CHUs;
				}
				else if (mYeepayChannel == YeepayChannel.CHA) 
				{
					String CHAs[] = {"50", "100"};
					items = CHAs;
				}
				
				final String []finalItems = items;
				
				AlertDialog.Builder builder = new AlertDialog.Builder(HTPPayActivity.this);
		        builder.setTitle("请选择充值卡面值金额");
		        builder.setItems(finalItems, new DialogInterface.OnClickListener() 
		        {  
		            public void onClick(DialogInterface dialog, int which) 
		            {
		            	mCardValue = Integer.parseInt(finalItems[which]);
		            	cardMonryButton.setText(finalItems[which]);
		            }
		        });
		        builder.setCancelable(false);
		        builder.create().show();
			}
		});
    	
    	OnClickListener channelOnClickListener = new OnClickListener() 
    	{
    		@Override
    		public void onClick(View view) 
    		{
   				if (view.getId() == R.id.btn_yeepay_CHL) 
				{
   					CHLButton.setBackgroundResource(R.drawable.htp_pay_yeepay_chl_selected);
   					CHUButton.setBackgroundResource(R.drawable.htp_pay_yeepay_chu);
   					CHAButton.setBackgroundResource(R.drawable.htp_pay_yeepay_cha);
   					
   					mYeepayChannel = YeepayChannel.CHL;
				}
   				else if (view.getId() == R.id.btn_yeepay_CHU) 
   				{
   					CHLButton.setBackgroundResource(R.drawable.htp_pay_yeepay_chl);
   					CHUButton.setBackgroundResource(R.drawable.htp_pay_yeepay_chu_selected);
   					CHAButton.setBackgroundResource(R.drawable.htp_pay_yeepay_cha);
   					
   					mYeepayChannel = YeepayChannel.CHU;
				}
   				else if (view.getId() == R.id.btn_yeepay_CHA) 
   				{
   					CHLButton.setBackgroundResource(R.drawable.htp_pay_yeepay_chl);
   					CHUButton.setBackgroundResource(R.drawable.htp_pay_yeepay_chu);
   					CHAButton.setBackgroundResource(R.drawable.htp_pay_yeepay_cha_selected);
   					
   					mYeepayChannel = YeepayChannel.CHA;
				}
   				else 
   				{
   					mYeepayChannel = YeepayChannel.none;
				}
    		}
    	};
        
        CHLButton.setOnClickListener(channelOnClickListener);
        CHUButton.setOnClickListener(channelOnClickListener);
        CHAButton.setOnClickListener(channelOnClickListener);
        
        //	默认选中
		CHLButton.setBackgroundResource(R.drawable.htp_pay_yeepay_chl_selected);
		CHUButton.setBackgroundResource(R.drawable.htp_pay_yeepay_chu);
		CHAButton.setBackgroundResource(R.drawable.htp_pay_yeepay_cha);
		mYeepayChannel = YeepayChannel.CHL;
			
    	OnClickListener okAndCancelOnClickListener = new OnClickListener() 
    	{
    		@Override
    		public void onClick(View view) 
    		{
    			if (view.getId() == R.id.btn_yeepay_ok) 
    			{
       				if (mYeepayChannel != YeepayChannel.none) 
       				{
    					String cardNo = cardNoEditText.getText().toString().trim();
    					String cardPWD = cardPWDEditText.getText().toString().trim();
    					
    					if (HTPUtils.isNullOrEmpty(cardNo)) 
    					{
    						HTPUtils.doShowToast("充值卡号不能为空");
    						return;
    					}
    					
    					if (HTPUtils.isNullOrEmpty(cardPWD)) 
    					{
    						HTPUtils.doShowToast("充值卡密码不能为空");
    						return;
    					}
    					
    					HTPSDKPayCenter.getInstance().yeepay(HTPPayActivity.this, mYeepayChannel, cardNo, cardPWD, mCardValue, mPayInfo, mSDKPayListener);
    				}
				}
    			else if (view.getId() == R.id.btn_yeepay_cancel) 
    			{
    				mPopupWindow.dismiss();
				}
    		}
    	};
        
        Button okButton = (Button) view.findViewById(R.id.btn_yeepay_ok);
        okButton.setOnClickListener(okAndCancelOnClickListener);
        
        Button cancelButton = (Button) view.findViewById(R.id.btn_yeepay_cancel);
        cancelButton.setOnClickListener(okAndCancelOnClickListener);
    }
	
	/**易宝支付	end	*/
}