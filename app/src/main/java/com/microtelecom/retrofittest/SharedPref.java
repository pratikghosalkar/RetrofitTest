package com.microtelecom.retrofittest;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pratik on 28-10-2016.
 */

public class SharedPref {
    private Context context;

    private String PREF_NAME = "Wall";
    private String PREF_NAME_CONNECT_MODE = "ConnectMode";

    private String wallpaper_change_date = "wallpaperChangeDate";

    public SharedPref(Context con) {
        this.context = con;
    }

    //<editor-fold desc="General Preference">
//    public void saveAppConnectMode(String mode) {
//        SharedPreferences mPrefs = context.getSharedPreferences(PREF_NAME_CONNECT_MODE, 0);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        prefsEditor.putString("mode", mode);
//        prefsEditor.apply();
//    }

//    public String getAppConnectMode() {
//        SharedPreferences mPrefs = context.getSharedPreferences(PREF_NAME_CONNECT_MODE, 0);
//        String mode = mPrefs.getString("mode", "");
//        return mode;
//    }


    public void saveDate(String date) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(wallpaper_change_date, date);
        prefsEditor.apply();
    }

    public String getDate() {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_NAME, 0);
        String ip = mPrefs.getString(wallpaper_change_date, "");
        return ip;
    }
}
