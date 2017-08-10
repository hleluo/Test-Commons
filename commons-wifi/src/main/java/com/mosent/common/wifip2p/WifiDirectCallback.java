package com.mosent.common.wifip2p;


import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;

/**
 * Created by lj on 2017/6/25.
 */

public abstract class WifiDirectCallback {

    /**
     * Wifi P2p 开启
     */
    public void onStateEnabled(){

    }

    /**
     * Wifi P2p关闭
     */
    public void onStateDisabled(){

    }

    /**
     * 对等点列表已经改变
     */
    public void onPeersChanged(){

    }

    /**
     * 扫描到对等点列表
     * @param peers
     */
    public void onPeersAvailable(WifiP2pDeviceList peers){

    }

    /**
     * p2p连接成功
     */
    public void onConnected(NetworkInfo networkInfo, WifiP2pDevice wifiP2pDevice){

    }

    /**
     * p2p连接
     * @param state
     */
    public void onConnected(int state){

    }

    /**
     * 连接可用
     * @param info
     */
    public void onConnectionInfoAvailable(WifiP2pInfo info){

    }

    /**
     * p2p连接设备状态改变
     * @param device
     */
    public void onThisDeviceChanged(WifiP2pDevice device){

    }

}
