package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
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

    public static Bitmap downloadBitmap(String url){
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
        }
        return bitmap;
    }

}
