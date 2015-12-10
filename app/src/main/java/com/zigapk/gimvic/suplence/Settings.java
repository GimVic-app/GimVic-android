package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zigapk on 10.12.2015.
 */
public class Settings {
    public static int getLastAppVersionNumber(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt("lastAppVersion", Main.CURRENT_APP_VERSION);
    }

    public static boolean getAdmin(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("admin", false);
    }

    public static boolean isDataConfigured(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("dataConfigured", false);
    }

    public static void setDataConfigured(boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("dataConfigured", value);
        editor.commit();
    }

    public static void setAdmin(boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("admin", value);
        editor.commit();
    }

    public static void resetSafetyCounter(Context context) {
        setSafetyCounter(0, context);
    }

    public static void setSafetyCounter(int value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("safetyCounter", value);
        editor.commit();
    }

    public static int getSafetyCounter(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt("safetyCounter", 0);
    }

    public static boolean wasProfesorsPassEntered(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("profesorsPassAlreadyEntered", false);
    }

    public static void setProfesorsPassEntered(boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("profesorsPassAlreadyEntered", value);
        editor.commit();
    }

    public static void clearAllData(Context context) {

        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().clear().commit();
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static void setChosenOptions(ChoosenOptions choosenOptions, Context context) {
        Files.writeToFile("choosenOptions.json", new Gson().toJson(choosenOptions), context);
    }

    public static ChoosenOptions getChosenOptions(Context context) {
        try {
            String json = Files.getFileValue("choosenOptions.json", context);
            return new Gson().fromJson(json, ChoosenOptions.class);
        } catch (Exception e) {
            return new ChoosenOptions();
        }
    }

}

class ChoosenOptions {
    ArrayList<String> classes = new ArrayList<>();
    String snack = "";
    String lunch = "";
}
