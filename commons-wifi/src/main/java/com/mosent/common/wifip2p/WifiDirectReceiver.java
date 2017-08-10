package com.mosent.common.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by lj on 2017/6/25.
 */

public class WifiDirectReceiver extends BroadcastReceiver{

    private final static String TAG = WifiDirectReceiver.class.getSimpleName();

    private WifiDirectCallback wifiDirectCallback;

    public WifiDirectReceiver(WifiDirectCallback wifiDirectCallback) {
        this.wifiDirectCallback = wifiDirectCallback;
    }

    public WifiDirectCallback getWifiDirectCallback() {
        return wifiDirectCallback;
    }

    public void setWifiDirectCallback(WifiDirectCallback wifiDirectCallback) {
        this.wifiDirectCallback = wifiDirectCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                //打开
                Log.i(TAG, "Wifi p2p onStateEnabled.");
                if (wifiDirectCallback != null){
                    wifiDirectCallback.onStateEnabled();
                }
            }else if(state == WifiP2pManager.WIFI_P2P_STATE_DISABLED){
                //关闭
                Log.i(TAG, "Wifi p2p onStateDisabled.");
                if (wifiDirectCallback != null){
                    wifiDirectCallback.onStateDisabled();
                }
            }
        }else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            Log.i(TAG, "Wifi p2p onPeersChanged.");
            if (wifiDirectCallback != null){
                wifiDirectCallback.onPeersChanged();
            }
        }else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            //连接状态改变
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pDevice device = (WifiP2pDevice)intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            if (networkInfo.isConnected()){
                Log.i(TAG, "Wifi p2p onConnected.");
                if (wifiDirectCallback != null){
                    wifiDirectCallback.onConnected(networkInfo, device);
                }
            }
        }else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
            //当前设备状态改变
            WifiP2pDevice device = (WifiP2pDevice)intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            Log.i(TAG, "Wifi p2p onThisDeviceChanged.");
            if (wifiDirectCallback != null){
                wifiDirectCallback.onThisDeviceChanged(device);
            }
        }
    }
}
