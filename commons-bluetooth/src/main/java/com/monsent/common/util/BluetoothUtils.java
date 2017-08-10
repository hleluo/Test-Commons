package com.monsent.common.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lj on 2017/7/2.
 */

public class BluetoothUtils {

    private static BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    /**
     * 是否可用
     * @return
     */
    public static boolean isEnabled(){
        return adapter == null ? false : adapter.isEnabled();
    }

    /**
     * 设置是否可用
     * @param enabled
     * @return
     */
    public static boolean setEnabled(boolean enabled){
        if (adapter == null){
            return false;
        }
        if (enabled) {
            return adapter.isEnabled() ? true : adapter.enable();
        }else {
            return adapter.isEnabled() ? adapter.disable() : true;
        }
    }

    /**
     * 根据蓝牙地址获取蓝牙设备
     * @param address
     * @return
     */
    public static BluetoothDevice getByAddress(String address){
        try {
            return adapter == null ? null : adapter.getRemoteDevice(address);
        }catch (Exception e){
            return null;
        }
    }

    public static void sendFile(Context context, File file){
        PackageManager pm = context.getPackageManager();
        Intent intent = null;
        Map<String, ActivityInfo> map = null;
        try{
            intent = new Intent();
            map = new HashMap<>();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("*/*");
            List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
            for (ResolveInfo resolveInfo : resolveInfos) {
                String name = resolveInfo.activityInfo.processName;
                if (name.contains("bluetooth")){
                    map.put(name, resolveInfo.activityInfo);
                }
            }
            ActivityInfo activityInfo = map.get("com.android.bluetooth");
            activityInfo = activityInfo == null ? map.get("com.mediatek.bluetooth") : activityInfo;
            if (activityInfo == null){
                Iterator<ActivityInfo> iterator = map.values().iterator();
                if (iterator.hasNext()){
                    activityInfo = iterator.next();
                }
            }
            if (activityInfo != null){
                intent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
                ((Activity)context).startActivityForResult(intent, 4098);
            }
        }catch (Exception e){

        }
    }

}
