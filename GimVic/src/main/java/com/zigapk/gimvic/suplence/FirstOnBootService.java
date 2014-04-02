package com.zigapk.gimvic.suplence;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class FirstOnBootService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TO DO SOMETHING USEFULL!!!!


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("enable_sync", true)){
            Calendar cal = Calendar.getInstance();
            Intent intent2 = new Intent(this, MyService.class);
            PendingIntent pintent = PendingIntent.getService(this, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1800000, pintent);

            Intent i= new Intent(getApplicationContext(), MyService.class);
            getApplicationContext().startService(i);
        }

        return Service.START_NOT_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        /*TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");*/
        return null;
    }
}
