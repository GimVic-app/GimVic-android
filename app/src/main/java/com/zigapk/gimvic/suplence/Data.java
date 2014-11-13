package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

/**
 * Created by ziga on 9/15/14.
 */
public class Data {

    public static void refresh(Context context, boolean GUI){

        if(GUI) setRefreshingGuiState(true);

        new refreshAsyncTask().execute(context);


    }


    private static class refreshAsyncTask extends AsyncTask<Context, Context, Context> {
        protected Context doInBackground(Context... context) {

            if(Internet.isOnline(context[0])){
                downloadData(context[0]);
            }

            return context[0];
        }

        protected void onPostExecute(Context context) {
            renderData(context);
            setRefreshingGuiState(false);
        }
    }


    public static void setRefreshingGuiState(Boolean refreshing){

        Main.mRefreshing = refreshing;

        if (Main.mSwipeRefreshLayout1 != null) {
            Main.mSwipeRefreshLayout1.setRefreshing(refreshing);
        }
        if (Main.mSwipeRefreshLayout2 != null) {
            Main.mSwipeRefreshLayout2.setRefreshing(refreshing);
        }
        if (Main.mSwipeRefreshLayout3 != null) {
            Main.mSwipeRefreshLayout3.setRefreshing(refreshing);
        }
        if (Main.mSwipeRefreshLayout4 != null) {
            Main.mSwipeRefreshLayout4.setRefreshing(refreshing);
        }
        if (Main.mSwipeRefreshLayout5 != null) {
            Main.mSwipeRefreshLayout5.setRefreshing(refreshing);
        }

    }

    public static void renderData(Context context){

        int mode = Settings.getMode(context);

        if(mode == Mode.MODE_HYBRID){
            //TODO: uncomment
            //Suplence.render(Urnik.getPersonalUrnik(context));
            //TODO: coment
            Urnik.render(context);
        }else if (mode == Mode.MODE_SUPLENCE) Suplence.render();
        else if (mode == Mode.MODE_URNIK) Urnik.render(context);

    }

    public static void downloadData(Context context){

        //TODO: make it do so only once per day
        Urnik.downloadUrnik(context);
        Urnik.parseUrnik(context);
        Suplence.downloadSuplence();


    }

    public static void clearAllData(Context context){
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}
