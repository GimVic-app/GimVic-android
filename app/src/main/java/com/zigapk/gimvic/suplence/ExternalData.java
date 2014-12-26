package com.zigapk.gimvic.suplence;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;

/**
 * Created by ziga on 26.12.2014.
 */
public class ExternalData {
    boolean admimn = false;
    int safetyCounter = 0;

    public static void backupToExternalStorage(Context context) {
        ExternalData old = getExternalBackup();
        if (old == null) {
            ExternalData data = new ExternalData();
            data.admimn = Settings.getAdmin(context);
            data.safetyCounter = Settings.getSafetyCounter(context);

            String file = new Gson().toJson(data);
            if (Files.isExternalStorageWritable()) {
                Files.writeToExternalStorage(".gimvic_backup.json", file);
            }
        } else {
            if (!getExternalBackup().admimn) {
                ExternalData data = new ExternalData();
                data.admimn = Settings.getAdmin(context);
                data.safetyCounter = Settings.getSafetyCounter(context);

                String file = new Gson().toJson(data);
                if (Files.isExternalStorageWritable()) {
                    Files.writeToExternalStorage(".gimvic_backup.json", file);
                }
            }
        }

    }

    private static ExternalData getExternalBackup() {
        String json = Files.readFileFromExternalStorage(".gimvic_backup.json");
        return new Gson().fromJson(json, ExternalData.class);
    }

    public static void restoreExternalBackup(Context context){
        ExternalData data = getExternalBackup();
        if(data != null){
            Settings.setAdmin(data.admimn, context);
            Settings.setSafetyCounter(data.safetyCounter, context);
        }
    }

    public static void syncExternalBackup(Context context){
        restoreExternalBackup(context);
        backupToExternalStorage(context);
    }
}
