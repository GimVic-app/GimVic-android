package com.zigapk.gimvic.suplence;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;


public class FirstActivity extends Activity {

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_first);

        context = getApplicationContext();

        Data.clearAllData(context);

        if(Internet.isOnline(context)){
            Thread download = new Thread()
            {
                @Override
                public void run() {
                    Data.downloadData(context);
                }
            };

            download.start();

            Thread wait = new Thread()
            {
                @Override
                public void run() {
                    while (!Settings.isUrnikDownloaded(context)){
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressBar progressBar = (ProgressBar) findViewById(R.id.firstProgressBar);
                            progressBar.setVisibility(View.GONE);
                            TextView downloadingData = (TextView) findViewById(R.id.downloadingData);
                            downloadingData.setVisibility(View.GONE);
                        }
                    });
                }
            };

            wait.start();
        }else{

        }
    }

}
