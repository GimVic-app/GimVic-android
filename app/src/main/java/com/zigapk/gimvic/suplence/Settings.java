package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;

/**
 * Created by ziga on 10/18/14.
 */
public class Settings {


    //TODO: make it read real data
    public static String getRazred(Context context){
        return "2B";
    }

    //TODO: make it return real data
    public static String getProfesor(Context context){
        return "Rudolf";
    }

    //TODO: make it work
    public static void resetSafetyCounter(){

    }

    public static int getMode(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String mode = prefs.getString("mode", "");

        int parsed = Integer.parseInt(mode);
        if(parsed == Mode.MODE_SUPLENCE){
            return Mode.MODE_SUPLENCE;
        }else if(parsed == Mode.MODE_URNIK){
            return Mode.MODE_URNIK;
        }else return Mode.MODE_HYBRID;
    }

    public static void setMode(int mode, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("mode", mode);
        editor.commit();
    }

    public static int getUserMode(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String mode = prefs.getString("userMode", "");

        int parsed = Integer.parseInt(mode);

        if(parsed == UserMode.MODE_UCITELJ){
            return UserMode.MODE_UCITELJ;
        }else return UserMode.MODE_UCENEC;
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

    public static boolean isUrnikDownloaded(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getBoolean("isDataDownloaded", false);
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

    public static void setUrnikDownloaded(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("isDataDownloaded", value);
        editor.commit();
    }

    public static void setFirstOpened(boolean value, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("alreadyOpened", value);
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
