package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Calendar;

/**
 * Created by ziga on 28.12.2014.
 */
public class Jedilnik {

    private static void downloadJedilnik(final Context context){
        if(Internet.isOnline(context)){
            Settings.setMalicaDownloading(true, context);
            Settings.setKosiloDownloading(true, context);

            new Thread() {
                @Override
                public void run() {
                    downloadMalica(context);
                }
            }.start();
            new Thread() {
                @Override
                public void run() {
                    downloadKosilo(context);
                }
            }.start();
        }
    }

    public static Bitmap getMalica(Context context){
        String name = Settings.getLastMalicaName(context);
        if(name != null) return Files.loadBitmap(name, context);
        else return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_web);
    }

    public static Bitmap getKosilo(Context context){
        String name = Settings.getLastKosiloName(context);
        if(name != null) return Files.loadBitmap(name, context);
        else return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_web);
    }


    private static void downloadKosilo(Context context) {
        //TODO: set right url
        Bitmap kosilo = Internet.downloadBitmap("http://img.docstoccdn.com/thumb/orig/111527579.png");
        if(kosilo != null){
            Calendar calendar = Calendar.getInstance();
            String name = getKosiloNameForDate(calendar);
            Files.saveBitmap(kosilo, name, context);
            Settings.setLastKosiloName(name, context);
            Settings.setKosiloDownloading(false, context);
        }
    }

    private static void downloadMalica(Context context){
        //TODO: set right url
        Bitmap malica = Internet.downloadBitmap("http://img.docstoccdn.com/thumb/orig/111527579.png");
        if(malica != null){
            Calendar calendar = Calendar.getInstance();
            String name = getMalicaNameForDate(calendar);
            Files.saveBitmap(malica, name, context);
            Settings.setLastMalicaName(name, context);
            Settings.setMalicaDownloading(false, context);
        }
    }

    private static String getMalicaNameForDate(Calendar calendar) {
        return "Malica_" + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + ".png";
    }

    private static String getKosiloNameForDate(Calendar calendar) {
        return "Koisilo_" + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + ".png";
    }
}
