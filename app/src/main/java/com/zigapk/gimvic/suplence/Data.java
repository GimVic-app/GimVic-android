package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

/**
 * Created by ziga on 9/15/14.
 */

public class Data {

    public static boolean refreshing = false;

    public static void refresh(Context context, boolean GUI) {

        if (GUI) setRefreshingGuiState(true);

        new refreshAsyncTask().execute(context);


    }

    public static void setRefreshingGuiState(Boolean refreshing) {

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

    public static void renderData(final Context context, final boolean first) {
        new Thread() {
            @Override
            public void run() {
                int mode = Settings.getMode(context);

                while (!Settings.isUrnikParsed(context) || !Settings.isTrueUrnikParsed(context) || !Settings.isHybridParsed(context) || !Settings.areSuplenceParsed(context)){}

                if (mode == Mode.MODE_HYBRID) {
                    Suplence.renderHybrid(context);
                } else if (mode == Mode.MODE_SUPLENCE) Suplence.render(context);
                else if (mode == Mode.MODE_URNIK) Urnik.render(context);

                Main.isDataRendered = true;

            }
        }.start();
    }

    public static void downloadData(Context context, boolean first) {
        Urnik.downloadUrnik(context);
        if (first) {
            Urnik.parseUrnik(context);
        }
        Suplence.downloadSuplence(context);


    }

    public static void clearAllData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    Files.deleteDir(new File(appDir, s));
                }
            }
        }

        Settings.clearSharedPrefs(context);
    }

    private static class refreshAsyncTask extends AsyncTask<Context, Context, Context> {
        protected Context doInBackground(final Context... context) {

            if (!refreshing) {

                refreshing = true;
                if (Internet.isOnline(context[0])) {
                    downloadData(context[0], false);
                }

                final Context tempContext = context[0];

                new Thread() {
                    @Override
                    public void run() {
                        Suplence.parse(tempContext);
                        renderData(tempContext, false);
                        Data.refreshing = false;
                    }
                }.start();

            }

            while (!Other.layoutComponentsReady() || !Settings.isTrueUrnikParsed(context[0])) {
            }
            while (refreshing) {
            }

            return context[0];
        }

        protected void onPostExecute(Context context) {
            setRefreshingGuiState(false);
        }
    }

}