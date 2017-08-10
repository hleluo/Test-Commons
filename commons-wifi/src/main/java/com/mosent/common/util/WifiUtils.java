package com.mosent.common.util;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by Administrator on 2017/8/10.
 */

public class WifiUtils {

    /**
     * WiFi是否可用
     * @param context
     * @return
     */
    public static boolean isEnabled(Context context) {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager == null ? false : wifiManager.isWifiEnabled();
    }

    /**
     * 设置WiFi
     * @param context
     * @param enabled
     * @return
     */
    public static boolean setEnabled(Context context, boolean enabled) {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null){
            return false;
        }else {
            if (enabled == wifiManager.isWifiEnabled()){
                return true;
            }else {
                return wifiManager.setWifiEnabled(enabled);
            }
        }
    }
}
