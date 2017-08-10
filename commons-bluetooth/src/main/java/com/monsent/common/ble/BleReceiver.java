package com.monsent.common.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by lj on 2017/6/18.
 */

public class BleReceiver extends BroadcastReceiver {

    private final static String TAG = BleReceiver.class.getSimpleName();
    private BleCallback bleCallback;

    public void setBleCallback(BleCallback bleCallback) {
        this.bleCallback = bleCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
            Log.i(TAG, "Ble onDiscoveryStarted.");
            if (bleCallback != null){
                bleCallback.onDiscoveryStarted();
            }
        }else if (BluetoothDevice.ACTION_FOUND.equals(action)){
            Log.i(TAG, "Ble onDeviceDiscovered.");
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (bleCallback != null){
                bleCallback.onDeviceDiscovered(device);
            }
        }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            Log.i(TAG, "Ble onDiscoveryFinished.");
            if (bleCallback != null){
                bleCallback.onDiscoveryFinished();
            }
        }
    }
}
