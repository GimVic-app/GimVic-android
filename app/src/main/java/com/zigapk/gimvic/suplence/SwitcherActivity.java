package com.zigapk.gimvic.suplence;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class SwitcherActivity extends Activity {

    private static Context context;
    private static ArrayList<String> razredi = new ArrayList<String>();
    private static ArrayList<String> ucitelji = new ArrayList<String>();
    private static ArrayList<String> lepirazredi = new ArrayList<String>();
    private static ArrayList<String> izbirni = new ArrayList<String>();
    private static CharSequence[] izbirniCharSequences;
    private static ChosenRazredi chosenRazredi = new ChosenRazredi();
    private static boolean[] itemsChecked;
    private static boolean izbirniFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initialize activity
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_switcher);
        context = getApplication();


        final LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.switcherLinearLayout);
        final TextView chooseView = (TextView) findViewById(R.id.switcherChoose);

        //set OnClickListener
        Button dijaki = (Button) findViewById(R.id.switcherDijakiButton);
        dijaki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseView.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.GONE);

                TextView tv = (TextView) findViewById(R.id.switcherChooseItem);
                tv.setText("Izberite razred:");
                tv.setVisibility(View.VISIBLE);
                ListView lv = (ListView) findViewById(R.id.switcherListView);


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
                        final String chosen = lepirazredi.get(position);

                        chosenRazredi.razredi.add(chosen);
                        izbirniToCharSequences();

                        if (chosen.contains("3") || chosen.contains("4")) {

                            AlertDialog.Builder builder=new AlertDialog.Builder(SwitcherActivity.this);
                            builder.setTitle("Izberite izbirne predmete");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i = 0; i < izbirni.size(); i++) {
                                        if(itemsChecked[i]){
                                            chosenRazredi.razredi.add(izbirni.get(i));
                                        }
                                    }
                                    izbirniFinished = true;
                                }
                            });
                            builder.setMultiChoiceItems(izbirniCharSequences, whichToBeChecked(), new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    itemsChecked[which]=isChecked;
                                }
                            });
                            builder.show();

                        }else {
                            izbirniFinished = true;
                        }

                        new Thread() {
                            @Override
                            public void run() {

                                Settings.setTrueUrnikParsed(false, context);
                                Settings.setUrnikParsed(false, context);
                                Settings.setSuplenceParsed(false, context);
                                Settings.setHybridParsed(false, context);

                                while(!izbirniFinished){}

                                Settings.setRazredi(chosenRazredi, context);
                                Settings.setUserMode(UserMode.MODE_UCENEC, context);

                                //clean up and prepare for next time
                                cleanUp();
                                razredi = new ArrayList<String>();
                                ucitelji = new ArrayList<String>();
                                lepirazredi = new ArrayList<String>();
                                izbirni = new ArrayList<String>();
                                izbirniCharSequences = null;
                                chosenRazredi = new ChosenRazredi();
                                itemsChecked = null;
                                izbirniFinished = false;

                                parseEverything(context);

                                //run on ui thread
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        finish();
                                        Data.renderData(context, true);
                                        Data.setRefreshingGuiState(true);
                                    }
                                });
                            }
                        }.start();


                    }
                });

            }
        });


        //TODO: set for profesorji
        //set OnClickListener
        Button profesorji = (Button) findViewById(R.id.switcherProfesorjiButton);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cleanUp();
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

        Collections.sort(izbirni, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        itemsChecked = new boolean[izbirni.size()];
    }

    private static boolean[] whichToBeChecked(){
        boolean[] result = new boolean[izbirni.size()];
        for(boolean bool : result){
            bool = false;
        }
        return result;

    }

    private static void izbirniToCharSequences(){
        izbirniCharSequences = new CharSequence[izbirni.size()];
        for(int i = 0; i < izbirni.size(); i++){
            izbirniCharSequences[i] = izbirni.get(i);
        }
    }

    private static void sortUcitelji(){
        Collections.sort(ucitelji, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
    }

    private static void parseEverything(final Context context){
        new Thread() {
            @Override
            public void run() {
                Urnik.parsePersonalUrnik(context);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                Suplence.parse(context);
            }
        }.start();
    }

    //clean up and prepare for next time
    private static void cleanUp(){
        razredi.clear();
        ucitelji.clear();
        lepirazredi.clear();
        izbirni.clear();
        izbirniCharSequences = new CharSequence[0];
        chosenRazredi = new ChosenRazredi();
        itemsChecked = new boolean[0];
        izbirniFinished = false;
    }
}
