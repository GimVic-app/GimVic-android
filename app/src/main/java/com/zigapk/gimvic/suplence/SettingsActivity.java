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
import android.view.Menu;
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
                if(left == 0){
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

        TextView safetySounterIndicator = (TextView) findViewById(R.id.safetyCounterIndicator);
        safetySounterIndicator.setText("To lahko spremenite še " + left + "-krat.");

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
