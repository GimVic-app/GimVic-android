package com.zigapk.gimvic.suplence;

import android.content.Context;

import com.google.gson.Gson;
import com.zigapk.gimvic.suplence.exceptions.CouldNotReachServerException;

/**
 * Created by zigapk on 8.12.2015.
 */
public class Data {
    public Day[] days;
    public String validUntil;

    public Data download(Context context) throws CouldNotReachServerException{
        try {
            return new Gson().fromJson(Internet.getTextFromUrl(buildUrl(context)), Data.class);
        }catch (Exception e){
            throw new CouldNotReachServerException();
        }
    }

    private String buildUrl(Context context){
        return "http://192.168.1.107:8080/data?addSubstitutions=true&classes[]=3B&classes[]=3GEO1&snackType=navadna&lunchType=navadno";
    }
}

class Day{
    public Lesson[] lessons;
    public String[] snackLines;
    public String[] lunchLines;
}

class Lesson {
    public String[] subjects;
    public String[] teachers;
    public String[] classrooms;
    public String[] classes;
    public boolean substitution;
}