package com.zigapk.gimvic.suplence;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.zigapk.gimvic.suplence.exceptions.CouldNotReachServerException;

public class SettingsActivity extends AppCompatActivity {

    private static ChooserOptions options;
    private static ChosenOptions chosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        start();
        setListeners();

        try {
            ((TextView) findViewById(R.id.settings_version_tv)).setText(Html.fromHtml("Različica: ") + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    private void setGui() {
        ((android.support.v7.widget.SwitchCompat) findViewById(R.id.addSubstitutionsSwitch)).setChecked(chosen.addSubstitutions);

        if (!chosen.teacherMode)
            ((TextView) findViewById(R.id.chosenTeacherOrClass)).setText(Html.fromHtml(getString(R.string.chosen_classes) + " <b>" + chosen.classesToStr() + "</b>"));
        else
            ((TextView) findViewById(R.id.chosenTeacherOrClass)).setText(Html.fromHtml(getString(R.string.chosen_teacher) + " <b>" + chosen.teacher + "</b>"));

        int left = 5 - Settings.getSafetyCounter(getApplicationContext());
        findViewById(R.id.settings_choose_button).setEnabled(left > 0);

        ((TextView) findViewById(R.id.safetyCounterTV)).setText(Html.fromHtml(getString(R.string.times_left) + " <b>" + left + "-krat</b>."));

        ((Button) findViewById(R.id.snack_type_button)).setText(getString(R.string.chosen_snack) + " " + chosen.snack.replace("_", " "));
        ((Button) findViewById(R.id.lunch_type_button)).setText(getString(R.string.chosen_lunch) + " " + chosen.lunch.replace("_", " "));

        findViewById(R.id.settings_layout).setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        ((android.support.v7.widget.SwitchCompat) findViewById(R.id.addSubstitutionsSwitch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chosen.addSubstitutions = isChecked;
                Settings.setChosenOptions(chosen, getApplicationContext());
            }
        });

        findViewById(R.id.settings_switch_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((android.support.v7.widget.SwitchCompat) findViewById(R.id.addSubstitutionsSwitch)).toggle();
            }
        });

        findViewById(R.id.settings_choose_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ClassChooserActivity.class));
                finish();
            }
        });

        findViewById(R.id.snack_type_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = positionInArray(chosen.snack, options.snackTypes) + 1;
                if (i >= options.snackTypes.length) i = 0;
                chosen.snack = options.snackTypes[i];
                setGui();
                Settings.setChosenOptions(chosen, getApplicationContext());
            }
        });

        findViewById(R.id.lunch_type_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = positionInArray(chosen.lunch, options.lunchTypes) + 1;
                if (i >= options.lunchTypes.length) i = 0;
                chosen.lunch = options.lunchTypes[i];
                setGui();
                Settings.setChosenOptions(chosen, getApplicationContext());
            }
        });
    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    options = new ChooserOptions().download();
                    chosen = Settings.getChosenOptions(getApplicationContext());
                } catch (CouldNotReachServerException e) {
                    showCouldNotReachServerDialog();
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        setGui();
                        findViewById(R.id.settings_progressBar).setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    private int positionInArray(String object, String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (object.equals(arr[i])) return i;
        }
        return 0;
    }

    private void showCouldNotReachServerDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.could_not_reach_server_dialog_title)
                .setMessage(R.string.could_not_reach_server_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        dialog.show();
    }

}
