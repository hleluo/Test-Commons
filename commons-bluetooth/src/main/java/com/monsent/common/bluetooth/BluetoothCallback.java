package com.monsent.common.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.io.InputStream;

/**
 * Created by lj on 2017/6/25.
 */

public abstract class BluetoothCallback {

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
     * 设备配对状态改变
     * @param device
     */
    public void onBondStateChanged(BluetoothDevice device, int state){

    }

    /**
     * 设备未配对
     * @param device
     */
    public void onDeviceUnbond(BluetoothDevice device){

    }

    /**
     * 设备配对成功
     * @param device
     */
    public void onDeviceBonded(BluetoothDevice device){

    }

    /**
     * 配对请求
     * @param device
     * @param state
     */
    public void onDevicePairingRequest(BluetoothDevice device, int state){
        /*bluetoothClient.setPairingConfirmation(device, true);
        bluetoothClient.createBond(device);
        bluetoothClient.setPin(device, "0000"); //1234
        bluetoothClient.cancelPairingUserInput(device);*/
    }

    /**
     * 连接设备
     * @param device
     * @param state
     */
    public void onConnected(BluetoothDevice device, int state){

    }

    /**
     * 断开连接成功
     */
    public void onDisconnected(){

    }

    /**
     * 接收到数据
     * @param inputStream
     */
    public void onDataArrived(InputStream inputStream){

    }
}
