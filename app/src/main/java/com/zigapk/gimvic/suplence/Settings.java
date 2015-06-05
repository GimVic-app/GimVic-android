package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ziga on 10/18/14.
 */
public class Settings {

    public static ChosenRazredi getRazredi(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString("razredi", "{}");
        Gson gson = new Gson();
        return gson.fromJson(json, ChosenRazredi.class);

    }

    public static void setRazredi(ChosenRazredi razredi, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(razredi);
        editor.putString("razredi", json);
        editor.commit();

        final Context tempContext = context;
        new Thread() {
            @Override
            public void run() {
                Urnik.parseUrnik(tempContext);
                setTrueUrnikParsed(true, tempContext);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Data.renderData(tempContext);
                    }
                });
            }
        }.start();
    }
    public static String getProfesor(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("ucitelj", "");
    }

    public static int getLastAppVersionNumber(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt("lastAppVersion", 1);
    }

    public static boolean getAnketa2015Done(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("anketa-2015-done", false);
    }

    public static boolean getAdmin(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("admin", false);
    }

    public static boolean wasProfesorsPassEntered(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("profesorsPassAlreadyEntered", false);
    }

    public static boolean isHybridParsed(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("isHybridParsed", false);
    }

    public static boolean areSuplenceParsed(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("areSuplenceParsed", false);
    }

    public static void setUcitelj(String ucitelj, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("ucitelj", ucitelj);
        editor.commit();
    }

    public static void setMalicaDownloading(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("malicaDownloading", value);
        editor.commit();
    }

    public static void setlastAppVersion(int version, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("lastAppVersion", version);
        editor.commit();
    }

    public static void setProfesorsPassEntered(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("profesorsPassAlreadyEntered", value);
        editor.commit();
    }

    public static void setHybridParsed(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("isHybridParsed", value);
        editor.commit();
    }

    public static void setSuplenceParsed(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("areSuplenceParsed", value);
        editor.commit();
    }

    public static void resetSafetyCounter(Context context){
        setSafetyCounter(0, context);
    }

    public static void setSafetyCounter(int value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("safetyCounter", value);
        editor.commit();
    }

    public static int getMode(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int mode = prefs.getInt("mode", 0);

        return mode;
    }

    public static int getSafetyCounter(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt("safetyCounter", 0);
    }

    public static void setAdmin(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("admin", value);
        editor.commit();
    }

    public static void setMode(int mode, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("mode", mode);
        editor.commit();
    }

    public static void increaseSafetyCounter(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("safetyCounter", getSafetyCounter(context) + 1);
        editor.commit();
    }

    public static int getUserMode(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt("userMode", 1);
    }

    public static void setUserMode(int mode, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("userMode", mode);
        editor.commit();
    }

    public static boolean isUrnikParsed(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getBoolean("isDataParsed", false);
    }

    public static boolean isTrueUrnikParsed(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getBoolean("isTrueUrnikParsed", false);
    }

    public static boolean isUrnikDownloaded(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getBoolean("isDataDownloaded", false);
    }

    public static String getUrnikHash(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getString("urnikHash", "a");
    }

    public static boolean areSuplenceDownloaded(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getBoolean("areSuplenceDownloaded", false);
    }

    public static boolean isFirstOpened(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getBoolean("alreadyOpened", true);
    }

    public static void setUrnikParsed(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("isDataParsed", value);
        editor.commit();
    }

    public static void setAnketa2015Done(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("anketa-2015-done", value);
        editor.commit();
    }

    public static void setTrueUrnikParsed(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("isTrueUrnikParsed", value);
        editor.commit();
    }

    public static void setUrnikDownloaded(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("isDataDownloaded", value);
        editor.commit();
    }

    public static void setUrnikHash(String value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("urnikHash", value);
        editor.commit();
    }

    public static void setSuplenceDownloaded(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("areSuplenceDownloaded", value);
        editor.commit();
    }

    public static void setFirstOpened(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("alreadyOpened", value);
        editor.commit();
    }

    public static void clearSharedPrefs(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public static int getMalicaMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getInt("malicaMode", JedilnikModes.MALICA_NAVADNA);
    }

    public static void setMalicaMode(int mode, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("malicaMode", mode);
        editor.commit();
    }

    public static int getKosiloMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getInt("kosiloMode", JedilnikModes.KOSILO_NAVADNO);
    }

    public static void setKosiloMode(int mode, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("kosiloMode", mode);
        editor.commit();
    }
}

class Mode{
    public static int MODE_HYBRID = 0;
    public static int MODE_URNIK = 1;
    public static int MODE_SUPLENCE = 2;
}

class UserMode{
    public static int MODE_UCITELJ = 0;
    public static int MODE_UCENEC = 1;
}

class JedilnikModes {
    public static final int MALICA_NAVADNA = 0;
    public static final int MALICA_VEGSPERUTNINO = 1;
    public static final int MALICA_VEGETARIANSKA = 2;
    public static final int KOSILO_NAVADNO = 0;
    public static final int KOSILO_VEGETARIANSKO = 2;
}

class ChosenRazredi{
    ArrayList<String> razredi = new ArrayList<String>();
}
