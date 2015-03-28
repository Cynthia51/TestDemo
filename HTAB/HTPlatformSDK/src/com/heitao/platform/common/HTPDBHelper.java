package com.heitao.platform.common;
import java.util.ArrayList;
import java.util.List;

import com.heitao.platform.model.HTPDBUser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HTPDBHelper extends SQLiteOpenHelper 
{
	private static HTPDBHelper mInstance = null;
	
	private final static String DB_NAME = "ht_platform_sdk.db";
	private final static int DB_VERSION = 1;
	
	private final String TABLE_USER = "user";
	
	private SQLiteDatabase mDatabase = null;
	
	public HTPDBHelper(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) 
	{
		// TODO Auto-generated method stub
		database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USER + " " +
						"(user_id CHAR(20), " +
						"user_name CHAR(50), " +
						"password CHAR(20))");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// TODO Auto-generated method stub
	}
	
	public static HTPDBHelper getInstance() 
	{
		if (null == mInstance) 
		{
			mInstance = new HTPDBHelper(HTPDataCenter.getInstance().mContext);
		}
		
		return mInstance;
	}
	
	public boolean isExist(HTPDBUser user) 
	{
		mDatabase = getReadableDatabase();
		Cursor cursor = mDatabase.query(TABLE_USER, new String[] { "user_name" }, "user_name = ?", new String[] { user.userName }, null, null, null);
		return cursor.moveToNext();
	}
	
	public int delete(HTPDBUser user) 
	{
		mDatabase = getReadableDatabase();
		return mDatabase.delete(TABLE_USER, "user_name = ?", new String[] { user.userName });
	}
	
	public void update(HTPDBUser user) 
	{
		if (isExist(user)) 
		{
			delete(user);
		}
		
		mDatabase = getReadableDatabase();
		
		ContentValues contentValues = new ContentValues();
		
		contentValues.put("user_id", user.userId.trim());
		contentValues.put("user_name", user.userName.trim());
		contentValues.put("password", user.password.trim());
		
		mDatabase.insert(TABLE_USER, null, contentValues);
	}
	
	public List<HTPDBUser> query() 
	{
		List<HTPDBUser> list = new ArrayList<HTPDBUser>();
		
		mDatabase = getReadableDatabase();
		Cursor cursor = mDatabase.query(TABLE_USER, null, null, null, null, null, null);
		while (cursor.moveToNext()) 
		{
			HTPDBUser user = new HTPDBUser();
			user.userId = cursor.getString(0).trim();
			user.userName = cursor.getString(1).trim();
			user.password = cursor.getString(2).trim();
			
			list.add(user);
		}
		
		return list;
	}
	
	public HTPDBUser queryWithUserId(String userId) 
	{
		if (HTPUtils.isNullOrEmpty(userId)) 
		{
			return null;
		}
		
		List<HTPDBUser> list = this.query();
		if (list != null && list.size() > 0) 
		{
			for (HTPDBUser dbUser : list) 
			{
				if (userId.trim().equals(dbUser.userId.trim())) 
				{
					return dbUser;
				}
			}
		}
		
		return null;
	}
	
	public HTPDBUser queryWithUserName(String userName) 
	{
		if (HTPUtils.isNullOrEmpty(userName)) 
		{
			return null;
		}
		
		List<HTPDBUser> list = this.query();
		if (list != null && list.size() > 0) 
		{
			for (HTPDBUser dbUser : list) 
			{
				if (userName.trim().equals(dbUser.userName.trim())) 
				{
					return dbUser;
				}
			}
		}
		
		return null;
	}
	
	public void close() 
	{
		if (null != mDatabase) 
		{
			this.mDatabase.close();
		}
	}
}
