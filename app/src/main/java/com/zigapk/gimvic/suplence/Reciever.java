package com.zigapk.gimvic.suplence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Reciever extends BroadcastReceiver {
    public Reciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, DownloadService.class));
    }
}
