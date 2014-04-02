package com.zigapk.gimvic.suplence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent /*arg1*/) {

        Intent i= new Intent(context, FirstOnBootService.class);
        context.startService(i);

    }
}
