/* Author: Rana
 * email id: ranakrishnapaul@gmail.com
 * skype id: rana.krishna.paul
 * File Name: DBManipulationForItems.java
 * All type of DB Manipulation such as 'CRUD' operations Handling Class
 * 
 *  */ 




package com.sg.ranaz.trackingitemsdemoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;



public class DBManipulationForItems
{

	//Variables,Objects Declaration
	SQLiteDatabase db;
	DatabasehelperForItems dbhelperForItems;
	Context context;
	Cursor cur;
	public  String columns[] = {"_ID","itemname","itemdescription","itemimage","itemlocation","itemcost"};

	public static boolean deleteSuccess  = false;
	public static long ROWID = 0;
	public static long TOTALUPDATEDROW = 0;
	
	
	
	
	//Insertion method for adding items into the Table
	public void insertRecordForItems(DatabasehelperForItems dbhelperForItems1,String itemname,String itemdescription,
			String itemimage,String itemlocation,String itemcost)
	{
		
		db = dbhelperForItems1.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DatabasehelperForItems.ITEM_NAME,itemname);
		cv.put(DatabasehelperForItems.ITEM_DESCRIPTION,itemdescription);
		cv.put(DatabasehelperForItems.ITEM_IMAGE,itemimage);
		cv.put(DatabasehelperForItems.ITEM_LOCATION,itemlocation);
		cv.put(DatabasehelperForItems.ITEM_COST,itemcost);

		try{
			ROWID = db.insert(DatabasehelperForItems.TABLE_NAME, null, cv);
			Log.i("Data insertion:", "Success");
			Log.i("ROWID:", String.valueOf(ROWID));
		}
		catch(Exception e){
	    	   Log.i("Data Insertion Failed: ", e.toString());
			   ROWID = -1;
			   Log.i("ROWID:", String.valueOf(ROWID));
	       }
		
		db.close();
		
	}
	
	
	//Get records from the Table
	public Cursor getRecordsForItems(DatabasehelperForItems dbhelperForItems1)
	{
		
        db = dbhelperForItems1.getReadableDatabase();
		
		cur = db.query(DatabasehelperForItems.TABLE_NAME, columns,
				null, null, null, null, "_ID");  //Order by ID

		return cur;
	}
	

	//Delete record from the Table by ID
	public void deleteRecordFromItemsById(DatabasehelperForItems dbhelperForItems1,long id)
	{

		db = dbhelperForItems1.getWritableDatabase();

		try{
			db.execSQL("delete from "+DatabasehelperForItems.TABLE_NAME+" where "+ BaseColumns._ID+" = '"+id+"' " + ";");
			Log.i("Data deletion: ", "Success");
			deleteSuccess  = true;
			
		}catch(Exception e){
			Log.i("Data deletion Failed: ", e.toString());
			deleteSuccess  = false;
		}

		db.close();

	}

	//Update the Table
	public void updateRecord(DatabasehelperForItems dbhelperForItems1, long id, String itemname,String itemdescription,
							 String itemimage,String itemlocation,String itemcost){

		db = dbhelperForItems1.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(DatabasehelperForItems.ITEM_NAME,itemname);
		cv.put(DatabasehelperForItems.ITEM_DESCRIPTION,itemdescription);
		cv.put(DatabasehelperForItems.ITEM_IMAGE,itemimage);
		cv.put(DatabasehelperForItems.ITEM_LOCATION,itemlocation);
		cv.put(DatabasehelperForItems.ITEM_COST, itemcost);

		try{
			TOTALUPDATEDROW = db.update(DatabasehelperForItems.TABLE_NAME, cv, BaseColumns._ID +"="+ id, null);
			Log.i("Data Update: ", "Success");
			Log.i("TOTALUPDATEDROW:", String.valueOf(TOTALUPDATEDROW));
		}
		catch(Exception e){
			Log.i("Data Update Failed: ", e.toString());
			TOTALUPDATEDROW = 0;
		}

		db.close();

	}


	//Close the opened DB
	public void close(DatabasehelperForItems dbhelperForItems1)
	{
		/*cur.close();
		db.close();
		dbhelperForItems1.close();*/
		
	}
	
}