package com.heitao.platform.statistics;

import java.util.Map;

import com.heitao.platform.common.HTPConsts;
import com.heitao.platform.common.HTPLog;
import com.heitao.platform.listener.HTPRequestListener;
import com.heitao.platform.model.HTPError;
import com.heitao.platform.model.HTPHttpEntity;
import com.heitao.platform.request.HTPNullResponeParser;

public class HTPStatistics 
{
	/**
	 * 统计
	 * @param label 标签
	 * @param map 统计数据
	 * */
	public static void statistics(String label, Map<String, String> map) 
	{
		String url = HTPConsts.API_URL + label;
		new HTPNullResponeParser().post(url, map, new HTPRequestListener() 
		{
			@Override
			public void onFailed(HTPError error) 
			{
				// TODO Auto-generated method stub
				HTPLog.e("提交统计数据失败，error=" + error.message);
			}
			
			@Override
			public void onCompleted(HTPHttpEntity httpEntity) 
			{
				// TODO Auto-generated method stub
				if (httpEntity.isCompleted) 
				{
					HTPLog.i("提交统计数据成功");
				}
				else 
				{
					HTPLog.e("提交统计数据失败，error=" + httpEntity.message);
				}
			}
		});
	}
}
