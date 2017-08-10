package com.mosent.common.wifip2p;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by lj on 2017/6/25.
 */

public class WifiDirectManager {

    private final static String TAG = WifiDirectManager.class.getSimpleName();

    public final static int STATE_SUCCESS = 0;
    public final static int STATE_FAILURE = 1;

    private Context context;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WifiDirectReceiver wifiDirectReceiver;
    private WifiDirectCallback wifiDirectCallback;
    private WifiP2pDevice connectedDevice;
    private boolean groupOwner;
    private WifiP2pClient wifiP2pClient;
    private WifiP2pServer wifiP2pServer;

    private WifiDirectManager(){

    }

    public WifiDirectManager(Context context) {
        this.context = context;
        wifiP2pManager = (WifiP2pManager)context.getSystemService(Context.WIFI_P2P_SERVICE);
        IntentFilter wifiDirectFilter = new IntentFilter();
        wifiDirectFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiDirectFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiDirectFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiDirectFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        wifiDirectReceiver = new WifiDirectReceiver(wifiDirectCallback);
        context.registerReceiver(wifiDirectReceiver, wifiDirectFilter);
    }

    public WifiDirectCallback getWifiDirectCallback() {
        return wifiDirectCallback;
    }

    public void setWifiDirectCallback(WifiDirectCallback wifiDirectCallback) {
        this.wifiDirectCallback = wifiDirectCallback;
        wifiDirectReceiver.setWifiDirectCallback(wifiDirectCallback);
    }

    public WifiP2pDevice getConnectedDevice() {
        return connectedDevice;
    }

    public void setConnectedDevice(WifiP2pDevice connectedDevice) {
        this.connectedDevice = connectedDevice;
    }

    public boolean isGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(boolean groupOwner) {
        this.groupOwner = groupOwner;
        if (groupOwner){
            wifiP2pServer = new WifiP2pServer();
        }else {
            wifiP2pClient = new WifiP2pClient();
        }
    }

    public WifiP2pClient getWifiP2pClient() {
        return wifiP2pClient;
    }

    public WifiP2pServer getWifiP2pServer() {
        return wifiP2pServer;
    }

    /**
     * 初始化
     * @return
     */
    public boolean initialize(){
        if (wifiP2pManager == null){
            return false;
        }
        try{
            channel = wifiP2pManager.initialize(context, context.getMainLooper(), null);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 扫描
     */
    public void discoverPeers(){
        if (wifiP2pManager == null || channel == null){
            return;
        }
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "discoverPeers onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                Log.i(TAG, "discoverPeers onFailure:" + reason);
            }
        });
    }

    /**
     * 停止扫描
     */
    public void stopPeerDiscovery(){
        if (wifiP2pManager == null || channel == null){
            return;
        }
        wifiP2pManager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    /**
     * 获取对等点列表
     */
    public void requestPeers(){
        if (wifiP2pManager == null || channel == null){
            return;
        }
        wifiP2pManager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                Log.i(TAG, "onPeersAvailable");
                if (wifiDirectCallback != null){
                    wifiDirectCallback.onPeersAvailable(peers);
                }
            }
        });
    }

    /**
     * 请求连接信息
     */
    public void requestConnectionInfo(){
        if (wifiP2pManager == null || channel == null){
            return;
        }
        wifiP2pManager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                Log.i(TAG, "onConnectionInfoAvailable");
                if (wifiDirectCallback != null){
                    wifiDirectCallback.onConnectionInfoAvailable(info);
                }
            }
        });
    }

    /**
     * 连接一个对等点
     * @param address
     */
    public void connect(final String address){
        if (wifiP2pManager == null || channel == null){
            return;
        }
        WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
        wifiP2pConfig.deviceAddress = address;
        wifiP2pManager.connect(channel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "Wifi onConnected");
                if (wifiDirectCallback != null){
                    wifiDirectCallback.onConnected(STATE_SUCCESS);
                }
            }

            @Override
            public void onFailure(int reason) {
                Log.i(TAG, "Wifi onConnected");
                if (wifiDirectCallback != null){
                    wifiDirectCallback.onConnected(STATE_FAILURE);
                }
            }
        });
    }

    /**
     * 取消连接
     */
    public void cancelConnect(){
        if (wifiP2pManager == null || channel == null){
            return;
        }
        wifiP2pManager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    /**
     * 创建组
     */
    public void createGroup(){
        if (wifiP2pManager == null || channel == null){
            return;
        }
        wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    /**
     * 移除组
     */
    public void removeGroup(){
        if (wifiP2pManager == null || channel == null){
            return;
        }
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    /**
     * 释放
     */
    public void close(){
        if (wifiP2pClient != null){
            wifiP2pClient.disconnect();
        }
        if (wifiP2pServer != null){
            wifiP2pServer.disconnect();
        }
        channel = null;
        if (wifiDirectReceiver != null) {
            context.unregisterReceiver(wifiDirectReceiver);
        }
    }

}
