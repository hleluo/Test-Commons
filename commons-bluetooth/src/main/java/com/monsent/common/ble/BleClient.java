package com.monsent.common.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by lj on 2017/6/25.
 */

public class BleClient {

    private final static String TAG = BleClient.class.getSimpleName();
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private String conntecedAddress = null;
    private BleReceiver bleReceiver;
    private BleCallback bleCallback;

    //Gatt回调
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i(TAG, "Ble onConnectionStateChange: " + status + " -> " + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED){  //已连接
                if (bleCallback != null){
                    bleCallback.onConnected(gatt.getDevice());
                }
                bluetoothGatt.discoverServices();
            }else if (newState == BluetoothProfile.STATE_DISCONNECTED){ //已断开连接
                if (bleCallback != null){
                    bleCallback.onDisconnected(gatt.getDevice());
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "Ble onServicesDiscovered: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (bleCallback != null) {
                    bleCallback.onServicesDiscovered(gatt.getDevice());
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "Ble onCharacteristicRead: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS){
                if (bleCallback != null){
                    bleCallback.onDataArrived(characteristic.getValue());
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "Ble onCharacteristicChanged.");
            if (bleCallback != null){
                bleCallback.onDataArrived(characteristic.getValue());
            }
        }
    };

    private BleClient() {

    }

    public BleClient(Context context) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //注册蓝牙扫描广播
        IntentFilter bleFilter = new IntentFilter();
        bleFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bleFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bleFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        bleReceiver = new BleReceiver();
        context.registerReceiver(bleReceiver, bleFilter);
    }

    public void setBleCallback(BleCallback bleCallback) {
        this.bleCallback = bleCallback;
        this.bleReceiver.setBleCallback(bleCallback);
    }

    /**
     * 初始化蓝牙开关
     * @return
     */
    public boolean initialize(){
        if (bluetoothAdapter == null){
            return false;
        }
        if (bluetoothAdapter.isEnabled()){
            return true;
        }
        return bluetoothAdapter.enable();
    }

    /**
     * 扫描设备
     */
    public void startDiscovery(){
        if (bluetoothAdapter == null){
            return;
        }
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    /**
     * 取消扫描
     */
    public void cancelDiscovery(){
        if (bluetoothAdapter == null){
            return;
        }
        bluetoothAdapter.cancelDiscovery();
    }

    /**
     * 连接设备
     * @param address
     * @return
     */
    public boolean connect(final String address){
        if (bluetoothAdapter == null || address == null){
            return false;
        }
        //如果地址等于当前已连接的地址
        if (address.equals(conntecedAddress) && bluetoothGatt != null && bluetoothGatt.connect()){
            return true;
        }
        final BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
        if (bluetoothDevice == null){
            return false;
        }
        bluetoothGatt = bluetoothDevice.connectGatt(context, false, gattCallback);
        conntecedAddress = address;
        return true;
    }

    /**
     * 断开连接
     */
    public void disconnect(){
        if (bluetoothGatt != null){
            bluetoothGatt.disconnect();
            bluetoothGatt = null;
        }
        conntecedAddress = null;
    }

    /**
     * 释放连接
     */
    public void close(){
        if (bluetoothGatt != null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        conntecedAddress = null;
        context.unregisterReceiver(bleReceiver);
    }

    public String getConntecedAddress(){
        return this.conntecedAddress;
    }

    /**
     * 写数据
     * @param serviceUuid
     * @param writeUuid
     * @param value
     * @return
     */
    public boolean write(String serviceUuid, String writeUuid, byte[] value){
        if (bluetoothGatt == null){
            return false;
        }
        try{
            BluetoothGattService gattService = bluetoothGatt.getService(UUID.fromString(serviceUuid));
            BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(writeUuid));
            gattCharacteristic.setValue(value);
            return bluetoothGatt.writeCharacteristic(gattCharacteristic);
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 设置当指定Characteristic值变化时，发出通知
     * @param serviceUuid
     * @param readUuid
     */
    public void enableCharacteristicNotification(String serviceUuid, String readUuid) {
        if (bluetoothGatt == null){
            return;
        }
        try {
            BluetoothGattService gattService = bluetoothGatt.getService(UUID.fromString(serviceUuid));
            BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(readUuid));
            boolean enabled = bluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
            if (enabled) {
                List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
                if (gattDescriptors != null && gattDescriptors.size() > 0) {
                    for (BluetoothGattDescriptor gattDescriptor: gattDescriptors) {
                        gattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        bluetoothGatt.writeDescriptor(gattDescriptor);
                    }
                }
            }
        }catch (Exception e){
        }
    }

    /**
     * 发起获取数据请求
     * @param serviceUuid
     * @param readUuid
     */
    public void readCharacteristic(String serviceUuid, String readUuid) {
        if (bluetoothGatt == null) {
            return;
        }
        try {
            BluetoothGattService gattService = bluetoothGatt.getService(UUID.fromString(serviceUuid));
            BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(readUuid));
            bluetoothGatt.readCharacteristic(gattCharacteristic);
        }catch (Exception e){
        }
    }
}
