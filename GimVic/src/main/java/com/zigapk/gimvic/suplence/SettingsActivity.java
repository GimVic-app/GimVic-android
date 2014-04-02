package com.zigapk.gimvic.suplence;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class SettingsActivity extends PreferenceActivity {

    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnCreateBackGroundTask task = new OnCreateBackGroundTask();
        task.execute("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        Preference myPref = (Preference) findPreference("about");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                Intent about = new Intent(SettingsActivity.this, About.class);
                startActivity(about);
                return false;
            }
        });

        Preference myPref3 = (Preference) findPreference("spremeni_filtre");
        myPref3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(getResources().getString(R.string.set_data_filters_title));
                builder.setMessage(getResources().getString(R.string.set_data_filters_text));
                builder.setIcon(R.drawable.ic_launcher);


                final EditText input = new EditText(SettingsActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setGravity(Gravity.CENTER_HORIZONTAL);

                builder.setView(input);
                if (getFileValue("filtri.value", getApplicationContext()) != null) {
                    input.setText(getFileValue("filtri.value", getApplicationContext()));
                }
                builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().length() > 0) {
                        }
                        try {
                            writeToFile("filtri.value", input.getText().toString(), getApplicationContext(), 1);
                        } catch (Exception e) {
                        }
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return false;
            }
        });

        Preference myPref4 = (Preference) findPreference("check_for_update");
        myPref4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                final String appName = getApplicationContext().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse("http://play.google.com/store/apps/details?id=" + appName)));
                }
                return false;
            }
        });
    }


    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null
                );

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    public static void deleteFile(String fileName, Context context) {
        context.deleteFile(fileName);
    }

    private class OnCreateBackGroundTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            /* ODJAVA ZA EMŠO
            Preference myPref = (Preference) findPreference("odjava");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Pozabi EMŠO?")
                            .setMessage("Aplikacija potem ne bo več mogla preverjati vaših suplenc.")
                            .setIcon(R.drawable.ic_launcher)
                            .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    Context context = getApplicationContext();
                                    deleteFile("emso.value", context);
                                    deleteAllFiles();
                                    clearApplicationData();

                                    new AlertDialog.Builder(SettingsActivity.this)
                                            .setTitle("Aplikacija se mora zapreti")
                                            .setCancelable(false)
                                            .setIcon(R.drawable.ic_launcher)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    finishAffinity();
                                                }
                                            })
                                            .show();
                                }
                            })
                            .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //nothing
                                }
                            })
                            .show();
                    return false;
                }
            });*/


        }
    }

    public void deleteAllFiles() {

        Context context = getApplicationContext();

        if (context.getFilesDir().isDirectory()) {
            String[] children = context.getFilesDir().list();
            for (int i = 0; i < children.length; i++) {
                new File(context.getFilesDir(), children[i]).delete();
            }
        }
    }

    public static boolean writeToFile(String fileName, String value,
                                      Context context, int writeOrAppendMode) {
        // just make sure it's one of the modes we support
        if (writeOrAppendMode != Context.MODE_WORLD_READABLE
                && writeOrAppendMode != Context.MODE_WORLD_WRITEABLE
                && writeOrAppendMode != Context.MODE_APPEND) {
            return false;
        }
        try {
            /*
             * We have to use the openFileOutput()-method the ActivityContext
             * provides, to protect your file from others and This is done for
             * security-reasons. We chose MODE_WORLD_READABLE, because we have
             * nothing to hide in our file
             */
            FileOutputStream fOut = context.openFileOutput(fileName,
                    writeOrAppendMode);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            // Write the string to the file
            osw.write(value);
            // save and close
            osw.flush();
            osw.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                super.onBackPressed();
        }

        return true;
    }


    public static String getFileValue(String fileName, Context context) {
        try {
            StringBuffer outStringBuf = new StringBuffer();
            String inputLine = "";
            /*
             * We have to use the openFileInput()-method the ActivityContext
             * provides. Again for security reasons with openFileInput(...)
             */
            FileInputStream fIn = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader inBuff = new BufferedReader(isr);
            while ((inputLine = inBuff.readLine()) != null) {
                outStringBuf.append(inputLine);
                //outStringBuf.append("\n");
            }
            inBuff.close();
            return outStringBuf.toString();
        } catch (IOException e) {
            return null;
        }
    }


    public boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.getBoolean("wifi_only", false)) {
                if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()) {
                    return false;
                } else return true;
            }
            return true;
        }
        return false;
    }

    private class HttpAsyncTaskForLastVersion extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String response) {

            if (isOnline()) {
                if (!response.equals(null) && !response.equals("Did not work!")) {

                    try {
                        response = response.replaceAll("\n", "");
                        Integer min_version = Integer.parseInt(response);

                        if (min_version > Main.MyVersion) {
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle(getResources().getString(R.string.update_title))
                                    .setMessage(getResources().getString(R.string.update_text))
                                    .setCancelable(true)
                                    .setIcon(R.drawable.ic_launcher)
                                    .setPositiveButton(getResources().getString(R.string.install), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {

                                                    new HttpAsyncTaskForFileUrl().execute(Main.server_name + "/" + Main.hash_for_suplence + "/" + "android/last_version_url.html");

                                                }
                                            }
                                    )
                                    .

                                            setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            //nič
                                                        }
                                                    }
                                            )
                                    .

                                            show();

                        } else {
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.last_update_already_installed), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }

                }
            }

        }
    }


    public String GET(String url) {
        InputStream inputStream = null;
        String result = "";

        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();


            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));


            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();


            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "null";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTaskForFileUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String response) {

            //opens broswer
            String file_url = response;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(file_url));
            startActivity(i);
            finish();

        }
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }

    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private class pocistiPodatke extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            Context context = getApplicationContext();
            //String emso = getFileValue("emso.value", context);
            String first_time = getFileValue("first.time", context);
            String filters = getFileValue("filtri.value", context);
            String opened_offline = getFileValue("opened.offline", context);
            deleteAllFiles();
            //writeToFile("emso.value", emso, context, 1);
            if (first_time != null) writeToFile("first.time", first_time, context, 1);
            if (filters != null) writeToFile("filtri.value", filters, context, 1);
            if (opened_offline != null) writeToFile("opened.offline", opened_offline, context, 1);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.data_cleared), Toast.LENGTH_LONG).show();
        }
    }
}
