package com.bledemo;

import android.util.Log;

/**
 * Created by wesker on 2017/11/1510:05.
 */

public class LogUtils {
    final static String TAG = "wesker";
    public static void LogV(String str){
        Log.v(TAG,str);
    }
    public static void LogE(String str){
        Log.e(TAG,str);
    }
    public static void LogD(String str){
        Log.d(TAG,str);
    }
    public static void LogI(String str){
        Log.i(TAG,str);
    }
}
