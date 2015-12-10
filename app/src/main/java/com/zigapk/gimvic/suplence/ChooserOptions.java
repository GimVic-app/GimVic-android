package com.zigapk.gimvic.suplence;

import com.google.gson.Gson;
import com.zigapk.gimvic.suplence.exceptions.CouldNotReachServerException;

/**
 * Created by zigapk on 10.12.2015.
 */
public class ChooserOptions {
    public String[] mainClasses;
    public String[] additionalClasses;
    public String[] teachers;
    public String[] snackTypes;
    public String[] lunchTypes;

    public ChooserOptions download() throws CouldNotReachServerException {
        try {
            String json = Internet.getTextFromUrl(Configuration.server + "/chooserOptions" +
                    "");
            ChooserOptions result = new Gson().fromJson(json, ChooserOptions.class);
            if (result.mainClasses == null || result.additionalClasses == null
                    || result.teachers == null || result.snackTypes == null ||
                    result.lunchTypes == null) throw new CouldNotReachServerException();
            return result;
        } catch (Exception e) {
            throw new CouldNotReachServerException();
        }
    }

    public String[] parsedSnackTypes() {
        String[] result = new String[snackTypes.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = snackTypes[i].replace("_", " ").toUpperCase();
        }
        return result;
    }

    public String[] parsedLunchTypes() {
        String[] result = new String[lunchTypes.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = lunchTypes[i].replace("_", " ").toUpperCase();
        }
        return result;
    }

}
