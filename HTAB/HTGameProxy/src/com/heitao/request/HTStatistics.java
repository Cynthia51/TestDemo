package com.heitao.request;

import java.util.Map;

import org.json.JSONObject;

import com.heitao.channel.HTChannelDispatcher;
import com.heitao.common.HTConsts;
import com.heitao.common.HTDataCenter;
import com.heitao.common.HTJSONHelper;
import com.heitao.common.HTLog;
import com.heitao.common.HTUtils;
import com.heitao.listener.HTRequestListener;

public class HTStatistics extends HTBaseRequest
{
	/**
	 * 统计角色信息
	 * */
	public void doStatisticsRoleInfo(Map<String, String> parsMap) 
	{
		String baseUrl = HTConsts.BASE_URL;
		if (HTUtils.isNullOrEmpty(baseUrl)) 
		{
			HTLog.e("请先设置SDK请求URL");
			return;
		}
		
		if (parsMap == null || parsMap.isEmpty()) 
		{
			HTLog.e("统计角色信息参数为空");
			return;
		}
		
		//	添加公共参数
		parsMap = this.addPublicParsMap(parsMap);
		
		String requestUrl = baseUrl + HTDataCenter.getInstance().mGameInfo.shortName + "/role/" + HTChannelDispatcher.getInstance().getChannelKey() + "?";
		requestUrl += HTUtils.mapToParsString(parsMap, true);
		HTLog.d("统计角色信息开始请求，requestUrl=" + requestUrl);
		
		//	发送请求
		this.get(requestUrl, new HTRequestListener() 
		{
			@Override
			public void onSuccess(String response) 
			{
				HTLog.d("统计角色信息返回成功，response=" + response);
				
				try {
					JSONObject responseObject = new JSONObject(response);
					
					//	all
					int errorCode = HTJSONHelper.getInt(responseObject, HTConsts.KEY_ERROR_CODE);
					String errorMessage = HTJSONHelper.getString(responseObject, HTConsts.KEY_ERROR_MESSAGE);
					
					if (errorCode == HTConsts.REQUEST_COMPLETED && !responseObject.isNull(HTConsts.KEY_ERROR_CODE)) 
					{
						HTLog.i("统计角色信息提交成功");
					}
					else 
					{
						HTLog.e("统计角色信息解析失败，error=" + errorMessage);
					}
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
					HTLog.e("统计角色信息解析失败，error=" + e.toString());
				}
			}
			
			@Override
			public void onFailure(Throwable e) 
			{ 
				HTLog.e("统计角色信息请求失败，error=" + e.toString());
			}
		});
	}
}
