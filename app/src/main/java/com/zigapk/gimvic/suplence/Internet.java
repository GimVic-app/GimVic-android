package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by ziga on 10/18/14.
 */
public class Internet {

    public static String getTextFromUrl(String url){
        try {
            URLConnection feedUrl = new URL(url).openConnection();

            try {
                InputStream in = feedUrl.getInputStream();
                String result = convertStreamToString(in);

                return result;
            }catch(Exception e){
                return "";
            }

        } catch (MalformedURLException e) {
            Log.v("ERROR","MALFORMED URL EXCEPTION");
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }


    private static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public static boolean isOnline(Context context) {
        try
        {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static boolean onWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static void downloadAndSaveBitmap(String url, String fileName, Context context){
        try {
            URL adress = new URL(url);
            InputStream input = adress.openStream();
            try {
                //The sdcard directory e.g. '/sdcard' can be used directly, or
                //more safely abstracted with getExternalStorageDirectory()
                FileOutputStream output = context.openFileOutput(fileName, context.MODE_PRIVATE);
                try {
                    byte[] buffer = new byte[2048];
                    int bytesRead = 0;
                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                        output.write(buffer, 0, bytesRead);
                    }
                } finally {
                    output.close();
                }
            } finally {
                input.close();
            }
        }catch (Exception e){}
    }
}
