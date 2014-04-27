package com.zigapk.gimvic.suplence;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MyService extends Service {

    public static Integer mId = 1;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TO DO SOMETHING USEFULL!!!!

        refresh("", 1);
        return Service.START_NOT_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Boolean isXmlValid(String xml){
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
        if(!xml.contains("<nadomescanja")){
            je = false;
        }
        if(!xml.contains("<menjava_predmeta")){
            je = false;
        }
        if(!xml.contains("<menjava_ur")){
            je = false;
        }
        if(!xml.contains("<menjava_ucilnic")){
            je = false;
        }
        if(!xml.contains("<rezerviranje_ucilnice")){
            je = false;
        }
        if(!xml.contains("<vec_uciteljev_v_razredu")){
            je = false;
        }
        if(!xml.contains("<seznam_manjkajocih_razredov")){
            je = false;
        }
        if(!xml.contains("<datum>")){
            je = false;
        }
        if(!xml.contains("</datum>")){
            je = false;
        }
        return je;
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


    public void refresh(String last_date, Integer date_number){
        //last_date bo potem nepotreben

        Context context = getApplicationContext();


        if(isOnline()){
            try {

                //NA KONCU MORA TUKAJ BITI PREGLED ZA 3 DNI NAPREJ

                Calendar cal = Calendar.getInstance();


                Integer year = cal.get(Calendar.YEAR);
                Integer month = cal.get(Calendar.MONTH);
                month ++;
                Integer day = cal.get(Calendar.DAY_OF_MONTH);



                String firstdate = year.toString() + "-" + month.toString() + "-" + day.toString();

                day++;

                if(!isValidDate(month.toString() + "/" + day.toString() + "/" + year.toString())){
                    if(month==12){
                        month = 1;
                        year++;

                    }else{
                        month ++;
                    }
                    day = 1;

                }


                String seconddate = year.toString() + "-" + month.toString() + "-" + day.toString();
                day++;
                if(!isValidDate(month.toString() + "/" + day.toString() + "/" + year.toString())){
                    if(month==12){
                        month = 1;
                        year++;

                    }else{
                        month ++;
                    }
                    day = 1;

                }
                String thirddate = year.toString() + "-" + month.toString() + "-" + day.toString();

                String naslov = "";
                String naslov2 = "http://app.gimvic.org/d0941e68da8f38151ff86a61fc59f7c5cf9fcaa2/data/tags.xml";

                if(date_number==1){

                    //tv.setText("");

                    naslov = Main.server_name + "/" + Main.hash_for_json_to_xml + "/index.php?datum=" + firstdate;
                    new HttpAsyncTask().execute(naslov);
                    new HttpAsyncTaskForTags().execute(naslov2);

                }else if(date_number==2){
                    naslov = Main.server_name + "/" + Main.hash_for_json_to_xml + "/index.php?datum=" + seconddate;
                    new HttpAsyncTask().execute(naslov);

                }else if(date_number==3){
                    naslov = Main.server_name + "/" + Main.hash_for_json_to_xml + "/index.php?datum=" + thirddate;
                    new HttpAsyncTask().execute(naslov);

                }else{

                }

            }catch (Exception e){}


        }

        if(date_number==1){
            refresh("", 2);

        }else if(date_number==2){
            refresh("", 3);

        }

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

    private class HttpAsyncTaskForTags extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {


            return GET(urls[0], false);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            writeToFile("tags.xml", result, getApplicationContext(), 1);
        }
    }

    private static boolean isValidDate(String input) {
        String formatString = "MM/dd/yyyy";

        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString);
            format.setLenient(false);
            format.parse(input);
        } catch (ParseException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
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

    public static boolean appendFileValue(String fileName, String value,
                                          Context context) {
        return writeToFile(fileName, value, context, Context.MODE_APPEND);
    }

    public static boolean setFileValue(String fileName, String value,
                                       Context context) {
        return writeToFile(fileName, value, context,
                Context.MODE_WORLD_READABLE);
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

    public static void deleteFile(String fileName, Context context) {
        context.deleteFile(fileName);
    }


    public String GET(String url, Boolean WriteToFiles){
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
                result = "null";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }


        if(WriteToFiles){

            Context context = getApplicationContext();

            //sets filename to date (finds it from url - date must be at the end)
            String whattoreplace = url.substring(0, url.indexOf("?"));
            url = url.replace(whattoreplace, "");
            url = url.replaceAll("\\?", "");
            url = url.replaceAll("datum=", "");
            String filename = url + ".xml";

            if(isXmlValid(result)){
                writeToFile(filename, result, context, 1);
            }
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
}
