package com.zigapk.gimvic.suplence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;


public class FirstActivity extends Activity {

    private static Context context;
    private static ArrayList<String> razredi = new ArrayList<String>();
    private static ArrayList<String> ucitelji = new ArrayList<String>();
    private static ArrayList<String> lepirazredi = new ArrayList<String>();
    private static ArrayList<String> izbirni = new ArrayList<String>();
    private static ChosenRazredi chosenRazredi = new ChosenRazredi();



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
                    Data.downloadData(context, true);
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
                        }
                    });
                }
            };

            wait.start();
        }else{

            //tell that there is no internet
            Dialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.offline))
                    .setCancelable(false)
                    .setMessage(getResources().getString(R.string.offline_explanation))
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(R.drawable.ic_launcher)
                    .show();

            int divierId = dialog.getContext().getResources()
                    .getIdentifier("android:id/titleDivider", null, null);
            View divider = dialog.findViewById(divierId);
            divider.setBackgroundColor(getResources().getColor(R.color.transparent));
        }


        final LinearLayout layout = (LinearLayout) findViewById(R.id.firstLinearLayout);
        final TextView choose = (TextView) findViewById(R.id.firstChoose);

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

                razredi = new ArrayList<String>(Arrays.asList(Urnik.parseRazredi(context).razredi));
                //filter
                filterRazredi();


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_list_item_1,
                        lepirazredi );
                lv.setAdapter(arrayAdapter);
                lv.setVisibility(View.VISIBLE);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String chosen = lepirazredi.get(position);

                        chosenRazredi.razredi.add(chosen);

                        if (chosen.contains("3") || chosen.contains("4")) {

                            //TODO: set izbirni

                        }

                        Settings.setRazredi(chosenRazredi, context);
                        Settings.setFirstOpened(false, context);
                        Settings.setUserMode(UserMode.MODE_UCENEC, context);

                        Intent intent = new Intent(context, Main.class);
                        startActivity(intent);
                        finish();
                    }
                });


            }
        });




        //set on click listener
        final Button profesor = (Button) findViewById(R.id.firstProfesorjiButton);
        profesor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                choose.setVisibility(View.GONE);


                final TextView indicator = (TextView) findViewById(R.id.passwordIndicator);
                indicator.setVisibility(View.VISIBLE);
                final EditText pass = (EditText) findViewById(R.id.password);
                pass.setVisibility(View.VISIBLE);
                pass.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        if(Security.sha256(s.toString()).equals(getString(R.string.passwordHash))){
                            pass.setVisibility(View.GONE);
                            indicator.setVisibility(View.GONE);

                            //hide keyboard
                            InputMethodManager imm = (InputMethodManager)getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(pass.getWindowToken(), 0);

                            TextView tv = (TextView) findViewById(R.id.firstChooseItem);
                            tv.setText("Izberite učitelja:");
                            tv.setVisibility(View.VISIBLE);
                            ListView lv = (ListView) findViewById(R.id.firstListView);

                            ucitelji = new ArrayList<String>(Arrays.asList(Urnik.parseUcitelji(context).ucitelji));
                            sortUcitelji();

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    context,
                                    android.R.layout.simple_list_item_1,
                                    ucitelji );
                            lv.setAdapter(arrayAdapter);
                            lv.setVisibility(View.VISIBLE);

                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    String chosen = ucitelji.get(position);

                                    Settings.setUcitelj(chosen, context);
                                    Settings.setFirstOpened(false, context);
                                    Settings.setUserMode(UserMode.MODE_UCITELJ, context);

                                    Intent intent = new Intent(context, Main.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });


                        }else if (s.toString().equals("")){
                            indicator.setText(getString(R.string.inputPassword));
                        }else{
                            indicator.setText(getString(R.string.wrongPass));
                        }
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                });

                Settings.setUserMode(UserMode.MODE_UCITELJ, context);
            }
        });
    }


    private static void filterRazredi(){

        for(String current : razredi){
            if(current.length() == 2){
                lepirazredi.add(current);
            }else {
                izbirni.add(current);
            }
        }

        Collections.sort(lepirazredi, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        Collections.sort(razredi, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
    }

    private static void sortUcitelji(){
        Collections.sort(ucitelji, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
    }


}
