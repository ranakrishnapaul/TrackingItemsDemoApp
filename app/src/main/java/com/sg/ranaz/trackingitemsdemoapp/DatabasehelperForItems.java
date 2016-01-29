/* Author: Rana
 * email id: ranakrishnapaul@gmail.com
 * skype id: rana.krishna.paul
 * File Name: DatabasehelperForItems.java
 * This is a Database helper class for Items
 * 
 *  */ 



package com.sg.ranaz.trackingitemsdemoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DatabasehelperForItems extends SQLiteOpenHelper
{
	
	//DB Name & Version
	private static final String DATABASE_NAME = "AllItemsDatabase.db";
	private static final int DATABASE_VERSION = 1; 
                                                     
	//Table name
	public static final String TABLE_NAME = "items_table";
	
	//Field/Column name
	public static final String ITEM_NAME = "itemname";
	public static final String ITEM_DESCRIPTION = "itemdescription";
	public static final String ITEM_IMAGE = "itemimage";
	public static final String ITEM_LOCATION = "itemlocation";
	public static final String ITEM_COST = "itemcost";


	public DatabasehelperForItems(Context context) {
		super(context,DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table " + TABLE_NAME + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "
		+ ITEM_NAME + " text not null, "
		+ ITEM_DESCRIPTION + " text not null, "
		+ ITEM_IMAGE + " text not null, "
	 	+ ITEM_LOCATION + " text not null, "
		+ ITEM_COST + " text not null );";
       Log.i("onCreate:SQL query: ",sql);
       
       try{
    	   db.execSQL(sql);
    	   Log.i("Table created: ", "Success");
       }
       catch(Exception e){
    	   Log.i("Table creation failed: ", e.toString());
       }

	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method
		if (oldVersion >= newVersion)
			return;
        
		Log.i("EventsData", "onUpgrade:");
		db.execSQL("DROP TABLE IF EXISTS items_table");
		onCreate(db);

	}

	
}