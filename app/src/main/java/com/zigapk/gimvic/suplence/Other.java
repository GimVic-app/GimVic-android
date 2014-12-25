package com.zigapk.gimvic.suplence;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static boolean areSame(String razred, String suplenceRazred) {
        suplenceRazred = suplenceRazred.replace(" ", "");
        suplenceRazred = suplenceRazred.replace(".", "");

        if (razred.toLowerCase().equals(suplenceRazred.toLowerCase())) {
            return true;
        } else return false;
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
}
