package com.zigapk.gimvic.suplence;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ziga on 12/5/14.
 */
public class Other {

    public static boolean layoutComponentsReady() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                if (Main.textViews[i][j][0] == null) return false;
                if (Main.textViews[i][j][1] == null) return false;
                if (Main.textViews[i][j][2] == null) return false;
                if (Main.textViews[i][j][3] == null) return false;
                if (Main.classItems[i][j] == null) return false;
            }
        }
        return true;
    }

    public static boolean areSame(ChosenRazredi razredi, String suplenceRazred) {
        suplenceRazred = suplenceRazred.replace(" ", "");
        suplenceRazred = suplenceRazred.replace(".", "");

        for(String razred : razredi.razredi){
            if (razred.toLowerCase().equals(suplenceRazred.toLowerCase()) || suplenceRazred.toLowerCase().contains(razred.toLowerCase())) return true;
        }

        return false;
    }

    public static boolean areProfesorsSame(String urnikString, String suplenceString) {
        String[] urnik = urnikString.split(" ");
        String[] suplence = suplenceString.split(" ");

        urnik = allToLowerCase(urnik);
        suplence = allToLowerCase(suplence);

        if(suplenceString.toLowerCase().contains("baga")){
            System.out.print("");
        }

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
        return suplence.contains(urnik);
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

    public static Date plus1Day(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
