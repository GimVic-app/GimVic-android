package com.zigapk.gimvic.suplence.helper;

import android.content.Context;

import com.zigapk.gimvic.suplence.helper.methods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ziga on 29.3.2014.
 */
public class working_with_files {

    public static String getFileValue(String fileName, Context context) {
        try {
            StringBuffer outStringBuf = new StringBuffer();
            String inputLine = "";
            /*
             * We have to use the openFileInput()-method the ActivityContext
             * provides. Again for security reasons with openFileInput(...)
             */
            FileInputStream fIn = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader inBuff = new BufferedReader(isr);
            while ((inputLine = inBuff.readLine()) != null) {
                outStringBuf.append(inputLine);
                //outStringBuf.append("\n");
            }
            inBuff.close();
            return outStringBuf.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean writeToFile(String fileName, String value,
                                      Context context, int writeOrAppendMode) {
        // just make sure it's one of the modes we support
        if (writeOrAppendMode != Context.MODE_WORLD_READABLE
                && writeOrAppendMode != Context.MODE_WORLD_WRITEABLE
                && writeOrAppendMode != Context.MODE_APPEND) {
            return false;
        }
        try {
            /*
             * We have to use the openFileOutput()-method the ActivityContext
             * provides, to protect your file from others and This is done for
             * security-reasons. We chose MODE_WORLD_READABLE, because we have
             * nothing to hide in our file
             */
            FileOutputStream fOut = context.openFileOutput(fileName,
                    writeOrAppendMode);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            // Write the string to the file
            osw.write(value);
            // save and close
            osw.flush();
            osw.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void clearOldData(Context context){

        Calendar cal = Calendar.getInstance();


        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH);
        month ++;
        Integer day = cal.get(Calendar.DAY_OF_MONTH);

        String firstdate = year.toString() + "-" + month.toString() + "-" + day.toString();

        day++;

        if(!methods.isValidDate(month.toString() + "/" + day.toString() + "/" + year.toString())){
            if(month==12){
                month = 1;
                year++;

            }else{
                month ++;
            }
            day = 1;

        }


        String seconddate = year.toString() + "-" + month.toString() + "-" + day.toString();
        day++;
        if(!methods.isValidDate(month.toString() + "/" + day.toString() + "/" + year.toString())){
            if(month==12){
                month = 1;
                year++;

            }else{
                month ++;
            }
            day = 1;

        }
        String thirddate = year.toString() + "-" + month.toString() + "-" + day.toString();

        ArrayList<String> obdrži_datoteke = new ArrayList<String>();
        obdrži_datoteke.add(firstdate + ".xml");
        obdrži_datoteke.add(seconddate + ".xml");
        obdrži_datoteke.add(thirddate + ".xml");
        obdrži_datoteke.add("first.time");
        obdrži_datoteke.add("filtri.value");
        obdrži_datoteke.add("opened.offline");


        if (context.getFilesDir().isDirectory()) {
            String[] children = context.getFilesDir().list();
            for (int i = 0; i < children.length; i++) {
                String ime = new File(context.getFilesDir(), children[i]).getName();
                if(!seImeUjema(ime, obdrži_datoteke)){
                    new File(context.getFilesDir(), children[i]).delete();
                }

            }
        }

    }

    public static Boolean seImeUjema(String ime, ArrayList<String> imena){
        Boolean se_ujema = false;
        for(int i = 0; i < imena.size(); i++){
            if(ime.equals(imena.get(i))){
                se_ujema = true;
            }
        }
        return se_ujema;
    }
}
