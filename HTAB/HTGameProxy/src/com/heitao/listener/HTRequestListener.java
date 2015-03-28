package com.heitao.listener;

public abstract class HTRequestListener 
{
	public abstract void onSuccess(String response);
	public abstract void onFailure(Throwable error);
}
