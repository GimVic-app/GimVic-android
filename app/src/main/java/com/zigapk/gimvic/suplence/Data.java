package com.zigapk.gimvic.suplence;

/**
 * Created by ziga on 9/15/14.
 */
public class Data {

    public static void refresh(){

        setRefreshingGuiState(true);

        new Thread()
        {
            public void run() {
                try {
                    Thread.currentThread().sleep(1500);
                }catch (Exception e){}

                setRefreshingGuiState(false);
            }
        }.start();

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
}
