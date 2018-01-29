package com.mustmobile.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by Tosh on 9/24/2015.
 */
public class AppStorage {

    public static final String APP_FOLDER = "/MeruUniversityApp";
    public static final String NO_MEDIA = ".nomedia";
    public static final String PASTPAPER_STORE = "/PastPapers";
    public static final String DOWNLOADS_STORE = "/Downloads";
    public static final String PROFILE = "/Profile";
    public static final String BOOKS_STORE = "/Books";

    private static String getPastPapersStore(){
        String pastPapersStore = null;
        File mFile = new File(Environment.getExternalStorageDirectory(),APP_FOLDER+PASTPAPER_STORE);
        if (!mFile.exists()){
            mFile.mkdirs();
            pastPapersStore = Environment.getExternalStorageDirectory()+APP_FOLDER+PASTPAPER_STORE;
        } else {
            pastPapersStore = Environment.getExternalStorageDirectory()+APP_FOLDER+PASTPAPER_STORE;
        }

        return pastPapersStore;
    }

    private static String getDownloadsStore(){
        String downloadsStore = null;
        File mFile = new File(Environment.getExternalStorageDirectory(),APP_FOLDER+DOWNLOADS_STORE);
        if (!mFile.exists()){
            mFile.mkdirs();
            downloadsStore = Environment.getExternalStorageDirectory()+APP_FOLDER+DOWNLOADS_STORE;
        } else {
            downloadsStore = Environment.getExternalStorageDirectory()+APP_FOLDER+DOWNLOADS_STORE;
        }

        return downloadsStore;
    }

    private static String getBooksStore(){
        String downloadsStore = null;
        File mFile = new File(Environment.getExternalStorageDirectory(),APP_FOLDER+ BOOKS_STORE);
        if (!mFile.exists()){
            mFile.mkdirs();
            downloadsStore = Environment.getExternalStorageDirectory()+APP_FOLDER+ BOOKS_STORE;
        } else {
            downloadsStore = Environment.getExternalStorageDirectory()+APP_FOLDER+ BOOKS_STORE;
        }

        return downloadsStore;
    }

    private static String getProfileStore(){
        String downloadsStore = null;
        File mFile = new File(Environment.getExternalStorageDirectory(),APP_FOLDER+PROFILE);
        if (!mFile.exists()){
            mFile.mkdirs();
            downloadsStore = Environment.getExternalStorageDirectory()+APP_FOLDER+PROFILE;
        } else {
            downloadsStore = Environment.getExternalStorageDirectory()+APP_FOLDER+PROFILE;
        }

        return downloadsStore;
    }

    public static String storeAs(String directory, String fileName){
        if (directory.equalsIgnoreCase(AppStorage.PASTPAPER_STORE)){
            return getPastPapersStore()+File.separator+fileName;
        } else if (directory.equalsIgnoreCase(AppStorage.DOWNLOADS_STORE)){
            return getDownloadsStore()+File.separator+fileName+".pdf";
        } else if(directory.equalsIgnoreCase(AppStorage.PROFILE)) {
            return getProfileStore()+File.separator+fileName;
        }
        else if(directory.equalsIgnoreCase(AppStorage.BOOKS_STORE)) {
            return getBooksStore()+File.separator+fileName+".pdf";
        }else {
            return "";
        }
    }

    public static String retrieve(String directory, String fileName){
        if (directory.equalsIgnoreCase(AppStorage.PASTPAPER_STORE)){
            return getPastPapersStore()+File.separator+fileName;
        } else if(directory.equalsIgnoreCase(AppStorage.DOWNLOADS_STORE)) {
            return getDownloadsStore()+File.separator+fileName+".pdf";
        }
        else if(directory.equalsIgnoreCase(AppStorage.BOOKS_STORE)) {
            return getBooksStore()+File.separator+fileName+".pdf";
        }
        else if(directory.equalsIgnoreCase(AppStorage.PROFILE)) {
            return getProfileStore()+File.separator+fileName;
        } else {
            return "";
        }
    }

    public static void delete(String directory, String fileName){
        if (externalIsReadable() && externalIsWritable()){
            File file = null;
            if(directory.equalsIgnoreCase(AppStorage.PROFILE)) {
                file = new File(getProfileStore()+File.separator+fileName);
            }

            if (file.exists()){
                file.delete();
            }
        }
    }

    public static boolean externalIsWritable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        } else {
            return false;
        }
    }

    public static boolean externalIsReadable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        } else {
            return false;
        }
    }
}
