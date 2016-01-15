package com.afilimonov.gitbrowser.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Aliaksandr_Filimonau on 2016-01-15.
 * shared preferences helper class
 */
public class Preferences {

    public static boolean putString(String name, String value, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putString(name, value).commit();
    }

    public static String getString(String name, String defValue, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(name, defValue);
    }

    public static boolean remove(String name, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().remove(name).commit();
    }
}
