package com.mosent.common.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by lj on 2017/7/13.
 */

public class WifiScaner {

    public enum WifiCipherType{
        TYPE_WEP, TYPE_WPA_EAP, TYPE_WPA_PSK, TYPE_WPA2_PSK, TYPE_NOPASS
    }

    private Context context;
    private WifiManager wifiManager;
    private WifiCallabck wifiCallabck;

    public WifiScaner(Context context){
        this.context = context;
        wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public WifiCallabck getWifiCallabck() {
        return wifiCallabck;
    }

    public void setWifiCallabck(WifiCallabck wifiCallabck) {
        this.wifiCallabck = wifiCallabck;
    }

    /**
     * 初始化
     * @return
     */
    public boolean init(){
        if (wifiManager == null){
            return false;
        }
        if (!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(wifiReceiver, wifiFilter);
        return true;
    }

    /**
     * 获取当前已连接的Wifi信息
     * @return
     */
    public WifiInfo getConnectedWifi(){
        if (wifiManager == null){
            return null;
        }
        try {
            return wifiManager.getConnectionInfo();
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 扫描Wifi
     * @return
     */
    public boolean startScan(){
        return wifiManager == null ? false : wifiManager.startScan();
    }

    /**
     * 注销扫描监听
     * @return
     */
    public boolean stopScan(){
        context.unregisterReceiver(wifiReceiver);
        return true;
    }

    /**
     * 获取指定ssid的配置信息
     * @param ssid
     * @return
     */
    public WifiConfiguration getWifiConfiguration(String ssid){
        if (wifiManager == null || ssid == null){
            return null;
        }
        List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
            if (wifiConfiguration.SSID.toString().equals("\"" + ssid + "\"")) {
                return wifiConfiguration;
            }
        }
        return null;
    }

    /**
     * 指定ssid是否已配置
     * @param ssid
     * @return
     */
    public boolean isSsidConfigured(String ssid){
        return getWifiConfiguration(ssid) == null ? false : true;
    }

    /**
     * 获取为网络的加密方式
     * @param scanResult
     * @return
     */
    public WifiCipherType getCipherType(ScanResult scanResult){
        if (scanResult.capabilities.contains("WPA2-PSK")) {
            // WPA-PSK加密
            return WifiCipherType.TYPE_WPA2_PSK;
        } else if (scanResult.capabilities.contains("WPA-PSK")) {
            // WPA-PSK加密
            return WifiCipherType.TYPE_WPA_PSK;
        } else if (scanResult.capabilities.contains("WPA-EAP")) {
            // WPA-EAP加密
           return WifiCipherType.TYPE_WPA_EAP;
        } else if (scanResult.capabilities.contains("WEP")) {
            // WEP加密
            return WifiCipherType.TYPE_WEP;
        } else {
            // 无密码
            return WifiCipherType.TYPE_NOPASS;
        }
    }

    public WifiConfiguration createWifiConfiguration(String ssid, String password, WifiCipherType type){
        WifiConfiguration config = getWifiConfiguration(ssid);
        if (config != null) {
            // 本机之前配置过此wifi热点，直接移除
            wifiManager.removeNetwork(config.networkId);
        }
        config = new WifiConfiguration();
        /* 清除之前的连接信息 */
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        config.status = WifiConfiguration.Status.ENABLED;
        int priority;
        /*List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
        priority = getMaxPriority() + 1;
        if (priority > 99999) {
            priority = shiftPriorityAndSave();
        }
        config.priority = priority; // 2147483647;*/
        /* 各种加密方式判断 */
        if (type == WifiCipherType.TYPE_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WifiCipherType.TYPE_WEP) {
            config.preSharedKey = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WifiCipherType.TYPE_WPA_EAP) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN | WifiConfiguration.Protocol.WPA);
        } else if (type == WifiCipherType.TYPE_WPA_PSK) {
            config.preSharedKey = "\"" + password + "\"";
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN | WifiConfiguration.Protocol.WPA);
        } else if (type == WifiCipherType.TYPE_WPA2_PSK) {
            config.preSharedKey = "\"" + password + "\"";
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        } else {
            return null;
        }
        return config;
    }

    public boolean connect(String ssid, String password, WifiCipherType type){
        WifiInfo wifiInfo = getConnectedWifi();
        WifiConfiguration config = getWifiConfiguration(ssid);
        if (wifiInfo != null && config != null && wifiInfo.getNetworkId() == config.networkId){
            if (wifiCallabck != null){
                wifiCallabck.onConnected(wifiInfo);
            }
            return true;
        }
        config = createWifiConfiguration(ssid, password, type);
        if (config != null){
            int networkId = wifiManager.addNetwork(config);
            return wifiManager.enableNetwork(networkId, true);
        }
        return false;
    }

    public boolean disconnect(String ssid){
        if (wifiManager == null){
            return true;
        }
        WifiConfiguration config = getWifiConfiguration(ssid);
        if (config == null){
            return true;
        }
        try {
            wifiManager.disableNetwork(config.networkId);
            return wifiManager.disconnect();
        }catch (Exception e){
            return false;
        }
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)){
                List<ScanResult> scanResults = wifiManager.getScanResults();
                //根据信号强度从强到弱进行排序，冒泡排序
                for (int i = 0; i < scanResults.size() - 1; i++){
                    for (int j = 0; j < scanResults.size() - i -1; j++){
                        if (scanResults.get(j).level < scanResults.get(j + 1).level){
                            ScanResult temp = scanResults.get(j);
                            scanResults.set(j, scanResults.get(j + 1));
                            scanResults.set(j + 1, temp);
                        }
                    }
                }
                if (wifiCallabck != null){
                    wifiCallabck.onScanResultsAvailable(scanResults);
                }
            }else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.CONNECTED)){
                    if (wifiCallabck != null && wifiManager != null){
                        wifiCallabck.onConnected(wifiManager.getConnectionInfo());
                    }
                }else if (info.getState().equals(NetworkInfo.State.DISCONNECTED)){
                    if (wifiCallabck != null){
                        wifiCallabck.onDisconnected();
                    }
                }
            }
        }
    };

}
