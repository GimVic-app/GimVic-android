package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ziga on 29.3.2014.
 */
public class methods {

    public static boolean isValidDate(String input) {
        String formatString = "MM/dd/yyyy";

        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString);
            format.setLenient(false);
            format.parse(input);
        } catch (ParseException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
    public static String getMacAddress(Context context) {
        WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "ne.dela";
        }
        return macAddress;
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static ArrayList<String> getFiltri(String string_filtri){
        ArrayList<String> lista = new ArrayList<String>();
        lista.clear();
        if(string_filtri==null){
            lista.add("empty");
        }else if(numberOf(string_filtri, ",")==0){
            lista.add(string_filtri);
        }else{
            int vejice = numberOf(string_filtri, ", ");
            for(int i=0; i<vejice+1; i++){
                if(string_filtri.contains(", ")){
                    lista.add(string_filtri.substring(0, string_filtri.indexOf(",")));
                    string_filtri = string_filtri.replaceAll(string_filtri.substring(0, string_filtri.indexOf(", ")+1), "");
                }else{
                    lista.add(string_filtri);
                }

            }

            for(int j = 0; j < lista.size(); j++){
                if(j!=0){
                    lista.set(j, lista.get(j).replaceFirst(" ", ""));
                }
            }
        }
        return lista;
    }

    public static int numberOf(String base,String searchFor){
        //funkcija prešteje kolikokrat se nek string ponovi v drugem stringu

        int len = searchFor.length();
        int result = 0;
        if (len > 0) {
            int start = base.indexOf(searchFor);
            while (start != -1) {
                result++;
                start = base.indexOf(searchFor, start+len);
            }
        }
        return result;
    }

    public static String getTagForDay(String tags_xml, String day){
        String tag = "";
        try {
            tags_xml = tags_xml.substring(tags_xml.indexOf("<tags>"), tags_xml.length());
            if(tags_xml.contains(day)){
                int date = tags_xml.indexOf(day);
                int start = tags_xml.indexOf("<tag>", date) + "<tag>".length();
                int stop = tags_xml.indexOf("</tag>", date);
                tag = tags_xml.substring(start, stop);

            }else {
                return tag;
            }

        }catch (Exception e){}

        return tag;
    }

    public static String getTextForDay(String tags_xml, String day){
        String tag = "";
        tags_xml = tags_xml.substring(tags_xml.indexOf("<tags>"), tags_xml.length());
        try {
            if(tags_xml.contains(day)){
                int date = tags_xml.indexOf(day);
                int start = tags_xml.indexOf("<text>", date) + "<text>".length();
                int stop = tags_xml.indexOf("</text>", date);
                tag = tags_xml.substring(start, stop);

            }else {
                return tag;
            }

        }catch (Exception e){}

        return tag;
    }


    public static String randomTextForNoData(){
        ArrayList<String> texts = new ArrayList<String>();

        texts.add("Nič posebnega danes");
        texts.add("Vse po urniku");
        texts.add("Nič novega");
        texts.add("Ni sprememb");
        texts.add("Ni nadomeščanj");

        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(texts.size());

        return texts.get(randomInt);
    }

    public static Boolean allEmpty(ArrayList<TextView> tvji, int page){
        Boolean bool = true;
        int i;
        int x;
        if(page==1){
            i = 0;
            x = 4;
        }else if(page==2){
            i = 4;
            x = 8;
        }else{
            i = 8;
            x = 12;

        }
        for (int j = i;j<x;j++){
            try {
                if(tvji.get(j).getText()!="Ni podatkov"){
                    bool = false;
                }
            }catch (Exception e){}
        }
        return bool;
    }
}
