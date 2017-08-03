package com.monsent.commons.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by lj on 2017/6/18.
 */

public class BluetoothReceiver extends BroadcastReceiver {

    private final static String TAG = BluetoothReceiver.class.getSimpleName();
    private BluetoothCallback bluetoothCallback;

    public void setBluetoothCallback(BluetoothCallback bluetoothCallback) {
        this.bluetoothCallback = bluetoothCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
            Log.i(TAG, "Bluetooth onDiscoveryStarted.");
            if (bluetoothCallback != null){
                bluetoothCallback.onDiscoveryStarted();
            }
        }else if (BluetoothDevice.ACTION_FOUND.equals(action)){
            Log.i(TAG, "Bluetooth onDeviceDiscovered.");
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (bluetoothCallback != null){
                bluetoothCallback.onDeviceDiscovered(device);
            }
        }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            Log.i(TAG, "Bluetooth onDiscoveryFinished.");
            if (bluetoothCallback != null){
                bluetoothCallback.onDiscoveryFinished();
            }
        }else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.i(TAG, "Bluetooth onBondStateChanged.");
            if (bluetoothCallback != null){
                bluetoothCallback.onBondStateChanged(device, device.getBondState());
            }
            if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                Log.i(TAG, "Bluetooth onDeviceBounded.");
                if (bluetoothCallback != null){
                    bluetoothCallback.onDeviceBonded(device);
                }
            }else if (device.getBondState() == BluetoothDevice.BOND_NONE){
                Log.i(TAG, "Bluetooth onDeviceUnbond.");
                if (bluetoothCallback != null){
                    bluetoothCallback.onDeviceUnbond(device);
                }
            }
        }else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.i(TAG, "Bluetooth onDevicePairingRequest.");
            if (bluetoothCallback != null){
                bluetoothCallback.onDevicePairingRequest(device, device.getBondState());
            }
        }
    }
}
