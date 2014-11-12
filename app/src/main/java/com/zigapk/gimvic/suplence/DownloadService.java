package com.zigapk.gimvic.suplence;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloadService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Internet.isOnline(getApplicationContext())){
            Suplence.downloadSuplence();

            if(Internet.onWifi(getApplicationContext())){
                Urnik.downloadUrnik(getApplicationContext());
            }
        }

        return Service.START_NOT_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
