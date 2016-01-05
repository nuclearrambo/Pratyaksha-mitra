package com.pratyaksha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PeriodicTaskReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent i = new Intent(context, RSSService.class);
		context.startService(i);
	
	}

}
