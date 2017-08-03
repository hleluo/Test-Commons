package com.monsent.commons.ble;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Administrator on 2017/8/3.
 */

public abstract class BleCallback {

    /**
     * 开始扫描设备
     */
    public void onDiscoveryStarted(){

    }

    /**
     * 扫描设备
     * @param device
     */
    public void onDeviceDiscovered(BluetoothDevice device){

    }

    /**
     * 设备扫描完毕
     */
    public void onDiscoveryFinished(){

    }

    /**
     * 已连接成功
     * @param device
     */
    public void onConnected(BluetoothDevice device){

    }

    /**
     * 已断开连接
     * @param device
     */
    public void onDisconnected(BluetoothDevice device){

    }

    /**
     * 发现服务
     * @param device
     */
    public void onServicesDiscovered(BluetoothDevice device){

    }


    /**
     * 有数据可读
     * @param data
     */
    public void onDataArrived(byte[] data){

    }
}
