package com.heitao.platform.model;

public class HTPUser 
{
	public String userId;
	public String userName;
	public String time;
	public String token;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "userId=" + userId + "&userName=" + userName + "&time=" + time + "&token=" + token;
	}
}
