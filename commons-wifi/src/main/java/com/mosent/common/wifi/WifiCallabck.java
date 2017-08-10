package com.mosent.common.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;

import java.util.List;

/**
 * Created by lj on 2017/7/13.
 */

public abstract class WifiCallabck {

    public void onScanResultsAvailable(List<ScanResult> scanResults){

    }

    public void onConnected(WifiInfo wifiInfo){

    }

    public void onDisconnected(){

    }

}
