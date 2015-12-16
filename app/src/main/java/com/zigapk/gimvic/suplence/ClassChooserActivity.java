package com.zigapk.gimvic.suplence;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zigapk.gimvic.suplence.exceptions.CouldNotReachServerException;

import java.security.MessageDigest;
import java.util.ArrayList;

public class ClassChooserActivity extends AppCompatActivity {

    ChooserOptions options = new ChooserOptions();
    ArrayAdapter<String> arrayAdapter;
    private static boolean teacher = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_chooser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        addListeners();
        start();
    }

    private void addListeners() {
        (findViewById(R.id.button_student)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacher = false;
                ((TextView) findViewById(R.id.choose_item_textView)).setText(R.string.selectMainClass);
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                        R.layout.list_view_item,
                        options.mainClasses);
                ((ListView) findViewById(R.id.chooser_listView)).setAdapter(arrayAdapter);
                (findViewById(R.id.chooser_layout)).setVisibility(View.VISIBLE);
                (findViewById(R.id.chooser_student_or_teacher_layout)).setVisibility(View.GONE);
            }
        });

        (findViewById(R.id.button_teacher)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacher = true;
                if (Settings.getAdmin(getApplicationContext()) || Settings.wasProfesorsPassEntered(getApplicationContext())) {
                    ((TextView) findViewById(R.id.choose_item_textView)).setText(R.string.selectTeacher);
                    arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.list_view_item,
                            options.teachers);
                    ((ListView) findViewById(R.id.chooser_listView)).setAdapter(arrayAdapter);
                    (findViewById(R.id.chooser_layout)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.chooser_student_or_teacher_layout)).setVisibility(View.GONE);
                } else {
                    (findViewById(R.id.chooserPassView)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.chooser_student_or_teacher_layout)).setVisibility(View.GONE);
                }
            }
        });

        ((EditText) findViewById(R.id.passInput)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                String hash = sha256(str);
                if (hash.equals("269433b0e58e6c074b3b3ef17a343fbe140680f59b7ff991feea55a054175214")) {
                    Settings.setAdmin(true, getApplicationContext());
                } else if (hash.equals("c00c09606b70d76cd018a432116c3c91ef683d9756eaae7f93574c5789cdcacb")) {
                    Settings.setProfesorsPassEntered(true, getApplicationContext());
                }

                if (hash.equals("269433b0e58e6c074b3b3ef17a343fbe140680f59b7ff991feea55a054175214") || hash.equals("c00c09606b70d76cd018a432116c3c91ef683d9756eaae7f93574c5789cdcacb")) {
                    ((TextView) findViewById(R.id.choose_item_textView)).setText(R.string.selectTeacher);
                    arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.list_view_item,
                            options.teachers);
                    ((ListView) findViewById(R.id.chooser_listView)).setAdapter(arrayAdapter);
                    (findViewById(R.id.chooser_layout)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.chooserPassView)).setVisibility(View.GONE);

                }
            }
        });

        ((ListView) findViewById(R.id.chooser_listView)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (teacher) {
                    ChosenOptions chosen = Settings.getChosenOptions(getApplicationContext());
                    chosen.classes.clear();
                    chosen.teacher = options.teachers[position];
                    chosen.teacherMode = true;
                    Settings.setChosenOptions(chosen, getApplicationContext());
                    if (!Settings.getAdmin(getApplicationContext()))
                        Settings.setSafetyCounter(Settings.getSafetyCounter(getApplicationContext()) + 1, getApplicationContext());
                    if (!Settings.isDataConfigured(getApplicationContext())) {
                        startActivity(new Intent(ClassChooserActivity.this, Main.class));
                        Settings.setDataConfigured(true, getApplicationContext());
                    }
                    finish();
                } else {
                    final String chosenMainClass = options.mainClasses[position];
                    if (chosenMainClass.contains("3") || chosenMainClass.contains("4")) {
                        final ArrayList<String> additionalClasses = new ArrayList<>();
                        new AlertDialog.Builder(ClassChooserActivity.this, R.style.MyAlertDialogTheme)
                                .setCancelable(false)
                                .setTitle(R.string.selectAditionalClass)
                                .setMultiChoiceItems(options.additionalClasses, null, new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        if (isChecked)
                                            additionalClasses.add(options.additionalClasses[which]);
                                        else
                                            additionalClasses.remove(options.additionalClasses[which]);
                                    }
                                })
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ChosenOptions chosen = Settings.getChosenOptions(getApplicationContext());
                                        additionalClasses.add(chosenMainClass);
                                        chosen.classes = additionalClasses;
                                        chosen.teacherMode = false;
                                        Settings.setChosenOptions(chosen, getApplicationContext());
                                        if (!Settings.getAdmin(getApplicationContext()))
                                            Settings.setSafetyCounter(Settings.getSafetyCounter(getApplicationContext()) + 1, getApplicationContext());
                                        if (!Settings.isDataConfigured(getApplicationContext())) {
                                            startActivity(new Intent(ClassChooserActivity.this, Main.class));
                                            Settings.setDataConfigured(true, getApplicationContext());
                                        }
                                        finish();
                                    }
                                })
                                .create().show();
                    } else {
                        ChosenOptions chosen = Settings.getChosenOptions(getApplicationContext());
                        chosen.classes.clear();
                        chosen.classes.add(chosenMainClass);
                        chosen.teacherMode = false;
                        Settings.setChosenOptions(chosen, getApplicationContext());
                        if (!Settings.isDataConfigured(getApplicationContext())) {
                            startActivity(new Intent(ClassChooserActivity.this, Main.class));
                            Settings.setDataConfigured(true, getApplicationContext());
                        }
                        if (!Settings.getAdmin(getApplicationContext()))
                            Settings.setSafetyCounter(Settings.getSafetyCounter(getApplicationContext()) + 1, getApplicationContext());
                        finish();
                    }
                }
            }
        });
    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    options = new ChooserOptions().download();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            (findViewById(R.id.chooser_progressBar)).setVisibility(View.GONE);
                            (findViewById(R.id.chooser_student_or_teacher_layout)).setVisibility(View.VISIBLE);
                        }
                    });

                } catch (CouldNotReachServerException e) {
                    showCouldNotReachServerDialog();
                }
            }
        }).start();
    }

    private void showCouldNotReachServerDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(ClassChooserActivity.this)
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

    private static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
