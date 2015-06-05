package com.zigapk.gimvic.suplence;

import android.content.Context;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.io.File;

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
    Nadomescanje[] nadomescanja;
    MenjavaPredmeta[] menjava_predmeta;
    MenjavaUre[] menjava_ur;
    MenjavaUcilnice[] menjava_ucilnic;

    //for tempDates
    public static final Date tempDate0 = new Date();
    public static final Date tempDate1 = Other.plus1Day(tempDate0);
    public static final Date tempDate2 = Other.plus1Day(tempDate1);
    public static final Date tempDate3 = Other.plus1Day(tempDate2);
    public static final Date tempDate4 = Other.plus1Day(tempDate3);
    public static final Date tempDate5 = Other.plus1Day(tempDate4);
    public static final Date tempDate6 = Other.plus1Day(tempDate5);


    public static void downloadSuplence(Context context) {
        final Context ctx = context;
        Settings.setSuplenceDownloaded(false, context);

        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate0, ctx);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate1, ctx);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate2, ctx);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate3, ctx);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate4, ctx);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                downloadForDate(tempDate5, ctx);
            }
        }.start();
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
            suplenceCounter = 0;
        } else suplenceCounter++;
    }

    private static String getStringForDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        return year + "-" + month + "-" + day;
    }

    private static String getFileNameForDate(Date date) {
        return "suplence_" + getStringForDate(date) + ".json";
    }

    private static String getDateFromStringFileName(String name){
        name = name.replaceAll("suplence_", "");
        name = name.replaceAll(".json", "");
        return name;
    }

    private static Date getDateflFromFileName(String name){
        String dateString = getDateFromStringFileName(name);
        int minus = dateString.indexOf("-");
        int minus1 = dateString.lastIndexOf("-");
        int day = Integer.parseInt(dateString.substring(0, minus));
        int month = Integer.parseInt(dateString.substring(minus + 1, minus1));
        int year = Integer.parseInt(dateString.substring(minus1 + 1, dateString.length()));

        Date date = new Date();
        date.setDate(day);
        date.setMonth(month);
        date.setYear(year);
        return date;
    }

    public static void cleanOldFiles(Context context) {

        File dir = context.getFilesDir();
        String[] files = dir.list();
        for(String name : files){
            if(name.contains("suplence_")){
                Date today = new Date();
                Date date = getDateflFromFileName(name);

                if(dir.exists()){
                    if(date.before(today)){
                        Files.deleteDir(new File(dir, name));
                    }
                }
            }

        }
    }

    public static void renderHybrid(Context context) {
        while (!Other.layoutComponentsReady() || !Settings.isHybridParsed(context)){}
        String json = Files.getFileValue("hybrid.json", context);
        if(json != null){
            Gson gson = new Gson();
            PersonalUrnik hybrid = gson.fromJson(json, PersonalUrnik.class);
            Urnik.renderPersonalUrnik(hybrid, context);
        }

    }

    public static void render(Context context) {
        while (!Other.layoutComponentsReady() || !Settings.areSuplenceParsed(context)){}
        String json = Files.getFileValue("suplence.json", context);
        if(json != null){
            Gson gson = new Gson();
            PersonalUrnik suplence = gson.fromJson(json, PersonalUrnik.class);
            Urnik.renderPersonalUrnik(suplence, context);
        }

    }

    public static void parse(final Context context){

        while (!Settings.isTrueUrnikParsed(context) || !Settings.isUrnikParsed(context)){}

        PersonalUrnik hybrid = parseHybridUrnik(Urnik.getPersonalUrnik(context), context);
        Gson gson = new Gson();
        String json = gson.toJson(hybrid);
        Files.writeToFile("hybrid.json", json, context);
        Settings.setHybridParsed(true, context);

        PersonalUrnik suplence = new PersonalUrnik();
        suplence = addSuplence(suplence, context);
        json = gson.toJson(suplence);
        Files.writeToFile("suplence.json", json, context);
        Settings.setSuplenceParsed(true, context);

        Data.renderData(context);
    }

    private static PersonalUrnik parseHybridUrnik(PersonalUrnik urnik, Context context) {
        return addSuplence(urnik, context);
    }

    private static PersonalUrnik addSuplence(PersonalUrnik urnik, Context context) {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day = day - 1;
        if(day==0)day = 7;

        Date date = new Date();
        int userMode = Settings.getUserMode(context);

        for(int i = 0; i < 7; i++){
            Suplence suplence = getSuplenceForDate(date, context);


            if(day <= 5 && suplence != null){
                urnik = addNadomescanja(urnik, suplence, day, userMode, context);
                urnik = addMenjavePredmeta(urnik, suplence, day, userMode, context);
                urnik = addMenjaveUr(urnik, suplence, day, userMode, context);
                urnik = addMenjaveUcilnic(urnik, suplence, day, userMode, context);
            }

            date = Other.plus1Day(date);
            day = day + 1;
            if(day > 7) day = day % 7;
        }

        return urnik;
    }

    private static PersonalUrnik addNadomescanja(PersonalUrnik urnik, Suplence suplence, int day, int userMode, Context context) {
        if (userMode == UserMode.MODE_UCENEC) {
            ChosenRazredi razredi = Settings.getRazredi(context);


            for (Nadomescanje nadomescanje : suplence.nadomescanja) {
                for(NadomescanjaUra nadomescanjeUra : nadomescanje.nadomescanja_ure){
                    if (Other.areSame(razredi, nadomescanjeUra.class_name)) {
                        int ura = Integer.parseInt(nadomescanjeUra.ura.substring(0, 1));
                        //TODO: If ura=0 then this is predura and we do not need to display it (yet)
                        if(ura != 0){
                            urnik.days[day - 1].classes[ura - 1].suplenca = true;
                            urnik.days[day - 1].classes[ura - 1].empty = false;
                            urnik.days[day - 1].classes[ura - 1].predmet = filterIfNeeded(nadomescanjeUra.predmet);
                            urnik.days[day - 1].classes[ura - 1].profesor = filterIfNeeded(nadomescanjeUra.nadomesca_full_name);
                            urnik.days[day - 1].classes[ura - 1].ucilnica = filterIfNeeded(nadomescanjeUra.ucilnica);
                            if(nadomescanjeUra.opomba != null && nadomescanjeUra.opomba != "") urnik.days[day - 1].classes[ura - 1].opomba = nadomescanjeUra.opomba;
                        }
                    }
                }

            }
        } else {
            String profesor = Settings.getProfesor(context);

            for (Nadomescanje nadomescanje : suplence.nadomescanja) {
                for(NadomescanjaUra nadomescanjeUra : nadomescanje.nadomescanja_ure){
                    if (Other.areProfesorsSame(profesor, nadomescanje.odsoten_fullname) || Other.areProfesorsSame(profesor, nadomescanjeUra.nadomesca_full_name)) {
                        int ura = Integer.parseInt(nadomescanjeUra.ura.substring(0, 1));
                        if(ura == 6){
                            System.out.print("");
                        }else if(ura != 0) {//TODO: If ura=0 then this is predura and we do not need to display it (yet)
                            urnik.days[day - 1].classes[ura - 1].suplenca = true;
                            urnik.days[day - 1].classes[ura - 1].empty = false;
                            urnik.days[day - 1].classes[ura - 1].predmet = filterIfNeeded(nadomescanjeUra.predmet);
                            urnik.days[day - 1].classes[ura - 1].profesor = filterIfNeeded(nadomescanjeUra.nadomesca_full_name);
                            urnik.days[day - 1].classes[ura - 1].ucilnica = filterIfNeeded(nadomescanjeUra.ucilnica);
                            urnik.days[day - 1].classes[ura - 1].razred = nadomescanjeUra.class_name;
                            if (nadomescanjeUra.opomba != null && nadomescanjeUra.opomba != "")
                                urnik.days[day - 1].classes[ura - 1].opomba = nadomescanjeUra.opomba;

                            if (Other.areProfesorsSame(profesor, nadomescanje.odsoten_fullname))
                                urnik.days[day - 1].classes[ura - 1].mankajociUcitelj = true;
                        }
                    }
                }

            }
        }

        return urnik;
    }

    private static PersonalUrnik addMenjavePredmeta(PersonalUrnik urnik, Suplence suplence, int day, int userMode, Context context) {
        if (userMode == UserMode.MODE_UCENEC) {
            ChosenRazredi razredi = Settings.getRazredi(context);


            for (MenjavaPredmeta menjava : suplence.menjava_predmeta) {

                if (Other.areSame(razredi, menjava.class_name)) {
                    int ura = Integer.parseInt(menjava.ura.substring(0, 1));
                    urnik.days[day - 1].classes[ura - 1].suplenca = true;
                    urnik.days[day - 1].classes[ura - 1].predmet = filterIfNeeded(menjava.predmet);
                    urnik.days[day - 1].classes[ura - 1].profesor = filterIfNeeded(menjava.ucitelj);
                    urnik.days[day - 1].classes[ura - 1].ucilnica = filterIfNeeded(menjava.ucilnica);
                    if(menjava.opomba != null && menjava.opomba != "") urnik.days[day - 1].classes[ura - 1].opomba = menjava.opomba;
                }
            }


        } else {
            String profesor = Settings.getProfesor(context);


            for (MenjavaPredmeta menjava : suplence.menjava_predmeta) {
                if (Other.areProfesorsSame(profesor, menjava.class_name)) {
                    int ura = Integer.parseInt(menjava.ura.substring(0, 1));
                    urnik.days[day - 1].classes[ura - 1].suplenca = true;
                    urnik.days[day - 1].classes[ura - 1].predmet = filterIfNeeded(menjava.predmet);
                    urnik.days[day - 1].classes[ura - 1].profesor = filterIfNeeded(menjava.ucitelj);
                    urnik.days[day - 1].classes[ura - 1].ucilnica = filterIfNeeded(menjava.ucilnica);
                    if(menjava.opomba != null && menjava.opomba != "") urnik.days[day - 1].classes[ura - 1].opomba = menjava.opomba;
                }
            }
        }

        return urnik;
    }

    private static PersonalUrnik addMenjaveUr(PersonalUrnik urnik, Suplence suplence, int day, int userMode, Context context) {

        if (userMode == UserMode.MODE_UCENEC) {
            ChosenRazredi razredi = Settings.getRazredi(context);

            for (MenjavaUre menjava : suplence.menjava_ur) {

                if (Other.areSame(razredi, menjava.class_name)) {
                    int ura = Integer.parseInt(menjava.ura.substring(0, 1));
                    urnik.days[day - 1].classes[ura - 1].suplenca = true;
                    urnik.days[day - 1].classes[ura - 1].predmet = filterIfNeeded(menjava.predmet);
                    urnik.days[day - 1].classes[ura - 1].profesor = filterIfNeeded(menjava.zamenjava_uciteljev);
                    urnik.days[day - 1].classes[ura - 1].ucilnica = filterIfNeeded(menjava.ucilnica);
                    if(menjava.opomba != null && menjava.opomba != "") urnik.days[day - 1].classes[ura - 1].opomba = menjava.opomba;
                }
            }


        } else {
            String profesor = Settings.getProfesor(context);


            for (MenjavaPredmeta menjava : suplence.menjava_predmeta) {
                if (Other.areProfesorsSame(profesor, menjava.class_name)) {
                    int ura = Integer.parseInt(menjava.ura.substring(0, 1));
                    urnik.days[day - 1].classes[ura - 1].suplenca = true;
                    urnik.days[day - 1].classes[ura - 1].predmet = filterIfNeeded(menjava.predmet);
                    urnik.days[day - 1].classes[ura - 1].profesor = filterIfNeeded(menjava.ucitelj);
                    urnik.days[day - 1].classes[ura - 1].ucilnica = filterIfNeeded(menjava.ucilnica);
                    if(menjava.opomba != null && menjava.opomba != "") urnik.days[day - 1].classes[ura - 1].opomba = menjava.opomba;
                }
            }
        }
        return urnik;
    }

    private static PersonalUrnik addMenjaveUcilnic(PersonalUrnik urnik, Suplence suplence, int day, int userMode, Context context) {

        ChosenRazredi razredi = Settings.getRazredi(context);

        for (MenjavaUcilnice menjava : suplence.menjava_ucilnic){
            int ura = Integer.parseInt(menjava.ura.substring(0, 1));
            if(Other.areSame(razredi, menjava.class_name)){
                    urnik.days[day - 1].classes[ura - 1].suplenca = true;
                    urnik.days[day - 1].classes[ura - 1].ucilnica = filterIfNeeded(menjava.ucilnica_to);
                    if(menjava.opomba != null && menjava.opomba != "") urnik.days[day - 1].classes[ura - 1].opomba = menjava.opomba;
                }
            }
            return urnik;
        }

    private static Suplence getSuplenceForDate(Date date, Context context) {
        String name = getFileNameForDate(date);
        String json = Files.getFileValue(name, context);
        Gson gson = new Gson();
        return gson.fromJson(json, Suplence.class);
    }

    private static String filterIfNeeded(String original){
        if(original.contains(" -> ")) return original.replaceFirst(original.substring(0, original.indexOf(" -> ") + 4), "");
        else return original;
    }

    private static String[] devideIfNeeded(String original){
        String[] result;
        if(original.contains("->")){
            result = new String[2];
            result[0] = original.substring(0, original.indexOf(" ->"));
            result[1] = original.substring(original.indexOf("-> ") + 3, original.length());
        }else {
            result = new String[1];
            result[0] = original;
        };
        return result;
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
