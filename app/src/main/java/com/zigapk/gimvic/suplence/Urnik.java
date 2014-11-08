package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
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

    public static void render(Context context){

        Gson gson = new Gson();
        PersonalUrnik urnik = gson.fromJson(Files.getFileValue("Urnik-personal.json", context), PersonalUrnik.class);

        for(int dan = 1; dan <= 5; dan++){
            for(int ura = 1; ura <= 9; ura++){
                UrnikElement current = urnik.days[dan - 1].classes[ura - 1];



                TextView predmetTv = Main.textViews[dan - 1][ura - 1][0];
                TextView profesorTv = Main.textViews[dan - 1][ura - 1][1];
                TextView ucilnicaTv = Main.textViews[dan - 1][ura - 1][2];


                predmetTv.setText(current.predmet);
                if(Settings.getUserMode() == UserMode.MODE_UCITELJ){
                    profesorTv.setText(current.razred);
                }else{
                    profesorTv.setText(current.profesor);
                }
                ucilnicaTv.setText(current.ucilnica);


            }
        }

    }

    public static void parseUrnik(Context context){
        String rawData = Files.getFileValue("Urnik.js", context);

        String razrediData = rawData.substring(rawData.indexOf("razredi = new Array("), rawData.indexOf("ucitelji = new Array("));
        String uciteljiData = rawData.substring(rawData.indexOf("ucitelji = new Array("), rawData.indexOf("ucilnice = new Array("));


        Urnik urnik = new Urnik();
        urnik.urnik = pasreUrnikFromString(rawData);

        Gson gson = new Gson();
        Files.writeToFile("Urnik.json", gson.toJson(urnik), context);

        Razredi razredi = parseRazredi(razrediData);
        Files.writeToFile("Razredi.json", gson.toJson(razredi), context);

        Ucitelji ucitelji = parseUcitelji(uciteljiData);
        Files.writeToFile("Ucitelji.json", gson.toJson(ucitelji), context);

        parsePersonalUrnik(context);

    }

    private static String[][] pasreUrnikFromString(String rawData){
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
        return finalData;
    }
    private static Razredi parseRazredi(String razrediData){

        Razredi result = new Razredi();

        String temp = "razredi = new Array(";
        int start = razrediData.indexOf(temp) + temp.length();
        int stop = razrediData.indexOf(");", start);

        int number = Integer.parseInt(razrediData.substring(start, stop));

        result.razredi = new String[number];

        for(int i = 0; i < number; i++){
            String before = "razredi[" + i + "] = \"";
            String after = "\";";

            int zacetek = razrediData.indexOf(before) + before.length();
            int konec = razrediData.indexOf(after, zacetek);

            result.razredi[i] = razrediData.substring(zacetek, konec);
        }

        return result;
    }

    private static Ucitelji parseUcitelji(String uciteljiData){
        Ucitelji result = new Ucitelji();

        String temp = "ucitelji = new Array(";
        int start = uciteljiData.indexOf(temp) + temp.length();
        int stop = uciteljiData.indexOf(");", start);

        int number = Integer.parseInt(uciteljiData.substring(start, stop));

        result.ucitelji = new String[number];

        for(int i = 0; i < number; i++){
            String before = "razredi[" + i + "] = \"";
            String after = "\";";

            int zacetek = uciteljiData.indexOf(before) + before.length();
            int konec = uciteljiData.indexOf(after, zacetek);

            result.ucitelji[i] = uciteljiData.substring(zacetek, konec);
        }

        return result;
    }

    private static void parsePersonalUrnik(Context context){
        Gson gson = new Gson();
        Urnik urnik = gson.fromJson(Files.getFileValue("Urnik.json", context), Urnik.class);

        PersonalUrnik personal = new PersonalUrnik();


        int mode = Settings.getUserMode();

        if(mode == UserMode.MODE_UCENEC){

            String razred = Settings.getRazred(context);
            for(int i = 0; i < urnik.urnik.length; i++){
                if(urnik.urnik[i][1].equals(razred)){
                    int ura = Integer.parseInt(urnik.urnik[i][6]);
                    int dan = Integer.parseInt(urnik.urnik[i][5]);
                    String profesor = urnik.urnik[i][2];
                    String predmet = urnik.urnik[i][3];
                    String ucilnica = urnik.urnik[i][4];

                    personal.days[dan - 1].classes[ura - 1].razred = razred;
                    personal.days[dan - 1].classes[ura - 1].ura = ura;
                    personal.days[dan - 1].classes[ura - 1].dan = dan;
                    personal.days[dan - 1].classes[ura - 1].profesor = profesor;
                    personal.days[dan - 1].classes[ura - 1].predmet = predmet;
                    personal.days[dan - 1].classes[ura - 1].ucilnica = ucilnica;

                }
            }

        }else if(mode == UserMode.MODE_UCITELJ){

            String profesor = Settings.getProfesor(context);
            for(int i = 0; i < urnik.urnik.length; i++){
                if(urnik.urnik[i][2].equals(profesor)){
                    int ura = Integer.parseInt(urnik.urnik[i][6]);
                    int dan = Integer.parseInt(urnik.urnik[i][5]);
                    String razred = urnik.urnik[i][1];
                    String predmet = urnik.urnik[i][3];
                    String ucilnica = urnik.urnik[i][4];

                    personal.days[dan - 1].classes[ura - 1].razred = razred;
                    personal.days[dan - 1].classes[ura - 1].ura = ura;
                    personal.days[dan - 1].classes[ura - 1].dan = dan;
                    personal.days[dan - 1].classes[ura - 1].profesor = profesor;
                    personal.days[dan - 1].classes[ura - 1].predmet = predmet;
                    personal.days[dan - 1].classes[ura - 1].ucilnica = ucilnica;

                }
            }
        }

        String json = gson.toJson(personal);
        Files.writeToFile("Urnik-personal.json", json, context);

    }

    public static PersonalUrnik getPersonalUrnik(Context context){
        return new Gson().fromJson(Files.getFileValue("Urnik-personal.json", context), PersonalUrnik.class);
    }

}

class Razredi{
    String[] razredi;
}

class Ucitelji{
    String[] ucitelji;
}

class PersonalUrnik{
    Day[] days;

    public PersonalUrnik(){
        days = new Day[5];
        days[0] = new Day();
        days[1] = new Day();
        days[2] = new Day();
        days[3] = new Day();
        days[4] = new Day();
    }
}

class UrnikElement{
    String razred = "";
    String profesor = "";
    String predmet = "";
    String ucilnica = "";
    int ura = 1;
    int dan = 1; // 1 = monday, 5 = friday
    boolean suplenca = false;

}

class Day{
    UrnikElement[] classes;

    public Day(){
        classes = new UrnikElement[9];
        classes[0] = new UrnikElement();
        classes[1] = new UrnikElement();
        classes[2] = new UrnikElement();
        classes[3] = new UrnikElement();
        classes[4] = new UrnikElement();
        classes[5] = new UrnikElement();
        classes[6] = new UrnikElement();
        classes[7] = new UrnikElement();
        classes[8] = new UrnikElement();
    }
}