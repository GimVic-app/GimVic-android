package com.zigapk.gimvic.suplence;

import android.content.Context;

/**
 * Created by ziga on 10/18/14.
 */
public class Settings {


    //TODO: make it read real data
    public static String getRazred(Context context){
        return "2B";
    }

    //TODO: make it return real data
    public static String getProfesor(Context context){
        return "Rudolf";
    }

    //TODO: make it work
    public static void resetSafetyCounter(){

    }

    public static int getMode(Context context){

        //TODO: make it return real mode
        return Mode.MODE_HYBRID;
    }

    public static int getUserMode(){
        //TODO: make it return real mode
        return UserMode.MODE_UCENEC;
    }
}

class Mode{
    public static int MODE_HYBRID = 0;
    public static int MODE_URNIK = 1;
    public static int MODE_SUPLENCE = 2;
}

class UserMode{
    public static int MODE_UCITELJ = 0;
    public static int MODE_UCENEC = 1;
}
