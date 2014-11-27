package com.zigapk.gimvic.suplence;

import android.content.Context;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ziga on 10/18/14.
 * <p/>
 * <p/>
 * Data object(s) to be converted form json using gson.
 * <p/>
 * USAGE:
 * Gson gson = new Gson();
 * new Suplence suplence = gson.fromJson(jsonString, Suplence.class);
 */


public class Suplence {
    private static int suplenceCounter = 0;
    String datum;
    Nadomescanje[] nadomescanja;
    MenjavaPredmeta[] menjava_predmeta;
    MenjavaUre[] menjava_ur;
    MenjavaUcilnice[] menjava_ucilnic;


    //for tempDates
    public static final Date tempDate0 = new Date();
    public static final Date tempDate1 = plus1Day(tempDate0);
    public static final Date tempDate2 = plus1Day(tempDate1);
    public static final Date tempDate3 = plus1Day(tempDate2);
    public static final Date tempDate4 = plus1Day(tempDate3);
    public static final Date tempDate5 = plus1Day(tempDate4);
    public static final Date tempDate6 = plus1Day(tempDate5);


    public static void downloadSuplence(Context context) {
        Date date = new Date();
        final Context ctx = context;
        Settings.setSuplenceDownloaded(false, context);

        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate0, ctx);
            }
        }.start();
        date = plus1Day(date);

        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate1, ctx);
            }
        }.start();
        date = plus1Day(date);

        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate2, ctx);
            }
        }.start();
        date = plus1Day(date);

        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate3, ctx);
            }
        }.start();
        date = plus1Day(date);

        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate4, ctx);
            }
        }.start();
        date = plus1Day(date);

        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate5, ctx);
            }
        }.start();
        date = plus1Day(date);

        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate6, ctx);
            }
        }.start();


        //start clean old files
        new Thread() {
            @Override
            public void run() {
                cleanOldFiles(ctx);
            }
        }.start();


        //wait for suplence to download
        while (!Settings.areSuplenceDownloaded(context)) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static void downloadForDate(Date date, Context context) {
        String url = "http://app.gimvic.org/APIv2/suplence_provider.php?datum=" + getStringForDate(date);

        String suplence = Internet.getTextFromUrl(url);
        Files.writeToFile(getFileNameForDate(date), suplence, context);

        if (suplenceCounter == 6) {
            Settings.setSuplenceDownloaded(true, context);
        } else suplenceCounter++;
    }

    private static String getStringForDate(Date date) {
        return date.getDay() + "-" + date.getMonth() + "-" + date.getYear();
    }

    private static String getFileNameForDate(Date date) {
        return "suplence_" + getStringForDate(date) + ".json";
    }

    private static Date plus1Day(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    public static void cleanOldFiles(Context context) {

        //TODO: finish
    }

    public static void render(Context context) {

        //TODO: finish - not gonna work like that
    }

    public static PersonalUrnik getHybridUrnik(PersonalUrnik urnik, Context context) {
        return addSuplence(urnik, context);
    }

    private static PersonalUrnik addSuplence(PersonalUrnik urnik, Context context) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        int day = calendar.DAY_OF_WEEK - 1;
        int userMode = Settings.getUserMode(context);

        if (day > 5) day = 1;

        for (int i = day; i <= day + 5; day++) {

            if (day > 5) day = 1;

            Suplence suplence = getSuplenceForDate(date, context);
            urnik = addNadomescanja(urnik, suplence, day, userMode, context);
            urnik = addMenjavePredmeta(urnik, suplence, day, userMode, context);
            urnik = addMenjaveUr(urnik, suplence, day, userMode, context);
            urnik = addMenjaveUcilnic(urnik, suplence, day, userMode, context);

            date = plus1Day(date);

        }

        return urnik;
    }

    private static PersonalUrnik addNadomescanja(PersonalUrnik urnik, Suplence suplence, int day, int userMode, Context context) {
        return urnik;
    }

    private static PersonalUrnik addMenjavePredmeta(PersonalUrnik urnik, Suplence suplence, int day, int userMode, Context context) {
        if (userMode == UserMode.MODE_UCENEC) {
            String razred = Settings.getRazred(context);

            for (MenjavaPredmeta menjava : suplence.menjava_predmeta) {
                if (areSame(razred, menjava.class_name)) {
                    int ura = Integer.parseInt(menjava.ura.substring(0, 1));
                    urnik.days[day - 1].classes[ura - 1].suplenca = true;
                    urnik.days[day - 1].classes[ura - 1].predmet = menjava.predmet;
                    urnik.days[day - 1].classes[ura - 1].profesor = menjava.ucitelj;
                    urnik.days[day - 1].classes[ura - 1].ucilnica = menjava.ucilnica;
                }
            }


        } else {
            String profesor = Settings.getProfesor(context);


            for (MenjavaPredmeta menjava : suplence.menjava_predmeta) {
                if (areProfesorsSame(profesor, menjava.class_name)) {
                    int ura = Integer.parseInt(menjava.ura.substring(0, 1));
                    urnik.days[day - 1].classes[ura - 1].suplenca = true;
                    urnik.days[day - 1].classes[ura - 1].predmet = menjava.predmet;
                    urnik.days[day - 1].classes[ura - 1].profesor = menjava.ucitelj;
                    urnik.days[day - 1].classes[ura - 1].ucilnica = menjava.ucilnica;
                }
            }
        }

        return urnik;
    }

    private static PersonalUrnik addMenjaveUr(PersonalUrnik urnik, Suplence suplence, int day, int userMode, Context context) {
        return urnik;
    }

    private static PersonalUrnik addMenjaveUcilnic(PersonalUrnik urnik, Suplence suplence, int day, int userMode, Context context) {
        return urnik;
    }

    private static Suplence getSuplenceForDate(Date date, Context context) {
        String json = Files.getFileValue(getFileNameForDate(date), context);
        Gson gson = new Gson();
        return gson.fromJson(json, Suplence.class);
    }

    private static boolean areSame(String razred, String suplenceRazred) {
        if (razred.toLowerCase().equals(suplenceRazred.toLowerCase())) {
            return true;
        } else if (suplenceRazred.contains(razred.substring(0, 1).toLowerCase()) && suplenceRazred.contains(razred.substring(1, 2).toLowerCase())) {
            return true;
        } else return false;
    }

    public static boolean areProfesorsSame(String profesor, String suplenceProfesor){
        profesor = profesor.toLowerCase();
        suplenceProfesor = suplenceProfesor.toLowerCase();

        profesor = profesor.replaceAll(" ", "");
        suplenceProfesor = suplenceProfesor.replaceAll(" ", "");

        if(suplenceProfesor.contains(profesor)) return true;

        return false;

    }


}

class Nadomescanje {
    String odsoten_fullname;
    int stevilo_ur_nadomescanj;
    NadomescanjaUra[] nadomescanja_ure;
}

class NadomescanjaUra {
    String ura;
    String class_name;
    String ucilnica;
    String nadomesca_full_name;
    int sproscen;
    String sproscen_class_name;
    String predmet;
    String opomba;

}


class MenjavaPredmeta {
    String menjava_predmeta;
    String ura;
    String class_name;
    String ucilnica;
    String ucitelj;
    String original_predmet;
    String predmet;
    String opomba;

}

class MenjavaUre {
    String class_name;
    String ura;
    String zamenjava_uciteljev;
    String predmet;
    String ucilnica;
    String opomba;

}

class MenjavaUcilnice {
    String class_name;
    String ura;
    String ucitelj;
    String predmet;
    String ucilnica_from;
    String ucilnica_to;
    String opomba;
}

