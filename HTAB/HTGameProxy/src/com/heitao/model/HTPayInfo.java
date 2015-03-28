package com.heitao.model;

import java.util.HashMap;
import java.util.Map;

import com.heitao.common.HTLog;
import com.heitao.common.HTUtils;

public class HTPayInfo 
{
	public float price;							//单价
	public int rate;							//兑换比例
	public int count;							//个数
	public int fixedMoney;						//是否定额
	public String unitName;						//货币单位
	public String productId;					//产品ID
	public String serverId;						//服务器ID
	public String name;							//商品名称
	public String callbackUrl;					//回调地址
	public String description;					//商品描述
	public String cpExtendInfo;					//CP扩展信息
	public String custom;						//自定义信息
	
	public static String KEY_PRICE = "price";
	public static String KEY_RATE = "rate";
	public static String KEY_COUNT = "count";
	public static String KEY_FIXED_MONEY = "fixed_money";
	public static String KEY_UNIT_NAME = "unit_name";
	public static String KEY_PRODUCT_ID = "product_id";
	public static String KEY_SERVER_ID = "server_id";
	public static String KEY_NAME = "name";
	public static String KEY_CALLBACK_URL = "callback_url";
	public static String KEY_DESCRIPTION = "description";
	public static String KEY_CP_EXTEND_INFO = "cp_extend_info";
	public static String KEY_CUSTOM = "custom";
	
	public HTPayInfo()
	{
		init();
	}
	
	/**
	 * 初始化支付信息
	 * @param _price 			单价
	 * @param _rate 			兑换比例
	 * @param _count 			个数
	 * @param _unitName 		货币单位
	 * @param _productId 		产品ID
	 * @param _serverId 		服务器ID
	 * @param _name 			商品名称
	 * @param _callbackUrl 		回调地址
	 * @param _description 		商品描述
	 * @param _cpExtendInfo 	CP扩展信息
	 * @param _custom 			自定义信息
	 * */
	public HTPayInfo(float _price, int _rate, int _count, int _fixedMoney, String _unitName, String _productId,
			String _serverId, String _name, String _callbackUrl, String _description, String _cpExtendInfo, String _custom)
	{
		price = _price;
		rate = _rate;
		count = _count;
		fixedMoney = _fixedMoney;
		unitName = _unitName;
		productId = _productId;
		serverId = _serverId;
		name = _name;
		callbackUrl = _callbackUrl;
		description = _description;
		cpExtendInfo = _cpExtendInfo;
		custom = _custom;
	}
	
	/**
	 * 初始化支付信息
	 * @param pars 参数集合
	 */
	public HTPayInfo(String pars)
	{
		Map<String, String> map = HTUtils.parsStringToMap(pars, true);
		if (map != null) 
		{
			try {
				price = Float.parseFloat(map.get(KEY_PRICE));
				rate = Integer.parseInt(map.get(KEY_RATE));
				count = Integer.parseInt(map.get(KEY_COUNT));
				fixedMoney = Integer.parseInt(map.get(KEY_FIXED_MONEY));
				unitName = map.get(KEY_UNIT_NAME);
				productId = map.get(KEY_PRODUCT_ID);
				serverId = map.get(KEY_SERVER_ID);
				name = map.get(KEY_NAME);
				callbackUrl = map.get(KEY_CALLBACK_URL);
				description = map.get(KEY_DESCRIPTION);;
				cpExtendInfo = map.get(KEY_CP_EXTEND_INFO);
				custom = map.get(KEY_CUSTOM);
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				init();
				HTLog.e("初始化支付信息pars异常，error=" + e.getMessage());
			}
		}
	}
	
	/**
	 * 初始化支付信息
	 * @param map 参数集合
	 */
	public HTPayInfo(Map<String, String> map)
	{
		if (map != null) 
		{
			try {
				price = Float.parseFloat(map.get(KEY_PRICE));
				rate = Integer.parseInt(map.get(KEY_RATE));
				count = Integer.parseInt(map.get(KEY_COUNT));
				fixedMoney = Integer.parseInt(map.get(KEY_FIXED_MONEY));
				unitName = map.get(KEY_UNIT_NAME);
				productId = map.get(KEY_PRODUCT_ID);
				serverId = map.get(KEY_SERVER_ID);
				name = map.get(KEY_NAME);
				callbackUrl = map.get(KEY_CALLBACK_URL);
				description = map.get(KEY_DESCRIPTION);
				cpExtendInfo = map.get(KEY_CP_EXTEND_INFO);
				custom = map.get(KEY_CUSTOM);
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				init();
				HTLog.e("初始化支付信息map异常，error=" + e.getMessage());
			}
		}
	}
	
	private void init() 
	{
		price = 1;
		rate = 10;
		count = 1;
		fixedMoney = 1;
		unitName = "";
		productId = "";
		serverId = "";
		name = "";
		callbackUrl = "";
		description = "";
		cpExtendInfo = "";
		custom = "";
	}
	
	private Map<String, String> getPropertiesMap() 
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(KEY_PRICE, price + "");
		map.put(KEY_RATE, rate + "");
		map.put(KEY_COUNT, count + "");
		map.put(KEY_FIXED_MONEY, fixedMoney + "");
		map.put(KEY_UNIT_NAME, unitName);
		map.put(KEY_PRODUCT_ID, productId);
		map.put(KEY_SERVER_ID, serverId);
		map.put(KEY_NAME, name);
		map.put(KEY_CALLBACK_URL, callbackUrl);
		map.put(KEY_DESCRIPTION, description);
		map.put(KEY_CP_EXTEND_INFO, cpExtendInfo);
		map.put(KEY_CUSTOM, custom);
		
		return map;
	}
	
	public String toString() 
	{
		return HTUtils.mapToParsString(this.getPropertiesMap(), false);
	}
	
	public String toEncodeString() 
	{
		return HTUtils.mapToParsString(this.getPropertiesMap(), true);
	}
}
