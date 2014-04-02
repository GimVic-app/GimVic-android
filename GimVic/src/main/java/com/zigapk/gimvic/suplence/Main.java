package com.zigapk.gimvic.suplence;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Main extends Activity implements ActionBar.TabListener {

    private Boolean mToogleIntermediate = false; //Spremenljivka, ki vedno pove ali je gumb za osveži slika ali vrteči se krogec (ang. progress bar).
    public static Integer MyVersion = 3; //Zaporedna številka verzije (posodobitve).
    public static String server_name = "http://app.gimvic.org"; //naslov strežnika
    public static String hash_for_suplence = "d0941e68da8f38151ff86a61fc59f7c5cf9fcaa2"; //Del spletnega naslova.
    public static String hash_for_json_to_xml = "f5f5d4903e9686b21f49cd417d24779001b432a5"; //Del spletnega naslova.

    public SwipeRefreshLayout mSwipeRefreshLayoutOne;
    public SwipeRefreshLayout mSwipeRefreshLayoutTwo;
    public SwipeRefreshLayout mSwipeRefreshLayoutThree;

    //Ostale spremenljivke za postavitev strani.
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //funkcija, ki se požene, ko se aplikacija odpre

        //operacijskemu sistemu povemo, da se je aplikacija odprla

        super.onCreate(savedInstanceState);


        //nastavimo gumb za osvežitev podatkov tako, da ga lahko tudi kasneje zamenjamo s "progress barom"
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(mToogleIntermediate);

        //postavimo postavitev strani
        setContentView(R.layout.main);

        //postavimo orodno vrstico
        final ActionBar actionBar = getActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        //naredimo 3 zslone (zadanes, jutri in pojutrišnjem)
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }

        //poženemo proces v ozadju, ki bo poskrbel za osvežitev, preverjanje posodobitev itd.
        OnCreateBackGroundTask task = new OnCreateBackGroundTask();
        task.execute("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        WaitABit task = new WaitABit();
        task.execute("");
    }

    @Override
    public void onBackPressed() {
        //funkcija se požene, ko uporabnik pritisne gumb nazaj

        //ustavimo vse procese v ozadju
        killAllTAsks();

        //Operacijskemu sistemu povemo, da je uporabnik pritisnil tipko nazaj, ta pa aplikacijo zapre.
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_refresh);
        item.setVisible(!mToogleIntermediate);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settings = new Intent(Main.this, SettingsActivity.class);
            startActivity(settings);
            killAllTAsks();
            return true;
        } else if (id == R.id.action_refresh) {

            refresh(1);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new com.zigapk.gimvic.suplence.FragmentOne();
            } else if (position == 1) {
                return new com.zigapk.gimvic.suplence.FragmentTwo();
            } else {
                return new com.zigapk.gimvic.suplence.FragmentThree();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date today = new Date();
            Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
            Date day_after_tomorrow = new Date(tomorrow.getTime() + (1000 * 60 * 60 * 24));

            String today_string = sdf.format(today);
            String tomorrow_string = sdf.format(tomorrow);
            String day_after_tomorrow_string = sdf.format(day_after_tomorrow);

            today_string = today_string.toLowerCase();
            tomorrow_string = tomorrow_string.toLowerCase();
            day_after_tomorrow_string = day_after_tomorrow_string.toLowerCase();

            today_string = today_string.replaceAll("monday", "ponedeljek");
            today_string = today_string.replaceAll("tuesday", "torek");
            today_string = today_string.replaceAll("wednesday", "sreda");
            today_string = today_string.replaceAll("thursday", "četrtek");
            today_string = today_string.replaceAll("friday", "petek");
            today_string = today_string.replaceAll("saturday", "sobota");
            today_string = today_string.replaceAll("sunday", "nedelja");

            tomorrow_string = tomorrow_string.replaceAll("monday", "ponedeljek");
            tomorrow_string = tomorrow_string.replaceAll("tuesday", "torek");
            tomorrow_string = tomorrow_string.replaceAll("wednesday", "sreda");
            tomorrow_string = tomorrow_string.replaceAll("thursday", "četrtek");
            tomorrow_string = tomorrow_string.replaceAll("friday", "petek");
            tomorrow_string = tomorrow_string.replaceAll("saturday", "sobota");
            tomorrow_string = tomorrow_string.replaceAll("sunday", "nedelja");

            day_after_tomorrow_string = day_after_tomorrow_string.replaceAll("monday", "ponedeljek");
            day_after_tomorrow_string = day_after_tomorrow_string.replaceAll("tuesday", "torek");
            day_after_tomorrow_string = day_after_tomorrow_string.replaceAll("wednesday", "sreda");
            day_after_tomorrow_string = day_after_tomorrow_string.replaceAll("thursday", "četrtek");
            day_after_tomorrow_string = day_after_tomorrow_string.replaceAll("friday", "petek");
            day_after_tomorrow_string = day_after_tomorrow_string.replaceAll("saturday", "sobota");
            day_after_tomorrow_string = day_after_tomorrow_string.replaceAll("sunday", "nedelja");

            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return today_string.toUpperCase(l);
                case 1:
                    return tomorrow_string.toUpperCase(l);
                case 2:
                    return day_after_tomorrow_string.toUpperCase(l);
            }
            return null;
        }
    }

    public class OnCreateBackGroundTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {


            //kasneje mora bit file emso.value
            Context context = getApplicationContext();
            String emso = working_with_files.getFileValue("first.time", context);

            return emso;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result == null) {
                Intent about = new Intent(Main.this, ScreenSlideActivity.class);
                startActivity(about);
                finish();
            } else {

                UpdaterTask updater = new UpdaterTask();
                updater.execute("");

                WaitABit task = new WaitABit();
                task.execute("");
                refresh(1);

                if (isOnline()) {
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    String hach_od_maca = "fail";
                    try {
                        hach_od_maca = methods.sha1(methods.getMacAddress(getApplicationContext()));
                    } catch (Exception e) {
                    }

                    String url = server_name + "/" + hash_for_suplence + "/" + "analitika/analitika.php?" + "mac_hash=" + hach_od_maca + "&type=mobile&os=android" + currentapiVersion + "&version=" + getResources().getString(R.string.version) + "&development=false" + "&opened_offline=" + working_with_files.getFileValue("opened.offline", getApplicationContext());
                    klicZaAnalitiko klic = new klicZaAnalitiko();
                    klic.execute(url);
                } else {
                    int value = 0;
                    try {
                        value = Integer.parseInt(working_with_files.getFileValue("opened.offline", getApplicationContext()));
                    } catch (Exception e) {
                    }

                    working_with_files.writeToFile("opened.offline", String.valueOf(value + 1), getApplicationContext(), 1);
                }

                working_with_files.clearOldData(getApplicationContext());
            }

        }
    }

    public void killAllTAsks() {

        //Funkcja ustvi vsa aktivna opravila v ozadju.
        HttpAsyncTask task = new HttpAsyncTask();
        task.cancel(true);
        OnCreateBackGroundTask task2 = new OnCreateBackGroundTask();
        task2.cancel(true);
        UpdaterTask task3 = new UpdaterTask();
        task3.cancel(true);
        HttpAsyncTaskForMinVersion task4 = new HttpAsyncTaskForMinVersion();
        task4.cancel(true);

    }

    private class WaitABit extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            refreshScreen();
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


    public String GET(String url, Boolean writeToFiles) {
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
                result = methods.convertInputStreamToString(inputStream);
            else
                result = "null";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }


        if (writeToFiles) {

            Context context = getApplicationContext();

            //sets filename to date (finds it from url - date must be at the end)
            String whattoreplace = url.substring(0, url.indexOf("?"));
            url = url.replace(whattoreplace, "");
            url = url.replaceAll("\\?", "");
            url = url.replaceAll("datum=", "");
            String filename = url + ".xml";

           /*if(isXmlValid(result)){
                working_with_files.writeToFile(filename, result, context, 1);
            }else{
                Toast.makeText(getBaseContext(), "Povezave s strežnikom ni mogoče vzpostaviti.", Toast.LENGTH_SHORT).show();
            }*/
            working_with_files.writeToFile(filename, result, context, 1);
        }

        return result;
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0], true);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        }

    }

    private class HttpAsyncTaskForLasOne extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0], true);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            toogleRefreshing(false);
            refreshScreen();
        }

    }


    public void refreshScreen() {
        Calendar cal = Calendar.getInstance();


        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH);
        month++;
        Integer day = cal.get(Calendar.DAY_OF_MONTH);


        String firstdate = year.toString() + "-" + month.toString() + "-" + day.toString();

        day++;

        if (!methods.isValidDate(month.toString() + "/" + day.toString() + "/" + year.toString())) {
            if (month == 12) {
                month = 1;
                year++;

            } else {
                month++;
            }
            day = 1;

        }


        String seconddate = year.toString() + "-" + month.toString() + "-" + day.toString();
        day++;
        if (!methods.isValidDate(month.toString() + "/" + day.toString() + "/" + year.toString())) {
            if (month == 12) {
                month = 1;
                year++;

            } else {
                month++;
            }
            day = 1;

        }
        String thirddate = year.toString() + "-" + month.toString() + "-" + day.toString();

        Context context = getApplicationContext();
        String xml_danes = working_with_files.getFileValue(firstdate + ".xml", context);
        String xml_jutri = working_with_files.getFileValue(seconddate + ".xml", context);
        String xml_pojutrišnjem = working_with_files.getFileValue(thirddate + ".xml", context);

        String string_filtri = working_with_files.getFileValue("filtri.value", context);
        ArrayList<String> filtri = methods.getFiltri(string_filtri);

        if (xml_danes == null) xml_danes = getResources().getString(R.string.no_data);
        if (xml_jutri == null) xml_jutri = getResources().getString(R.string.no_data);
        if (xml_pojutrišnjem == null) xml_pojutrišnjem = getResources().getString(R.string.no_data);

        TextView nadomescanja_danes = (TextView) findViewById(R.id.nadomescanja_danes_text);
        TextView menjava_predmeta_danes = (TextView) findViewById(R.id.menjava_predmeta_danes_text);
        TextView menjava_ur_danes = (TextView) findViewById(R.id.menjava_ur_danes_text);
        TextView menjava_ucilnic_danes = (TextView) findViewById(R.id.menjava_ucilnic_danes_text);

        TextView nadomescanja_jutri = (TextView) findViewById(R.id.nadomescanja_jutri_text);
        TextView menjava_predmeta_jutri = (TextView) findViewById(R.id.menjava_predmeta_jutri_text);
        TextView menjava_ur_jutri = (TextView) findViewById(R.id.menjava_ur_jutri_text);
        TextView menjava_ucilnic_jutri = (TextView) findViewById(R.id.menjava_ucilnic_jutri_text);

        TextView nadomescanja_pojutrišnjem = (TextView) findViewById(R.id.nadomescanja_pojutrišnjem_text);
        TextView menjava_predmeta_pojutrišnjem = (TextView) findViewById(R.id.menjava_predmeta_pojutrišnjem_text);
        TextView menjava_ur_pojutrišnjem = (TextView) findViewById(R.id.menjava_ur_pojutrišnjem_text);
        TextView menjava_ucilnic_pojutrišnjem = (TextView) findViewById(R.id.menjava_ucilnic_pojutrišnjem_text);

        try {
            nadomescanja_danes.setText(dobi_xml.tekstZaNadomescanja(xml_danes, filtri));
        } catch (Exception e) {
        }
        try {
            menjava_predmeta_danes.setText(dobi_xml.tekstZaMenjavaPredmeta(xml_danes, filtri));
        } catch (Exception e) {
        }
        try {
            menjava_ur_danes.setText(dobi_xml.tekstZaMenjavaUr(xml_danes, filtri));
        } catch (Exception e) {
        }
        try {
            menjava_ucilnic_danes.setText(dobi_xml.tekstZaMenjavaUcilnic(xml_danes, filtri));
        } catch (Exception e) {
        }


        try {
            nadomescanja_jutri.setText(dobi_xml.tekstZaNadomescanja(xml_jutri, filtri));
        } catch (Exception e) {
        }
        try {
            menjava_predmeta_jutri.setText(dobi_xml.tekstZaMenjavaPredmeta(xml_jutri, filtri));
        } catch (Exception e) {
        }
        try {
            menjava_ur_jutri.setText(dobi_xml.tekstZaMenjavaUr(xml_jutri, filtri));
        } catch (Exception e) {
        }
        try {
            menjava_ucilnic_jutri.setText(dobi_xml.tekstZaMenjavaUcilnic(xml_jutri, filtri));
        } catch (Exception e) {
        }


        try {
            nadomescanja_pojutrišnjem.setText(dobi_xml.tekstZaNadomescanja(xml_pojutrišnjem, filtri));
        } catch (Exception e) {
        }
        try {
            menjava_predmeta_pojutrišnjem.setText(dobi_xml.tekstZaMenjavaPredmeta(xml_pojutrišnjem, filtri));
        } catch (Exception e) {
        }
        try {
            menjava_ur_pojutrišnjem.setText(dobi_xml.tekstZaMenjavaUr(xml_pojutrišnjem, filtri));
        } catch (Exception e) {
        }
        try {
            menjava_ucilnic_pojutrišnjem.setText(dobi_xml.tekstZaMenjavaUcilnic(xml_pojutrišnjem, filtri));
        } catch (Exception e) {
        }
    }


    public void toogleRefreshing(Boolean refreshing) {
        mToogleIntermediate = refreshing;
        if (com.zigapk.gimvic.suplence.FragmentOne.mSwipeRefreshLayoutOne != null) {
            com.zigapk.gimvic.suplence.FragmentOne.mSwipeRefreshLayoutOne.setRefreshing(refreshing);
        }
        if (com.zigapk.gimvic.suplence.FragmentTwo.mSwipeRefreshLayoutTwo != null) {
            com.zigapk.gimvic.suplence.FragmentTwo.mSwipeRefreshLayoutTwo.setRefreshing(refreshing);
        }
        if (com.zigapk.gimvic.suplence.FragmentThree.mSwipeRefreshLayoutThree != null) {
            com.zigapk.gimvic.suplence.FragmentThree.mSwipeRefreshLayoutThree.setRefreshing(refreshing);
        }
        setProgressBarIndeterminateVisibility(mToogleIntermediate);
        this.invalidateOptionsMenu();
    }

    public void refresh(Integer date_number) {

        // || (date_number == 2 || date_number == 3) ker pobriše file za 2 in 3
        if ((isOnline() && !mToogleIntermediate) || (date_number == 2 || date_number == 3)) {
            //refreshScreen();
            toogleRefreshing(true);
            try {

                //NA KONCU MORA TUKAJ BITI PREGLED ZA 3 DNI NAPREJ

                Calendar cal = Calendar.getInstance();


                Integer year = cal.get(Calendar.YEAR);
                Integer month = cal.get(Calendar.MONTH);
                month++;
                Integer day = cal.get(Calendar.DAY_OF_MONTH);


                String firstdate = year.toString() + "-" + month.toString() + "-" + day.toString();

                day++;

                if (!methods.isValidDate(month.toString() + "/" + day.toString() + "/" + year.toString())) {
                    if (month == 12) {
                        month = 1;
                        year++;

                    } else {
                        month++;
                    }
                    day = 1;

                }


                String seconddate = year.toString() + "-" + month.toString() + "-" + day.toString();
                day++;
                if (!methods.isValidDate(month.toString() + "/" + day.toString() + "/" + year.toString())) {
                    if (month == 12) {
                        month = 1;
                        year++;

                    } else {
                        month++;
                    }
                    day = 1;

                }
                String thirddate = year.toString() + "-" + month.toString() + "-" + day.toString();

                String naslov = "";
                if (isOnline()) {
                    if (date_number == 1) {

                        //tv.setText("");

                        naslov = server_name + "/" + hash_for_json_to_xml + "/index.php?datum=" + firstdate;
                        new HttpAsyncTask().execute(naslov);

                    } else if (date_number == 2) {
                        naslov = server_name + "/" + hash_for_json_to_xml + "/index.php?datum=" + seconddate;

                        new HttpAsyncTask().execute(naslov);

                    } else if (date_number == 3) {
                        naslov = server_name + "/" + hash_for_json_to_xml + "/index.php?datum=" + thirddate;
                        new HttpAsyncTaskForLasOne().execute(naslov);

                    }
                } else {
                    toogleRefreshing(false);
                    return;
                }
            } catch (Exception e) {
            }
        } else if (mToogleIntermediate) {

        } else {
            if (date_number == 1) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                WaitABit task = new WaitABit();
                task.execute("");
            }
            toogleRefreshing(false);
        }
        if (date_number == 1) {
            refresh(2);

        } else if (date_number == 2) {
            refresh(3);

        }

    }

    public static Boolean isXmlValid(String xml) {
        /*Funkcija preveri, če xml vsebuje vse kar bi moral.
        * To je pomembno, ker če dobimo kaj drugega (npr. html za prijavo v omrežje),
        * nesmemo prepisati zadnih resničnih podatkov.*/

        Boolean je = true;
        /*if(!xml.contains("<json>")){
            je = false;
        }
        if(!xml.contains("</json>")){
            je = false;
        }*/
        if (!xml.contains("<nadomescanja")) {
            je = false;
        }
        if (!xml.contains("<menjava_predmeta")) {
            je = false;
        }
        if (!xml.contains("<menjava_ur")) {
            je = false;
        }
        if (!xml.contains("<menjava_ucilnic")) {
            je = false;
        }
        if (!xml.contains("<rezerviranje_ucilnice")) {
            je = false;
        }
        if (!xml.contains("<vec_uciteljev_v_razredu")) {
            je = false;
        }
        if (!xml.contains("<seznam_manjkajocih_razredov")) {
            je = false;
        }
        if (!xml.contains("<datum>")) {
            je = false;
        }
        if (!xml.contains("</datum>")) {
            je = false;
        }
        return je;
    }

    public class UpdaterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            new HttpAsyncTaskForMinVersion().execute(server_name + "/" + hash_for_suplence + "/android/min_version.html");
        }
    }

    private class HttpAsyncTaskForMinVersion extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0], false);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String response) {

            if (isOnline()) {
                if (!response.equals(null) && !response.equals("Did not work!")) {
                    response = response.replaceAll("\n", "");
                    try {
                        Integer min_version = Integer.parseInt(response);
                        if (min_version > MyVersion) {
                            new AlertDialog.Builder(Main.this)
                                    .setTitle(getResources().getString(R.string.force_update_title))
                                    .setMessage(getResources().getString(R.string.force_update_text))
                                    .setCancelable(false)
                                    .setIcon(R.drawable.ic_launcher)
                                    .setPositiveButton(getResources().getString(R.string.install), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {

                                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.zigapk.gimvic.suplence"));
                                                    startActivity(i);
                                                    finish();

                                                }
                                            }
                                    )
                                    .

                                            setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            Context context = getApplicationContext();

                                                            //cancles all alarms
                                                            Calendar cal = Calendar.getInstance();
                                                            Intent intent2 = new Intent(Main.this, MyService.class);
                                                            PendingIntent pintent = PendingIntent.getService(Main.this, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
                                                            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                                            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10000, pintent);
                                                            alarm.cancel(pintent);

                                                            finishAffinity();
                                                        }
                                                    }
                                            )
                                    .

                                            show();

                        }
                    } catch (Exception e) {
                    }
                }
            }

        }
    }

    private class klicZaAnalitiko extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0], false);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String response) {

            working_with_files.writeToFile("opened.offline", "0", getApplicationContext(), 1);
        }
    }


}

