package com.heitao.platform.request;

import org.json.JSONException;
import org.json.JSONObject;

import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPHttpEntity;

public class HTPHttpRequestParser extends HTPHttpRequest
{
	protected HTPHttpEntity mHttpEntity = new HTPHttpEntity();
	
	@Override
	protected void onParse(JSONObject jsonObject) 
	{
		// TODO Auto-generated method stub
		try 
		{
			int code = jsonObject.getInt("errno");
			mHttpEntity.code = code + "";
			mHttpEntity.message = jsonObject.getString("errmsg");
			mHttpEntity.isCompleted = (code == 0);
			mHttpEntity.jsonObject = jsonObject;
			
			if (!jsonObject.isNull("data")) 
			{
				mHttpEntity.dataObject = jsonObject.getJSONObject("data");
			}
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			if (mListener != null) 
			{
				mListener.onFailed(HTPError.getRequestParseError());
			}
		}
	}
}