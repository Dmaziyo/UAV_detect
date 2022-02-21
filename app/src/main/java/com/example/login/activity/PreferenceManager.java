package com.example.login.activity;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PreferenceManager implements Runnable{
    public static List<String> login=null;
    private static Lock lock=new ReentrantLock();
    public static void saveCookiePreference(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences("ISLOGINED", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("SessionID", value);
        editor.apply();
        lock.unlock();
    }

    public static String getCookiePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("ISLOGINED", Context.MODE_PRIVATE);
        String s = "0";
        if (preferences.contains("SessionID")) {
            s = preferences.getString("SessionID", "");
        }
        return s;
    }

    public static void restartPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("ISLOGINED", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("SessionID", "0");
        editor.apply();
    }

    @Override
    public void run() {
        Scanner in =new Scanner(System.in);
    }
}
