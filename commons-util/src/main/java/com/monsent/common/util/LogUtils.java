package com.monsent.common.util;

import android.util.Log;

/**
 * Created by lj on 2017/6/25.
 */

public class LogUtils {

    private final static boolean DEBUG = true;
    private final static String TAG_PREFIX = "Cleaner-";

    public static void i(String tag, String msg){
        if (DEBUG){
            Log.i(TAG_PREFIX + tag, msg);
        }
    }

    public static void e(String tag, String msg){
        if (DEBUG){
            Log.e(TAG_PREFIX + tag, msg);
        }
    }

    public static void d(String tag, String msg){
        if (DEBUG){
            Log.d(TAG_PREFIX + tag, msg);
        }
    }

}
