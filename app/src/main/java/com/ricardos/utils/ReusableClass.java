package com.ricardos.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class ReusableClass {


    public static Typeface getFontGillSansMTProStyle(Context c) {
        return Typeface.createFromAsset(c.getAssets(), "fonts/GillSansMTPro_UltraBold.ttf");
    }

    public static Typeface getFontGenericaBoldStyle(Context c) {
        return Typeface.createFromAsset(c.getAssets(), "fonts/Generica Bold.ttf");
    }

    //===================================================================================================================================
    //check Mobile data and wifi
    //===================================================================================================================================
    public static boolean haveNetworkConnection(Activity myActivity) {
        ConnectivityManager cm = (ConnectivityManager) myActivity.getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    //====================================================================================================================================
    //checking Mobile data and wifi END
    //====================================================================================================================================


    //===================================================================================================================================
    //Preference variable
    //===================================================================================================================================

    //--------------------------------------------
    // method to save variable in preference
    //--------------------------------------------
    public static void saveInPreference(String name, String content, Context myActivity) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(myActivity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, content);
        editor.commit();
    }

    //--------------------------------------------
    // getting content from preferences
    //--------------------------------------------
    public static String getFromPreference(String variable_name, Context myActivity) {
        String preference_return;
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(myActivity);
        preference_return = preferences.getString(variable_name, "");

        return preference_return;
    }


    //===================================================================================================================================
    //Preference variable
    //===================================================================================================================================
}
