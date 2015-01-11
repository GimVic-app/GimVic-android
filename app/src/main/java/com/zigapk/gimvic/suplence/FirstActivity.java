package com.zigapk.gimvic.suplence;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


public class FirstActivity extends Activity {

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

            if(android.os.Build.VERSION.SDK_INT <= 19){
                //if kitkat or lower
                int divierId = dialog.getContext().getResources()
                        .getIdentifier("android:id/titleDivider", null, null);
                View divider = dialog.findViewById(divierId);
                divider.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
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
                        R.layout.list_view_item,
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

                            AlertDialog.Builder builder=new AlertDialog.Builder(FirstActivity.this);
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

                                while(!izbirniFinished){}


                                Settings.setRazredi(chosenRazredi, context);
                                Settings.setFirstOpened(false, context);
                                Settings.setUserMode(UserMode.MODE_UCENEC, context);

                                parseEverything(context);

                                //run on ui thread
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        Intent intent = new Intent(context, Main.class);
                                        startActivity(intent);
                                        Data.renderData(context);
                                        finish();
                                    }
                                });
                            }
                        }.start();
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
                                    R.layout.list_view_item,
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
                                    Settings.setProfesorsPassEntered(true, context);

                                    parseEverything(context);

                                    Intent intent = new Intent(context, Main.class);
                                    startActivity(intent);
                                    Data.renderData(context);
                                    finish();
                                }
                            });


                        }else if(Security.sha256(s.toString()).equals(getString(R.string.adminPasswordHash))){
                            //set admin and backup
                            Settings.setAdmin(true, context);
                            ExternalData.backupToExternalStorage(context);

                            //relaunch app
                            Intent mStartActivity = new Intent(context, Main.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);

                            //reset data
                            Settings.setFirstOpened(false, context);
                            Settings.setUrnikDownloaded(false, context);
                            Settings.setTrueUrnikParsed(false, context);
                            Settings.setUrnikHash("a", context);
                            Settings.setSuplenceParsed(false, context);
                            Settings.setHybridParsed(false, context);
                            Settings.setFirstOpened(true, context);
                            Settings.setProfesorsPassEntered(true, context);
                            System.exit(0);


                        } else if (s.toString().equals("")){
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


        //sync bcg data
        new Thread() {
            @Override
            public void run() {
                ExternalData.syncExternalBackup(context, true);
            }
        }.start();

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
                Urnik.parseUrnik(context);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                Suplence.parse(context);
            }
        }.start();
    }

}
