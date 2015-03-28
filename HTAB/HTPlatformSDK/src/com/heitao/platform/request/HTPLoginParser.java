package com.heitao.platform.request;

import org.json.JSONException;
import org.json.JSONObject;

import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPUser;

public class HTPLoginParser extends HTPHttpRequestParser
{
	@Override
	protected void onParse(JSONObject jsonObject) 
	{
		// TODO Auto-generated method stub
		super.onParse(jsonObject);
		
		if (mHttpEntity.isCompleted) 
		{
			try 
			{
				JSONObject dataJsonObject = jsonObject.getJSONObject("data");
				HTPUser user = new HTPUser();
				user.userId = dataJsonObject.getString("uid");
				user.userName = dataJsonObject.getString("username");
				user.time = dataJsonObject.getString("time");
				user.token = dataJsonObject.getString("token");
				
				mHttpEntity.object = user;
				
				if (mListener != null) 
				{
					mListener.onCompleted(mHttpEntity);
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
		else 
		{
			if (mListener != null) 
			{
				mListener.onCompleted(mHttpEntity);
			}
		}
	}
}
