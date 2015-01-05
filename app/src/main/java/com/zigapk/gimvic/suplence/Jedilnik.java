package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ziga on 28.12.2014.
 */
public class Jedilnik {

    private static boolean refreshing = false;

    public static void refresh(final Context context){
        if(((Internet.isOnline(context) && !jedilnikDownloadedToday(context) ) || !jedilnikValid(context)) && !refreshing){
            new Thread() {
                @Override
                public void run() {
                    refreshing = true;
                    onJedilnikDownloadStarted(context);
                    downloadJedilnik(context);
                    onJedilnikReady(context);
                    refreshing = false;
                }
            }.start();
        }
    }
    private static boolean jedilnikDownloadedToday(Context context){
        Date now = Calendar.getInstance().getTime();
        Date last = Settings.getJedilnikLastDownloadedDate(context).getTime();
        int days = Days.daysBetween(new DateTime(last), new DateTime(now)).getDays();
        if(days > 0){
            return true;
        }else return false;
    }

    private static boolean jedilnikValid(final Context context) {
        boolean valid = true;
        Calendar now = Calendar.getInstance();
        JedilnikData data = new Gson().fromJson(JedilnikData.getJson(context), JedilnikData.class);

        if(data != null){
            if(data.malica != null && data.kosilo != null){
                if(data.malica.filename != null && data.kosilo.filename != null){
                    if(data.malica.filename != "" && data.kosilo.filename != ""){
                        //for malica
                        String fromString = data.malica.fromdate;
                        Calendar from = getCalForString(fromString);
                        String toString = data.malica.todate;
                        Calendar to = getCalForString(toString);

                        if(!(from.before(now) && to.after(now))) valid = false;

                        //for kosilo
                        fromString = data.kosilo.fromdate;
                        from = getCalForString(fromString);
                        toString = data.kosilo.todate;
                        to = getCalForString(toString);

                        if(!(from.before(now) && to.after(now))) valid = false;

                    }else valid = false;
                }else valid = false;
            }else valid = false;
        }else valid = false;

        return valid;
    }

    private static Calendar getCalForString(String date) {
        Calendar calendar = Calendar.getInstance();
        String[] values = date.split("-");
        calendar.set(Calendar.YEAR, Integer.parseInt(values[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(values[1]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(values[2]));
        return calendar;
    }

    public static Bitmap getMalica(Context context){
        try {
            Bitmap bitmap = Files.loadBitmap("Malica.png", context);
            if(bitmap != null && jedilnikValid(context)) return bitmap;
            else return BitmapFactory.decodeResource(context.getResources(), R.drawable.jedilnik_empty);
        }catch (Exception e){
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.jedilnik_empty);
        }
    }

    public static Bitmap getKosilo(Context context){
        try {
            Bitmap bitmap = Files.loadBitmap("Kosilo.png", context);
            if(bitmap != null && jedilnikValid(context)) return bitmap;
            else return BitmapFactory.decodeResource(context.getResources(), R.drawable.jedilnik_empty);
        }catch (Exception e){
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.jedilnik_empty);
        }
    }


    private static void downloadJedilnik(final Context context) {
        Settings.setMalicaDownloading(true, context);
        Settings.setKosiloDownloading(true, context);
        String json = Internet.getTextFromUrl("http://www.gimvic.org/delovanjesole/solske_sluzbe_in_solski_organi/solska_prehrana/jedilnik_data/");
        JedilnikData data = new Gson().fromJson(json, JedilnikData.class);

        if(data != null){
            if(data.malica != null && data.kosilo != null){
                if(data.malica.filename != "" && data.malica.filename != null && data.kosilo.filename != "" && data.kosilo.filename != null){
                    final String malicaUrl = "http://www.gimvic.org/delovanjesole/solske_sluzbe_in_solski_organi/solska_prehrana/files/" + data.malica.filename.replace(".pdf",".png");
                    Settings.setMalicaDownloading(true, context);
                    new Thread() {
                        @Override
                        public void run() {
                            Internet.downloadAndSaveBitmap(malicaUrl, "Malica.png", context);
                            Settings.setMalicaDownloading(false, context);
                        }
                    }.start();

                    final String kosiloUrl = "http://www.gimvic.org/delovanjesole/solske_sluzbe_in_solski_organi/solska_prehrana/files/" + data.kosilo.filename.replace(".pdf",".png");
                    Settings.setKosiloDownloading(true, context);
                    new Thread() {
                        @Override
                        public void run() {
                            Internet.downloadAndSaveBitmap(kosiloUrl, "Kosilo.png", context);
                            Settings.setKosiloDownloading(false, context);
                        }
                    }.start();
                }else {
                    Files.saveBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.jedilnik_empty), "Malica.png", context);
                    Files.saveBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.jedilnik_empty), "Kosilo.png", context);
                }
            }else {
                Files.saveBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.jedilnik_empty), "Malica.png", context);
                Files.saveBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.jedilnik_empty), "Kosilo.png", context);
            }
        }else {
            Files.saveBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.jedilnik_empty), "Malica.png", context);
            Files.saveBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.jedilnik_empty), "Kosilo.png", context);
        }


        Files.writeToFile("JedilnikData.json", json, context);
        Settings.setJedilnikLastDownloadedDate(Calendar.getInstance(), context);
    }

    public static void onJedilnikReady(final Context context) {
        //run on ui thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                try {
                    Main.jedilnikProgressBar.setVisibility(View.GONE);
                    Main.jedilnikImage.setVisibility(View.VISIBLE);

                    if(Main.malica) Main.renderMalica(false);
                    else Main.renderKosilo();
                }catch (Exception e){}
            }
        });
    }

    public static void onJedilnikDownloadStarted(final Context context) {
        //run on ui thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                try {
                    Main.jedilnikProgressBar.setVisibility(View.VISIBLE);
                    Main.jedilnikImage.setVisibility(View.GONE);
                } catch (Exception e) {
                }
            }
        });
    }
}

class JedilnikData {
    JedilnikDataItem malica;
    JedilnikDataItem kosilo;

    public static String getJson(Context context) {
        String json = Files.getFileValue("JedilnikData.json", context);
        if(json == null) return "{}";
        return json;
    }
}

class JedilnikDataItem {
    String filename;
    String todate;
    String fromdate;
}
