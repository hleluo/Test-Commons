package com.monsent.commons.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by lj on 2017/6/25.
 */

public class BluetoothServer {

    public final static int STATE_SUCCESS = 0;
    public final static int STATE_FAILURE = 1;

    private final static String TAG = BluetoothServer.class.getSimpleName();

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket bluetoothServerSocket;
    private BluetoothSocket connectedSocket;
    private BluetoothCallback bluetoothCallback;
    private AcceptThread acceptThread;
    private boolean readAvailable = true;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    private BluetoothServer() {

    }

    public BluetoothServer(Context context) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothCallback getBluetoothCallback() {
        return bluetoothCallback;
    }

    public void setBluetoothCallback(BluetoothCallback bluetoothCallback) {
        this.bluetoothCallback = bluetoothCallback;
    }

    /**
     * 初始化蓝牙开关
     * @return
     */
    public boolean init(){
        if (bluetoothAdapter == null){
            return false;
        }
        if (bluetoothAdapter.isEnabled()){
            return true;
        }
        return bluetoothAdapter.enable();
    }

    public void accept(String name, String uuid){
        acceptThread = new AcceptThread(name, uuid);
        acceptThread.start();
    }

    //连接线程
    private class AcceptThread extends Thread{

        private String name;
        private String uuid;

        public AcceptThread(String name, String uuid){
            this.name = name;
            this.uuid = uuid;
        }

        @Override
        public void run() {
            if (bluetoothAdapter == null){
                return;
            }
            BluetoothDevice bluetoothDevice = null;
            try {
                bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, UUID.fromString(uuid));
                connectedSocket = bluetoothServerSocket.accept();
                bluetoothDevice = connectedSocket.getRemoteDevice();
                Log.i(TAG, "Bluetooth onConnected");
                if (bluetoothCallback != null){
                    //启动读数据
                    ReadThread readThread = new ReadThread();
                    readThread.start();
                    bluetoothCallback.onConnected(bluetoothDevice, STATE_SUCCESS);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (bluetoothCallback != null){
                    bluetoothCallback.onConnected(bluetoothDevice, STATE_FAILURE);
                }
            }
        }
    }

    //读线程
    private class ReadThread extends Thread{

        @Override
        public void run() {
            try{
                inputStream = connectedSocket.getInputStream();
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
     * 断开连接
     */
    public void disconnect(){
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
        if (bluetoothServerSocket != null){
            try {
                bluetoothServerSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (acceptThread != null){
            try{
                acceptThread.interrupt();
                acceptThread = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        readAvailable = false;
        connectedSocket = null;
        if (bluetoothCallback != null){
            bluetoothCallback.onDisconnected();
        }
    }

    /**
     * 写数据
     * @param data
     */
    public boolean write(String data){
        return write(data.getBytes());
    }

    /**
     * 写数据
     * @param data
     */
    public boolean write(byte[] data){
        return write(data, 0 , data.length);
    }

    /**
     * 写数据
     * @param data
     * @param off
     * @param len
     */
    public boolean write(byte[] data, int off, int len){
        if (connectedSocket == null || data == null || data.length == 0){
            return false;
        }
        try{
            outputStream = connectedSocket.getOutputStream();
            outputStream.write(data, off, len);
            outputStream.flush();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
