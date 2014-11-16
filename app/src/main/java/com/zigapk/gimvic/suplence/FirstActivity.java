package com.zigapk.gimvic.suplence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;


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
                            //hide old elements
                            ProgressBar progressBar = (ProgressBar) findViewById(R.id.firstProgressBar);
                            progressBar.setVisibility(View.GONE);
                            TextView downloadingData = (TextView) findViewById(R.id.downloadingData);
                            downloadingData.setVisibility(View.GONE);

                            //show new
                            final LinearLayout layout = (LinearLayout) findViewById(R.id.firstLinearLayout);
                            layout.setVisibility(View.VISIBLE);
                            final TextView choose = (TextView) findViewById(R.id.firstChoose);
                            choose.setVisibility(View.VISIBLE);


                            //set on click listener
                            final Button dijak = (Button) findViewById(R.id.firstDijakiButton);
                            dijak.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    layout.setVisibility(View.GONE);
                                    choose.setVisibility(View.GONE);


                                    TextView tv = (TextView) findViewById(R.id.firstChooseItem);
                                    tv.setText("Izberite razred:");
                                    tv.setVisibility(View.VISIBLE);
                                    ListView lv = (ListView) findViewById(R.id.firstListView);
                                    final ArrayList<String> razredi = new ArrayList<String>(Arrays.asList(Urnik.parseRazredi(context).razredi));
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                            context,
                                            android.R.layout.simple_list_item_1,
                                            razredi );
                                    lv.setAdapter(arrayAdapter);
                                    lv.setVisibility(View.VISIBLE);

                                    lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        public void onItemSelected(AdapterView parentView, View childView, int position, long id){
                                            Intent intent = new Intent(context, Main.class);
                                            startActivity(intent);

                                            finish();

                                            Settings.setRazred(razredi.get(position), context);
                                            Settings.setFirstOpened(false, context);
                                        }

                                        public void onNothingSelected(AdapterView parentView) {

                                        }
                                    });

                                    Settings.setUserMode(UserMode.MODE_UCENEC, context);
                                }
                            });

                            //set on click listener
                            final Button profesor = (Button) findViewById(R.id.firstProfesorjiButton);
                            profesor.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    layout.setVisibility(View.GONE);
                                    choose.setVisibility(View.GONE);

                                    Settings.setUserMode(UserMode.MODE_UCITELJ, context);
                                }
                            });

                        }
                    });
                }
            };

            wait.start();
        }else{

        }
    }

}
