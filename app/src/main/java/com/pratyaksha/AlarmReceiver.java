package com.pratyaksha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Nuclearrambo on 11/29/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final long INTERVAL_FIVE_MINUTES = 1000 * 60 * 5;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, RSSService.class);

        context.startService(service);
    }
}