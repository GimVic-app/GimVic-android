package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by ziga on 9/15/14.
 */
public class Data {

    public static void refresh(Context context, boolean GUI){

        if(GUI) setRefreshingGuiState(true);

        if(Internet.isOnline(context)){
            new refreshAsyncTask().execute(context);
        }else {
            setRefreshingGuiState(false);
        }

    }


    private static class refreshAsyncTask extends AsyncTask<Context, String, String> {
        protected String doInBackground(Context... context) {

            downloadData(context[0]);
            return null;
        }

        protected void onPostExecute(String result) {
            renderData();
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

    private static void renderData(){

    }

    public static void downloadData(Context context){
        Urnik.downloadUrnik(context);
        Urnik.parseUrnik(context);
        Suplence.downloadSuplence();


    }


}
