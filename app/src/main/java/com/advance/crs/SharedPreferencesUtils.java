package com.advance.crs;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    private static final String PREF_EMAIL = "email";

    public static String getStoredEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_EMAIL, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_EMAIL, "");
    }

    public static void saveEmail(Context context, String email) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_EMAIL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_EMAIL, email);
        editor.apply();
    }
    public static void deleteStoredEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_EMAIL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREF_EMAIL);
        editor.apply();
    }
}
