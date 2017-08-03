package com.monsent.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * Created by lj on 2017/6/30.
 */

public class NetworkUtils {

    private static NetworkInfo getActiveNetworkInfo(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null){
            return null;
        }
        return manager.getActiveNetworkInfo();
    }

    /**
     * 网络是否可用
     * @param context
     * @return
     */
    public static boolean isAvailable(Context context){
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo == null ? false : networkInfo.isAvailable();
    }

    /**
     * 网络是否连接
     * @param context
     * @return
     */
    public static boolean isConnected(Context context){
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo == null ? false : networkInfo.isConnected();
    }

    /**
     * WiFi是否连接
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return isConnected(context) && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 移动网络是否连接
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return isConnected(context) && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

}
