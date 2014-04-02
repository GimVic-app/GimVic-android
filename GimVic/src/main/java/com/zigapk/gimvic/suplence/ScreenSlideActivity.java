package com.zigapk.gimvic.suplence;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class ScreenSlideActivity extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide1);

        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(false);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOffscreenPageLimit(5);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.screen_slide, menu);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item1 = menu.add(Menu.NONE, R.id.action_previous, Menu.NONE, R.string.back);
        item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.findItem(R.id.action_previous).setEnabled(mViewPager.getCurrentItem() > 0);
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mViewPager.getCurrentItem() == 1)
                        ? R.string.ok
                        : R.string.next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                if(mViewPager.getCurrentItem() == 4){

                    /* KODA ZA TAKRAT KO BO EMSO
                    TextView tv = (TextView) findViewById(R.id.textView2);
                    TextView preverjam = (TextView) findViewById(R.id.preverjam);
                    EditText emso_input = (EditText) findViewById(R.id.emso_input);

                    //5 mora na koncu bit 13
                    if(emso_input.length()<4){
                        tv.setText("Vnesite celoten EMŠO.");
                    }else if(isOnline()){
                        try {
                            preverjam.setText("Preverjam ...");
                            String naslov = "http://193.77.135.107/json/emso_exists.php?emso=" + emso_input.getText().toString();
                            new HttpAsyncTask().execute(naslov);

                        }catch (Exception e){}
                    }else{
                        tv.setText("Ni internetne povezave.");
                    }*/

                    EditText filtri = (EditText) findViewById(R.id.filtri);
                    Intent intent = new Intent(ScreenSlideActivity.this, Main.class);
                    startActivity(intent);
                    writeToFile("first.time", "true", getApplicationContext(), 1);
                    writeToFile("opened.offline", "0", getApplicationContext(), 1);

                    String string_filtri = "";
                    string_filtri = filtri.getText().toString();
                    if(string_filtri.length()>0){
                        writeToFile("filtri.value", string_filtri, getApplicationContext(), 1);
                    }

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ScreenSlideActivity.this);

                    if(prefs.getBoolean("enable_sync", true)){
                        //NASTAVI ALARM, KI VSAKIH X SEKUND POŽENE BACKGROUND SERVICE (pol ure)
                        Calendar cal = Calendar.getInstance();
                        Intent intent2 = new Intent(ScreenSlideActivity.this, MyService.class);
                        PendingIntent pintent = PendingIntent.getService(ScreenSlideActivity.this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1800000, pintent);
                    }

                    this.finish();
                }
                else{

                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                    return true;
                }

        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            int about = R.layout.fragment_screen_slide_page_1;
            int page = getArguments().getInt(ARG_SECTION_NUMBER);

            if(page==1){
                about = R.layout.fragment_screen_slide_page_1;
            }else if(page==2){
                about = R.layout.fragment_screen_slide_page_2;
            }else if(page==3){
                about = R.layout.fragment_screen_slide_page_3;
            }else if(page==4){
                about = R.layout.fragment_screen_slide_page_4;
            }else if(page==5){
                about = R.layout.fragment_screen_slide_page_5;
            }

            ViewGroup rootView = (ViewGroup) inflater.inflate(about, container, false);

            // Set the title view to show the page number.

		/*if(mPageNumber==0)
        ((TextView) rootView.findViewById(android.R.id.text1)).setText("GimVic - suplence");*/

            return rootView;
        }
    }

    public boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if(prefs.getBoolean("wifi_only", false)){
                if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()){
                    return false;
                }else return true;
            }
            return true;
        }
        return false;
    }

    public static String GET(String url){
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
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    /* KODA ZA TAKRAT KO BO EMSO

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            TextView tv = (TextView) findViewById(R.id.textView2);
            TextView preverjam = (TextView) findViewById(R.id.preverjam);
            EditText emso_input = (EditText) findViewById(R.id.emso_input);
            Context context = getApplicationContext();

            preverjam.setText("");
            if(result.toLowerCase().equals("true")){
                Toast.makeText(getBaseContext(), "EMŠO je pravi", Toast.LENGTH_LONG).show();
                writeToFile("emso.value", emso_input.getText().toString(), context, 1);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ScreenSlideActivity.this);

                if(prefs.getBoolean("enable_sync", true)){
                    //NASTAVI ALARM, KI VSAKIH X SEKUND POŽENE BACKGROUND SERVICE (pol ure)
                    Calendar cal = Calendar.getInstance();
                    Intent intent = new Intent(ScreenSlideActivity.this, MyService.class);
                    PendingIntent pintent = PendingIntent.getService(ScreenSlideActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1800000, pintent);
                }

                finish();
                //new MainActivity.fromscreenslide();
            }else{
                tv.setText(R.string.ni_emsota);
            }
        }
    }*/

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

}
