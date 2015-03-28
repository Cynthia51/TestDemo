package com.heitao.platform.activity;

import com.heitao.platform.R;
import com.heitao.platform.common.HTPLog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class HTPWebViewActivity extends HTPBaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.htp_pay_webview);
		
		WebView webView = (WebView) findViewById(R.id.htp_webview);
		webView.setWebViewClient(new HTPWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.clearCache(true);
		webView.clearHistory();
		
		String url = this.getIntent().getStringExtra("URL");
		if (url != null && url.length() > 0 && URLUtil.isHttpsUrl(url)) 
		{
			webView.loadUrl(url);
		}
		else 
		{
			HTPLog.e("url为空或者无效");
			finish();
		}
	}
	
	class HTPWebViewClient extends WebViewClient 
	{
		/*
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) 
		{
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			
			HTPUtils.doShowProgressDialog(HTPWebViewActivity.this, "加载中···");
		}
		
		@Override
		public void onPageFinished(WebView view, String url) 
		{
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			
			HTPUtils.doCancelProgressDialog(HTPWebViewActivity.this);
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) 
		{
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
			
			HTPUtils.doCancelProgressDialog(HTPWebViewActivity.this);
		}
		*/
	}
}
