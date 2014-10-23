package com.zigapk.gimvic.suplence;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by ziga on 10/21/14.
 */
public class Urnik {

    public String[][] urnik;

    public static void downloadUrnik(Context context){

        //TODO: make iti use propper API
        String rawData = Internet.getTextFromUrl("https://dl.dropboxusercontent.com/u/16258361/urnik/data.js");

        if(rawData.contains("podatki = new Array(")){
            Files.writeToFile("Urnik.js", rawData, context);
        }

    }

    public static void parseUrnik(Context context){
        String rawData = Files.getFileValue("Urnik.js", context);

        String razrediData = rawData.substring(rawData.indexOf("razredi = new Array("), rawData.indexOf("\nucitelji = new Array("));
        String uciteljiData = rawData.substring(rawData.indexOf("ucitelji = new Array("), rawData.indexOf("\nucilnice = new Array("));

        String array[] = rawData.split("podatki");
        ArrayList<String> dataArray = new ArrayList<String>();
        for (int i = 2; i < array.length; i++)
            dataArray.add(array[i]);

        String last = dataArray.get(dataArray.size()-1);
        String lastCleared = "";
        for (int i = 0; i < last.length(); i++) {
            if (!last.substring(i, i+1).contains("r")) lastCleared += last.substring(i, i+1);
            else break;
        }
        dataArray.set(dataArray.size() - 1, lastCleared);

        for (int i = 0; i < dataArray.size(); i++) {
            String string = dataArray.get(i);
            if (string.contains("new Array")) {
                dataArray.remove(i);
                i--;
            }
            else {
                string = string.replaceAll("]\\[", ";");
                string = string.replaceAll("\\[", "");
                string = string.replaceAll("]", "");
                string = string.replaceAll("\"", "");
                string = string.replace(" = ", ";");
                dataArray.set(i, string);
            }
        }

        int lastIndex1;
        String string = "";
        String lastString = dataArray.get(dataArray.size() - 1);
        for (int i = 0; i < lastString.length(); i++) {
            if (lastString.substring(i, i+1).contains(";"))
                break;
            string += lastString.substring(i, i+1);
        }

        lastIndex1 = Integer.parseInt(string);
        boolean start = false;
        for (int i = 0; i < lastString.length(); i++) {
            if (start) {
                string = lastString.substring(i, i + 1);
                break;
            }
            if (lastString.substring(i, i+1).contains(";"))
                start = true;
        }
        int lastIndex2 = Integer.parseInt(string);

        String finalData[][] = new String[lastIndex1+1][lastIndex2+1];
        for (int i = 0; i < dataArray.size(); i++) {
            String components[] = dataArray.get(i).split(";");
            if (components.length == 3)
                finalData[Integer.parseInt(components[0])][Integer.parseInt(components[1])] = components[2];
            else
                finalData[Integer.parseInt(components[0])][Integer.parseInt(components[1])] = "";
        }



        Urnik urnik = new Urnik();
        urnik.urnik = finalData;

        Gson gson = new Gson();
        Files.writeToFile("Urnik.json", gson.toJson(urnik), context);

        Razredi razredi = parseRazredi(razrediData);
        Files.writeToFile("Razredi.json", gson.toJson(razredi), context);

        Ucitelji ucitelji = parseUcitelji(uciteljiData);
        Files.writeToFile("Ucitelji.json", gson.toJson(ucitelji), context);


    }

    private static Razredi parseRazredi(String razrediData){
        String temp = "razredi[";
        int start = razrediData.lastIndexOf(temp) + temp.length();
        int stop = razrediData.indexOf("] = \"", start);

        int number = Integer.parseInt(razrediData.substring(start, stop));

        //TODO: finish
    }

    private static Ucitelji parseUcitelji(String uciteljiData){

    }

}

class Razredi{
    String[] razredi;
}

class Ucitelji{
    String[] ucitelji;
}