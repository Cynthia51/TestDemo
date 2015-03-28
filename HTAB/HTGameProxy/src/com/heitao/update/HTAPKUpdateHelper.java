package com.heitao.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.heitao.common.HTLog;
import com.heitao.model.HTAppUpdateInfo;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class HTAPKUpdateHelper 
{
    private static final int DOWNLOADING = 0;
    private static final int DOWNLOAD_END = 1;
    private static final int DOWNLOAD_ERROR = 2;
	
	private static ProgressDialog mProgressDialog = null;
	private static int mProgress = 0;
	private static Context mContext = null;
	private static HTAppUpdateInfo mAppUpdateInfo = null;
	private static String mAPKSavePath = null;
	
    static Handler handler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg) 
        {
            super.handleMessage(msg);
            switch (msg.what) 
            {
			case DOWNLOADING:
				mProgressDialog.setProgress(mProgress);
				break;
			case DOWNLOAD_END:
				mProgressDialog.cancel();
				installAPK(mContext, mAPKSavePath);
				break;
			case DOWNLOAD_ERROR:
				mProgressDialog.cancel();
				
				mProgressDialog.setTitle("下载失败");
				mProgressDialog.setMessage("网络链接异常，下载失败！");
				mProgressDialog.setCancelable(true);
				mProgressDialog.setCanceledOnTouchOutside(true);
				mProgressDialog.show();
				break;
			}
        }
    };
	
    /**
     * @param context	context
     * @param apkFilePath APK文件路径
     * */
	public static void installAPK(Context context, String apkFilePath)
	{
	    Intent intent = new Intent(Intent.ACTION_VIEW); 
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    File file = new File(apkFilePath);
	    try
	    {
	    	Uri data = Uri.fromFile(file);
	    	intent.setDataAndType(data, "application/vnd.android.package-archive"); 
	    	context.startActivity(intent);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	AlertDialog.Builder dialog = new Builder(context);
	    	dialog.setTitle("安装失败");
	    	dialog.setMessage("更新文件不存在或者已经损坏！");
	    	dialog.show();
	    }
	}
	
	/**
	 * 下载APK
	 * @param context context
	 * @param appUpdateInfo	APK更新信息
	 * */
	public static void downloadAPK(Context context, String apkSavePath, HTAppUpdateInfo appUpdateInfo)
	{
		if (null == appUpdateInfo) 
		{
			HTLog.e("下载信息为空");
			return;
		}
		
		mContext = context;
		mAppUpdateInfo = appUpdateInfo;
		mAPKSavePath = apkSavePath;
		
		deleteApk();
		
		final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setTitle("游戏更新");
		mProgressDialog.setMessage(mAppUpdateInfo.content);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();
		
        new Thread()
        {
            public void run()
            {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(mAppUpdateInfo.apkURL);
                HttpResponse response;
                try 
                {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    InputStream is =  entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null)
                    {
                    	NetworkInfo info; 
                        File file = new File(mAPKSavePath);
                        fileOutputStream = new FileOutputStream(file);
                        byte[] b = new byte[1024];
                        int charb = -1;
                        int count = 0;
                        while ( (charb = is.read(b)) != -1 )
                        {
                            info = manager.getActiveNetworkInfo();
                            if (info != null && info.getState() == NetworkInfo.State.CONNECTED)
                            {
                            	handler.sendEmptyMessage(DOWNLOADING);
                            }
                            else
                            {
                            	handler.sendEmptyMessage(DOWNLOAD_ERROR);
                            }
                        	count += charb;
                        	mProgress = (int) (((float) count / length) * 100);
                            fileOutputStream.write(b, 0, charb);
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null)
                    {
                        fileOutputStream.close();
                    }
                    handler.sendEmptyMessage(DOWNLOAD_END);
                }  
                catch (Exception e) 
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }
	
	private static void deleteApk() 
	{
		try 
		{
			File file = new File(mAPKSavePath);
			if (file.exists()) 
			{
				file.delete();
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		}
	}
}
