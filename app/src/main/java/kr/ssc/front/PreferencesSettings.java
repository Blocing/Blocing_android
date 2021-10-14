package kr.ssc.front;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesSettings {

    private static final String PREF_FILE = "settings_pref";

    public static void saveToPref(Context context, String str){
        final SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("code",str);
        editor.apply();
    }
    public static void deletePref(Context context) {
        final SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        editor.remove("code");
        editor.apply();
    }
    public static String getCode(Context context){
        final SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        final String defaultValue="";
        return sharedPref.getString("code",defaultValue);
    }
}
