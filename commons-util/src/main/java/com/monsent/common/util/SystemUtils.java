package com.monsent.common.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;

import java.lang.reflect.Method;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/7/7.
 */

public class SystemUtils {

    /**
     * •设置系统的小时制
     * @param context
     * @param type 12 或 24
     */
    public static boolean setHourType(Context context, String type){
        try {
            return Settings.System.putString(context.getContentResolver(), Settings.System.TIME_12_24, type);
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 设置系统日期
     * @param context
     * @param calendar
     */
    public static boolean setTime(Context context, Calendar calendar){
        try {
            long when = calendar.getTimeInMillis();
            if (when / 1000 < Integer.MAX_VALUE) {
                ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
            }
            long now = Calendar.getInstance().getTimeInMillis();
            if (now - when > 1000){
                return false;
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 隐藏虚拟键盘
     * @param context
     * @return
     */
    public static boolean hideVirtualKey(Context context){
        try {
            View decorView = ((Activity) context).getWindow().getDecorView();
            //隐藏虚拟按键，并且全屏
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                decorView.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                int options = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(options);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 关屏
     * @param context
     * @return
     */
    public static boolean turnOfScreen(Context context){
        try{
            Class c = Class.forName("android.os.PowerManager");
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            for(Method m : c.getDeclaredMethods()){
                if(m.getName().equals("goToSleep")){
                    m.setAccessible(true);
                    if(m.getParameterTypes().length == 1){
                        m.invoke(powerManager, SystemClock.uptimeMillis());
                    }
                }
            }
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
