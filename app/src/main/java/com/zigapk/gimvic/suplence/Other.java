package com.zigapk.gimvic.suplence;

import android.app.ActivityManager;
import android.content.Context;

import java.util.Calendar;

/**
 * Created by ziga on 12/5/14.
 */
public class Other {

    public static boolean layoutComponentsReady() {
        boolean temp = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                if (Main.textViews[i][j][2] == null) temp = false;
            }
        }

        return temp;
    }

    public static boolean areSame(ChosenRazredi razredi, String suplenceRazred) {
        suplenceRazred = suplenceRazred.replace(" ", "");
        suplenceRazred = suplenceRazred.replace(".", "");

        for(String razred : razredi.razredi){
            if (razred.toLowerCase().equals(suplenceRazred.toLowerCase())) return true;
        }

        return false;
    }

    public static boolean areProfesorsSame(String urnikString, String suplenceString) {
        String[] urnik = urnikString.split(" ");
        String[] suplence = suplenceString.split(" ");

        urnik = allToLowerCase(urnik);
        suplence = allToLowerCase(suplence);


        if (suplence.length == 2) return compare2(urnik, suplence);
        else return compare3(urnikString, suplenceString);
    }

    private static boolean compare2(String[] urnik, String[] suplence) {
        boolean result = false;

        //is any same
        for (String temp : suplence) {
            if (temp.equals(urnik[0])) result = true;
        }

        String temp;

        //if the first one is surname
        String substring = suplence[1].substring(0, 1);
        temp = substring + suplence[0];
        if (temp.equals(urnik[0])) result = true;

        //and vice versa
        temp = suplence[0] + substring;
        if (temp.equals(urnik[0])) result = true;

        //if the second one is surname
        substring = suplence[0].substring(0, 1);
        temp = suplence[1] + substring;
        if (temp.equals(urnik[0])) result = true;

        //and vice versa
        temp = substring + suplence[1];
        if (temp.equals(urnik[0])) result = true;


        //and for Bajec and other special cases using spaces
        //if the first one is surname
        substring = suplence[1].substring(0, 1);
        temp = substring + " " + suplence[0];
        if (temp.equals(urnik[0])) result = true;

        //and vice versa
        temp = suplence[0] + " " + substring;
        if (temp.equals(urnik[0])) result = true;

        //if the second one is surname
        substring = suplence[0].substring(0, 1);
        temp = suplence[1] + " " + substring;
        if (temp.equals(urnik[0])) result = true;

        //and vice versa
        temp = substring + " " + suplence[1];
        if (temp.equals(urnik[0])) result = true;

        return result;

    }

    private static String[] allToLowerCase(String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].toLowerCase();
        }
        return array;
    }

    private static boolean compare3(String urnik, String suplence) {
        if (suplence.contains(urnik)) return true;
        else return false;
    }

    public static boolean razredEqualsAny(String razred, ChosenRazredi razredi){
        for(String temp : razredi.razredi){
            if(temp.equals(razred)) return true;
        }
        return false;
    }

    public static boolean holidays(){
        Calendar calendar = Calendar.getInstance();
        if(6<=calendar.get(Calendar.MONTH) + 1 && calendar.get(Calendar.MONTH)<=8) {
            if(calendar.get(Calendar.MONTH) + 1 == 6){
                if(calendar.get(Calendar.DAY_OF_MONTH) >= 25) return true;
            }else if(calendar.get(Calendar.MONTH) + 1 == 8){
                if(calendar.get(Calendar.DAY_OF_MONTH) <= 20) return true;
            }else if(calendar.get(Calendar.MONTH) + 1 == 7) return true;
        }
        return false;
    }

    public static long memoryAvailable(Context context){
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return mi.availMem / 1048576L;
    }
}
