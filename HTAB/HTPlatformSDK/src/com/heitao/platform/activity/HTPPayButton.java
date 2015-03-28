package com.heitao.platform.activity;

import com.heitao.platform.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HTPPayButton extends LinearLayout
{
	private ImageView mImageView = null;
	private TextView mTextView = null;
	
	private boolean mIsSelected = false;
	
	public HTPPayButton(Context context) 
	{
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public HTPPayButton(Context context, AttributeSet attrs) 
	{  
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.htp_pay_channel_button, this);
          
        //实例化控件对象  
        mImageView = (ImageView) findViewById(R.id.btn_channel_icon);
        mTextView = (TextView) findViewById(R.id.txt_channel_title);
    }
	
	/** 
     * 设置图片资源 
     * @param resId 
     */  
    public void setImageResource(int resId) 
    {
    	mImageView.setImageResource(resId);
    }
      
    /** 
     * 设置要显示的文字 
     * @param text 
     */  
    public void setText(String strId) 
    {  
        mTextView.setText(strId);
    }
    
    /**
     * 设置选中状态
     * @param isSelected 是否选中
     * */
    public void setSelected(boolean isSelected) 
    {
    	mIsSelected = isSelected;
    	if (mIsSelected) 
    	{
    		//mImageView.setBackgroundColor(android.graphics.Color.parseColor("#60c2e7"));
    		mTextView.setTextColor(android.graphics.Color.parseColor("#ed6001"));
		}
    	else 
    	{
    		//mImageView.setBackgroundColor(android.graphics.Color.parseColor("#00000000"));
    		mTextView.setTextColor(Color.BLACK);
		}
	}
    
    /**
     * 获取选中状态
     * @return 是否选中
     * */
    public boolean getSelected() 
    {
		return mIsSelected;
	}
}
