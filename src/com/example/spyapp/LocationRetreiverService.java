package com.example.spyapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class LocationRetreiverService extends Service
{
private static final String TAG = "LocationRetreiverService";
private LocationManager mLocationManager = null;
private static final int LOCATION_INTERVAL = 10000;
private static final float LOCATION_DISTANCE = 	0;
boolean isNetworkEnabled;
boolean isGPSEnabled;
boolean networkwasoff=true;
boolean GPSwasoff=true;
boolean useGPS=false;

String BEST_PROVIDER;

LocationListener[] mLocationListeners = new LocationListener[] 
		{
      new LocationListener(LocationManager.GPS_PROVIDER),
        new LocationListener(LocationManager.NETWORK_PROVIDER)
		};


@Override
public IBinder onBind(Intent arg0)
{
    return null;
}


@Override
public void onCreate()
{
    
			Log.e(TAG, "onCreate");
			initializeLocationManager();
			Location location;
			isNetworkEnabled = mLocationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			isGPSEnabled = mLocationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			
			if (isNetworkEnabled == true) {

				mLocationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
						LOCATION_DISTANCE, mLocationListeners[1]);
				//     location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				networkwasoff=false;
			}
			if (isGPSEnabled == true) {

				mLocationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, LOCATION_INTERVAL,
						LOCATION_DISTANCE, mLocationListeners[0]);
				//     location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				GPSwasoff=false;
			}
			
		
	
       
  
}
@Override
public void onDestroy()
{
    Log.e(TAG, "onDestroy");
    super.onDestroy();
    if (mLocationManager != null) {
        for (int i = 0; i < mLocationListeners.length; i++) {
            try {
                mLocationManager.removeUpdates(mLocationListeners[i]);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
    }
} 
private void initializeLocationManager() {
    Log.e(TAG, "initializeLocationManager");
    if (mLocationManager == null) {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }
}
private class LocationListener implements android.location.LocationListener{
	Location LastLocation=null;
	Location finalLocation=null;
	Location GPSLocation;
	Location NetworkLocation;
	long latesttimestamp=0;
	
    DatabaseHandler databasehandler1;
    
    public LocationListener(String provider)
    {
    	//just initializing and creating empty location containers
        Log.d(TAG, "LocationListener " + provider);
        if (provider==LocationManager.GPS_PROVIDER)
        	{
        	
        	GPSLocation = new Location(LocationManager.GPS_PROVIDER);
            LastLocation=new Location(LocationManager.GPS_PROVIDER);
          //  LastLocation.setAccuracy(1000);
            }
        else if (provider == LocationManager.NETWORK_PROVIDER)
        {
        	NetworkLocation = new Location(LocationManager.NETWORK_PROVIDER);
          //  LastLocation=new Location(LocationManager.NETWORK_PROVIDER);
         //   LastLocation.setAccuracy(1000);
        }
        
    }
    @Override
    public void onLocationChanged(Location location)
    {
    	
    	useGPS=false;
      	databasehandler1 = new DatabaseHandler(
				LocationRetreiverService.this);
      	
      	Log.d(TAG,"NETWORK STATUS:"+Boolean.toString(isNetworkEnabled));
     	Log.d(TAG,"GPS STATUS:"+Boolean.toString(isGPSEnabled));

      	
    	isNetworkEnabled = mLocationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		isGPSEnabled = mLocationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		//This is to reiniate the locationlistner in case the network
    	//or GPS were switched off at the time of installation.
	    if (networkwasoff && isNetworkEnabled)
	    {
	    	
	    	mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
					LOCATION_DISTANCE, mLocationListeners[1]);
			//     location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			networkwasoff=false;
	    }

	    if (GPSwasoff && isGPSEnabled)
	    {
	    	Log.d(TAG,"GPS WAS off, It' snow ON");
	    	mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, LOCATION_INTERVAL,
					LOCATION_DISTANCE, mLocationListeners[0]);
			//     location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			GPSwasoff=false;
	    }
		
        Log.d(TAG, "onLocationChanged: " + location);
        Log.d(TAG, "TIMESTAMP NEW"+Long.toString(location.getTime()));
 //       Log.d(TAG, "TIMESTAMP OLD"+Long.toString(LastLocation.getTime()));

        if(location.getProvider().equals(LocationManager.GPS_PROVIDER))
        {
              if (location.getTime()>latesttimestamp)
              {
            	  Log.d(TAG,"TEST1");
        	  GPSLocation.set(location);
        	  LastLocation=GPSLocation;
        	  latesttimestamp=location.getTime();
        	  useGPS=true;
              databasehandler1.insertlocintodb(
           			LastLocation.getTime(),
           			LastLocation.getLatitude(),
           			LastLocation.getLongitude());
                	  
              }
        	/*  if (isBetterLocation(GPSLocation)==true)
        	  {
        		  Log.d(TAG,"Udpdating database with this new GPS Time");
        		  //update the database with this location value and timestamp;
        	  }*/
        	 
        }
        else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
        {
        	if (useGPS==false)
            {
        	Log.d(TAG,"TEST2");
        	Log.d(TAG,Boolean.toString(useGPS));
        	NetworkLocation.set(location);
       	     LastLocation=NetworkLocation;
          	latesttimestamp=location.getTime();
                databasehandler1.insertlocintodb(
  	     			LastLocation.getTime(),
  	     			LastLocation.getLatitude(),
  	     			LastLocation.getLongitude());
  	    
            }
/*
      	  if(isBetterLocation(NetworkLocation)==true)
      			  {
    		  Log.d(TAG,"Udpdating database with this new Network Time");

    		  //update the database with this location value and timestamp;

      			  }*/
        }

   //    	Log.d(TAG,"Current Location Details"+
    //   	    	Double.toString(LastLocation.getLatitude())+":"+
     //  	    			Double.toString(LastLocation.getLongitude())+":TIME:"
     //  	    			+Long.toString(LastLocation.getTime()));
      	

    }
    @Override
    public void onProviderDisabled(String provider)
    {
        Log.d(TAG, "onProviderDisabled: " + provider);            
    }
    @Override
    public void onProviderEnabled(String provider)
    {
        Log.d(TAG, "onProviderEnabled: " + provider);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.d(TAG, "onStatusChanged: " + provider);
    }
    
    
    //Not used now, we can use this later once the code is complete, to refine
    //the time algorithm
    public boolean isBetterLocation(Location newlocation)
    {
    	//check if the new location is more than 1 minute newer than the old location
    	
    	Log.d(TAG,"isBetterLocation called");
    	boolean isMoreAccurate=false;
    	boolean isNewer=false;
    	boolean isFromSameProvider=false;
    	boolean isSignificantlyLessAccurate = false;
    	
    	int accuracyDelta =(int)( newlocation.getAccuracy()-LastLocation.getAccuracy());
    	  	
    	if (accuracyDelta<0)
    	{
    		isMoreAccurate=true;
    	}

    	if (accuracyDelta>20)
    	{
    		isSignificantlyLessAccurate=true;
    	}
    	
    	
    	if((newlocation.getTime()-LastLocation.getTime()>10000))
    	{
    		isNewer=true;
    	}
    	
    	if(newlocation.getProvider()==LastLocation.getProvider())
    	{
    		isFromSameProvider=true;
    	}
    	
    	Log.d(TAG,"Current Location Details"+
    	Double.toString(LastLocation.getLatitude())+":"+
    			Double.toString(LastLocation.getLongitude())+":TIME:"
    			+Long.toString(LastLocation.getTime()));
 
    /*	if(isMoreAccurate)
    	{
    		LastLocation=newlocation;
    		return true;
    	}*/
    	if(isNewer && isMoreAccurate)
    	{
    		Log.d(TAG,"HERE1");
    		LastLocation=newlocation;
    		return true;
    	}
    	else if(isNewer && isFromSameProvider && !isMoreAccurate)
    	{
    		Log.d(TAG,"HERE2");
    		LastLocation=newlocation;
    		return true;
    	}
    	return false;
    }
} 
}

