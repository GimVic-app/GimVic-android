package com.zigapk.gimvic.suplence;

import java.util.Date;

/**
 * Created by ziga on 10/18/14.
 *
 *
 * Data object(s) to be converted form json using gson.
 *
 * USAGE:
 * Gson gson = new Gson();
 * new Suplence suplence = gson.fromJson(jsonString, Suplence.class);
 */


public class Suplence {
    String datum;
    Nadomescanje[] nadomescanja;
    MenjavaPredmeta[] menjava_predmeta;
    MenjavaUre[] menjava_ur;
    MenjavaUcilnice[] menjava_ucilnic;




    public static void downloadSuplence(){

    }

    private static void downloadForDate(Date date){
        //TODO: make it do something
    }

    public static void cleanOldFiles(){

        //TODO: finish
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

