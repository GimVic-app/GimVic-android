package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by ziga on 10/21/14.
 */
public class Files {

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

    public static boolean writeToFile(String fileName, String value,
                                      Context context) {

        try {
            /*
             * We have to use the openFileOutput()-method the ActivityContext
             * provides, to protect your file from others and This is done for
             * security-reasons. We chose MODE_WORLD_READABLE, because we have
             * nothing to hide in our file
             */
            FileOutputStream fOut = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
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

    //Checks if external storage is available for read and write
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static void writeToExternalStorage(String fileName, String content){
        File file;
        FileOutputStream outputStream;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(fileName), "");

            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFileFromExternalStorage(String fileName){
        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(fileName), "");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }

            return text.toString();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            return null;
        }
    }

    public static Bitmap loadBitmap(String name, Context context){
        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(name);
            b = BitmapFactory.decodeStream(fis);
            fis.close();
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        return b;
    }

    public static void saveBitmap(Bitmap b, String name, Context context){
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
    }
}
