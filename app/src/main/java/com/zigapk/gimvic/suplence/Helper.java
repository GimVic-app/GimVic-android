package com.zigapk.gimvic.suplence;

import android.content.Context;

import com.zigapk.gimvic.suplence.exceptions.CouldNotReachServerException;

import java.util.Date;

/**
 * Created by zigapk on 18.12.2015.
 */
public class Helper {

    //this method is run by broadcast receivers
    public static void broadcastVoid(final Context context, boolean fromAlarmReciever) {
        if (!fromAlarmReciever) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (Settings.isDataConfigured(context) && Internet.isOnline(context))
                            new Data().download(context);
                    } catch (CouldNotReachServerException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            //filter every second call (every 20 min)
            String mins = Integer.toString((new Date()).getMinutes());
            if (mins.length() == 2 && (mins.startsWith("1") || mins.startsWith("3") || mins.startsWith("5"))) {
                broadcastVoid(context, false);
            }
        }
    }
}
