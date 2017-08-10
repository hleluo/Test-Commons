package com.monsent.common.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

/**
 * Created by lj on 2017/6/25.
 */

public class BluetoothClient {

    public final static int STATE_SUCCESS = 0;
    public final static int STATE_FAILURE = 1;

    private final static String TAG = BluetoothClient.class.getSimpleName();

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private String conntecedAddress = null;
    private BluetoothSocket bluetoothSocket;
    private BluetoothReceiver bluetoothReceiver;
    private BluetoothCallback bluetoothCallback;
    private boolean readAvailable = true;
    private ReadThread readThread;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    private BluetoothClient() {

    }

    public BluetoothClient(Context context) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //注册蓝牙扫描广播
        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        bluetoothReceiver = new BluetoothReceiver();
        context.registerReceiver(bluetoothReceiver, bluetoothFilter);
    }

    public BluetoothCallback getBluetoothCallback() {
        return bluetoothCallback;
    }

    public void setBluetoothCallback(BluetoothCallback bluetoothCallback) {
        this.bluetoothCallback = bluetoothCallback;
        bluetoothReceiver.setBluetoothCallback(bluetoothCallback);
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
     * 取消扫描设备
     */
    public void cancelDiscovery(){
        if (bluetoothAdapter == null){
            return;
        }
        bluetoothAdapter.cancelDiscovery();
    }

    /**
     * 配对设备
     * @param device
     * @return
     */
    public boolean createBond(BluetoothDevice device){
        if (device == null){
            return false;
        }
        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            return (Boolean) createBondMethod.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解除配对
     * @param device
     * @return
     */
    public boolean removeBond(BluetoothDevice device){
        if (device == null){
            return false;
        }
        try {
            Method removeBondMethod = BluetoothDevice.class.getMethod("removeBond");
            return (Boolean) removeBondMethod.invoke(device);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取消配对
     * @param device
     * @return
     */
    public boolean cancelBondProcess(BluetoothDevice device){
        if (device == null){
            return false;
        }
        try {
            Method cancelBondProcessMethod = BluetoothDevice.class.getMethod("cancelBondProcess");
            return (Boolean) cancelBondProcessMethod.invoke(device);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置PIN码
     * @param device
     * @param pin
     * @return
     */
    public boolean setPin(BluetoothDevice device, String pin){
        if (device == null){
            return false;
        }
        try {
            Method setPinMethod = BluetoothDevice.class.getDeclaredMethod("setPin", new Class[]{byte[].class});
            return (Boolean) setPinMethod.invoke(device, new Object[]{pin.getBytes()});
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取消用户输入
     * @param device
     * @return
     */
    public boolean cancelPairingUserInput(BluetoothDevice device){
        if (device == null){
            return false;
        }
        try {
            Method cancelPairingUserInputMethod = BluetoothDevice.class.getMethod("cancelPairingUserInput");
            return (Boolean) cancelPairingUserInputMethod.invoke(device);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 确认配对
     * @param device
     * @param isConfirm
     * @return
     */
    public boolean setPairingConfirmation(BluetoothDevice device, boolean isConfirm){
        try {
            Method setPairingConfirmationMethod = BluetoothDevice.class.getDeclaredMethod("setPairingConfirmation", boolean.class);
            return (Boolean) setPairingConfirmationMethod.invoke(device, isConfirm);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取已绑定的设备
     * @return
     */
    public Set<BluetoothDevice> getBondedDevices(){
        if (bluetoothAdapter != null){
            return bluetoothAdapter.getBondedDevices();
        }
        return null;
    }

    /**
     * 解除所有配对的设备
     */
    public void removeAllBondedDevices() {
        if (bluetoothAdapter == null) {
            return;
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            removeBond(device);
        }
    }

    private Handler handlerConnect = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case STATE_SUCCESS:
                    if (bluetoothCallback != null){
                        bluetoothCallback.onConnected((BluetoothDevice) msg.obj, STATE_SUCCESS);
                    }
                    break;
                case STATE_FAILURE:
                    if (bluetoothCallback != null){
                        bluetoothCallback.onConnected((BluetoothDevice) msg.obj, STATE_FAILURE);
                    }
                    break;
            }
        }
    };

    //连接线程
    private class ConnectThread extends Thread{

        private String address;
        private String uuid;

        public ConnectThread(String address, String uuid){
            this.address = address;
            this.uuid = uuid;
        }

        @Override
        public void run() {
            if (bluetoothAdapter == null){
                return;
            }
            BluetoothDevice bluetoothDevice = null;
            try {
                bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
                //创建一个Socket连接：只需要服务器在注册时的UUID号
                final int sdk = Build.VERSION.SDK_INT;
                if (sdk >= 10){
                    bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid));
                }else {
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
                }
                bluetoothSocket.connect();
                conntecedAddress = address;
                Log.i(TAG, "Bluetooth onConnected");
                if (bluetoothCallback != null){
                    //启动读数据
                    if (readThread == null) {
                        readThread = new ReadThread();
                    }
                    readThread.start();
                    if (handlerConnect != null) {
                        Message message = handlerConnect.obtainMessage();
                        message.what = STATE_SUCCESS;
                        message.obj = bluetoothDevice;
                        handlerConnect.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (bluetoothCallback != null && handlerConnect != null){
                    Message message = handlerConnect.obtainMessage();
                    message.what = STATE_FAILURE;
                    message.obj = bluetoothDevice;
                    handlerConnect.sendMessage(message);
                }
            }
        }
    }

    //读线程
    private class ReadThread extends Thread{

        @Override
        public void run() {
            try{
                inputStream = bluetoothSocket.getInputStream();
                while (readAvailable) {
                    if (inputStream != null && bluetoothCallback != null) {
                        bluetoothCallback.onDataArrived(inputStream);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (inputStream != null){
                    try{
                        inputStream.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * 是否连接
     * @return
     */
    public boolean isConnected(){
        return bluetoothSocket == null ? false : bluetoothSocket.isConnected();
    }

    /**
     * 连接设备
     * @param address
     * @param uuid
     */
    public void connect(String address, String uuid) {
        if (bluetoothAdapter == null || address == null || uuid == null) {
            return;
        }
        readAvailable = true;
        //如果地址等于当前已连接的地址
        if (address.equals(conntecedAddress) && bluetoothSocket != null && bluetoothSocket.isConnected()) {
            Log.i(TAG, "Bluetooth onConnected");
            if (bluetoothCallback != null){
                if (readThread == null) {
                    readThread = new ReadThread();
                    readThread.start();
                }
                bluetoothCallback.onConnected(bluetoothAdapter.getRemoteDevice(address), STATE_SUCCESS);
            }
            return;
        }
        ConnectThread connectThread = new ConnectThread(address, uuid);
        connectThread.start();
    }

    /**
     * 关闭资源
     */
    private void dispose(){
        if (outputStream != null){
            try{
                outputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (inputStream != null){
            try{
                inputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (bluetoothSocket != null){
            try {
                bluetoothSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        readAvailable = false;
        if (readThread != null) {
            try {
                readThread.interrupt();
            }catch (Exception e){
                e.printStackTrace();
            }
            readThread = null;
        }
        conntecedAddress = null;
    }

    /**
     * 断开连接
     */
    public void disconnect(){
        dispose();
        if (bluetoothCallback != null){
            bluetoothCallback.onDisconnected();
        }
    }

    /**
     * 释放连接
     */
    public void close(){
        dispose();
        if (context != null && bluetoothReceiver != null) {
            context.unregisterReceiver(bluetoothReceiver);
        }
    }

    /**
     * 写数据
     * @param data
     */
    public boolean write(String data){
        return data == null ? false : write(data.getBytes());
    }

    /**
     * 写数据
     * @param data
     */
    public boolean write(byte[] data){
        return data == null ? false : write(data, 0 , data.length);
    }

    /**
     * 写数据
     * @param data
     * @param off
     * @param len
     */
    public boolean write(byte[] data, int off, int len){
        if (bluetoothSocket == null || data == null || data.length == 0){
            return false;
        }
        try{
            outputStream = bluetoothSocket.getOutputStream();
            outputStream.write(data, off, len);
            outputStream.flush();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
