package com.heitao.platform.request;

import org.json.JSONObject;

public class HTPNullResponeParser extends HTPHttpRequestParser
{
	@Override
	protected void onParse(JSONObject jsonObject) 
	{
		// TODO Auto-generated method stub
		super.onParse(jsonObject);
		
		if (mListener != null) 
		{
			mListener.onCompleted(mHttpEntity);
		}
	}
}
