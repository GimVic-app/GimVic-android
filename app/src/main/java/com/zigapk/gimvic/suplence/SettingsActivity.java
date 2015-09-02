package com.zigapk.gimvic.suplence;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4CAF50")));
        bar.setIcon(R.drawable.ic_logo_white);
        bar.setTitle(Html.fromHtml("<font color='#ffffff'>" + getString(R.string.gimvic) + "</font>"));
        setContentView(R.layout.activity_settings);

        final int left = 4 - Settings.getSafetyCounter(getApplicationContext());


        Button switcher = (Button) findViewById(R.id.changeButton);
        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(left == 0 && Settings.getAdmin(getApplicationContext()) == false){
                    Dialog dialog = new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle(getResources().getString(R.string.no_more_trys_left))
                            .setCancelable(false)
                            .setMessage(getResources().getString(R.string.no_more_trys_left_explanation))
                            .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //nothing
                                }
                            })
                            .setIcon(R.drawable.ic_launcher)
                            .show();

                    int divierId = dialog.getContext().getResources()
                            .getIdentifier("android:id/titleDivider", null, null);
                    View divider = dialog.findViewById(divierId);
                    divider.setBackgroundColor(getResources().getColor(R.color.transparent));
                }else {
                    //launch switcher
                    startActivity(new Intent(SettingsActivity.this, SwitcherActivity.class));
                }
            }
        });

        Button modeButton = (Button) findViewById(R.id.modeButton);
        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mode = Settings.getMode(getApplicationContext());
                if (mode == 2){
                    Settings.setMode(0, getApplicationContext());
                }else {
                    Settings.setMode(mode + 1, getApplicationContext());
                }
                setModeButtonText();
            }
        });
        setModeButtonText();

        Button malicaButton = (Button) findViewById(R.id.malicaSettingsButton);
        malicaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mode = Settings.getMalicaMode(getApplicationContext());
                if (mode == 3){
                    Settings.setMalicaMode(0, getApplicationContext());
                }else {
                    Settings.setMalicaMode(mode + 1, getApplicationContext());
                }
                setMalicaButtonText();
            }
        });
        setMalicaButtonText();

        Button kosiloButton = (Button) findViewById(R.id.kosiloSettingsButton);
        kosiloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mode = Settings.getKosiloMode(getApplicationContext());
                if (mode == 1){
                    Settings.setKosiloMode(0, getApplicationContext());
                }else {
                    Settings.setKosiloMode(1, getApplicationContext());
                }
                setKosiloButtonText();
            }
        });
        setKosiloButtonText();

    }

    private void setMalicaButtonText() {
        Button malicaButton = (Button) findViewById(R.id.malicaSettingsButton);
        String text = "Malica: ";
        int mode = Settings.getMalicaMode(getApplicationContext());
        if(mode == JedilnikModes.MALICA_NAVADNA){
            text += "navadna";
        }else if(mode == JedilnikModes.MALICA_VEGSPERUTNINO){
            text += "vegetarijanska s perunino";
        }else if(mode == JedilnikModes.MALICA_VEGETARIJANSKA){
            text += "vegetarijanska";
        }else {
            text += "sadno-zelenjavna";
        }
        malicaButton.setText(text);
    }

    private void setKosiloButtonText() {
        Button kosiloButton = (Button) findViewById(R.id.kosiloSettingsButton);
        String text = "Kosilo: ";
        int mode = Settings.getKosiloMode(getApplicationContext());
        if(mode == JedilnikModes.KOSILO_NAVADNO){
            text += "navadno";
        }else {
            text += "vegetarijansko";
        }
        kosiloButton.setText(text);
    }

    private void setSafetyIndicatorText(){
        final int left = 4 - Settings.getSafetyCounter(getApplicationContext());
        TextView safetyCounterIndicator = (TextView) findViewById(R.id.safetyCounterIndicator);
        safetyCounterIndicator.setText("To lahko spremenite še " + left + "-krat.");
        if (left <= 1) safetyCounterIndicator.setTextColor(getResources().getColor(R.color.red));
        else safetyCounterIndicator.setTextColor(getResources().getColor(R.color.black));
    }

    private void setChosenIndiccatorText(){
        TextView tv = (TextView) findViewById(R.id.settingsChosenItemIndicator);
        String value = getResources().getString(R.string.chosenString) + " ";
        if(Settings.getUserMode(getApplicationContext()) == UserMode.MODE_UCENEC) value = value + "razred: " + Settings.getRazredi(getApplicationContext()).razredi.get(0);
        else value = value + "profesor: " + Settings.getProfesor(getApplicationContext());
        tv.setText(value);
    }

    public void onResume() {
        super.onResume();
        setSafetyIndicatorText();
        setChosenIndiccatorText();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            super.onBackPressed();
        }
        return true;
    }

    private void setModeButtonText(){
        Button modeButton = (Button) findViewById(R.id.modeButton);
        String text = "Prikazuj ";
        int mode = Settings.getMode(getApplicationContext());
        if(mode == Mode.MODE_HYBRID){
            text += "urnik in nadomeščanja";
        }else if(mode == Mode.MODE_SUPLENCE){
            text += "samo nadomeščanja";
        }else {
            text += "samo urnik";
        }
        modeButton.setText(text);
    }
}
