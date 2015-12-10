package com.zigapk.gimvic.suplence;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.zigapk.gimvic.suplence.exceptions.CouldNotReachServerException;

public class FirstActivity extends AppCompatActivity {

    private static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity = this;

        runAll();

    }

    public void runAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ChooserOptions options = new ChooserOptions().download();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);

                            final ChosenOptions choosen = new ChosenOptions();

                            new AlertDialog.Builder(FirstActivity.this)
                                    .setTitle(R.string.selectMainClass)
                                    .setItems(options.mainClasses, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            choosen.classes.add(options.mainClasses[which]);

                                            if (options.mainClasses[which].contains("3") || options.mainClasses[which].contains("4")) {
                                                new AlertDialog.Builder(FirstActivity.this, R.style.MyAlertDialogTheme)
                                                        .setTitle(R.string.selectAditionalClass)
                                                        .setMultiChoiceItems(options.additionalClasses, null, new DialogInterface.OnMultiChoiceClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                                                if (isChecked) {
                                                                    choosen.classes.add(options.additionalClasses[which]);
                                                                } else if (choosen.classes.contains(options.additionalClasses[which])) {
                                                                    choosen.classes.remove(choosen.classes.indexOf(options.additionalClasses[which]));
                                                                }
                                                            }
                                                        })
                                                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                new AlertDialog.Builder(FirstActivity.this)
                                                                        .setTitle(R.string.selectSnackType)
                                                                        .setItems(options.parsedSnackTypes(), new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog, int which) {

                                                                                choosen.snack = options.snackTypes[which];
                                                                                new AlertDialog.Builder(FirstActivity.this)
                                                                                        .setTitle(R.string.selectLunchType)
                                                                                        .setItems(options.parsedLunchTypes(), new DialogInterface.OnClickListener() {
                                                                                            public void onClick(DialogInterface dialog, int which) {

                                                                                                choosen.lunch = options.lunchTypes[which];
                                                                                                Settings.setChosenOptions(choosen, getApplicationContext());
                                                                                                Settings.setDataConfigured(true, getApplicationContext());

                                                                                                startActivity(new Intent(FirstActivity.this, Main.class));
                                                                                                finish();
                                                                                            }
                                                                                        })
                                                                                        .create()
                                                                                        .show();
                                                                            }
                                                                        })
                                                                        .create()
                                                                        .show();
                                                            }
                                                        })
                                                        .create()
                                                        .show();
                                            } else {
                                                new AlertDialog.Builder(FirstActivity.this)
                                                        .setTitle(R.string.selectSnackType)
                                                        .setItems(options.parsedSnackTypes(), new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                choosen.snack = options.snackTypes[which];
                                                                new AlertDialog.Builder(FirstActivity.this)
                                                                        .setTitle(R.string.selectLunchType)
                                                                        .setItems(options.parsedLunchTypes(), new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog, int which) {

                                                                                choosen.lunch = options.lunchTypes[which];
                                                                                Settings.setChosenOptions(choosen, getApplicationContext());
                                                                                Settings.setDataConfigured(true, getApplicationContext());

                                                                                startActivity(new Intent(FirstActivity.this, Main.class));
                                                                                finish();
                                                                            }
                                                                        })
                                                                        .create()
                                                                        .show();
                                                            }
                                                        })
                                                        .create()
                                                        .show();
                                            }
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    });
                } catch (CouldNotReachServerException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            showCouldNotReachServerDialog();
                        }
                    });
                }
            }
        }).start();
    }

    public static void showCouldNotReachServerDialog() {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.could_not_reach_server_dialog_title)
                .setMessage(R.string.could_not_reach_server_dialog_message)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
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
