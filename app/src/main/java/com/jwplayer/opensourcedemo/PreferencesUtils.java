package com.jwplayer.opensourcedemo;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {

    public static final String IDENTITY = "identity";

    public static void save(String key, String value) {
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences(
                App.getInstance().getResources().getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        );
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static void save(String key, int value) {
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences(
                App.getInstance().getResources().getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        );
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static void save(String key, boolean value) {
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences(
                App.getInstance().getResources().getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        );
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static String getString(String key, String defaultValue) {
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences(
                App.getInstance().getResources().getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        );
        return sharedPreferences.getString(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences(
                App.getInstance().getResources().getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        );
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences(
                App.getInstance().getResources().getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        );
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void clear(String key) {
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences(
                App.getInstance().getResources().getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        );
        sharedPreferences.edit().remove(key).apply();
    }
}
