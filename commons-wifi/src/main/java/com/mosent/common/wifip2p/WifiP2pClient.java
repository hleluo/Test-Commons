package com.mosent.common.wifip2p;

import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by lj on 2017/6/25.
 */

public class WifiP2pClient {

    private WifiP2pCallback wifiP2pCallback;
    private Socket socket;
    private String connectedIp;
    private int connectedPort;
    private boolean readAvailable = true;
    private ReadThread readThread;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public WifiP2pCallback getWifiP2pCallback() {
        return wifiP2pCallback;
    }

    public void setWifiP2pCallback(WifiP2pCallback wifiP2pCallback) {
        this.wifiP2pCallback = wifiP2pCallback;
    }

    private Handler handlerConnect = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WifiP2pCallback.STATE_SUCCESS:
                    if (wifiP2pCallback != null){
                        wifiP2pCallback.onConnected(WifiP2pCallback.STATE_SUCCESS);
                    }
                    break;
                case WifiP2pCallback.STATE_FAILURE:
                    if (wifiP2pCallback != null){
                        wifiP2pCallback.onConnected(WifiP2pCallback.STATE_FAILURE);
                    }
                    break;
            }
        }
    };
    //连接线程
    private class ConnectThread extends Thread{

        private String ip;
        private int port;

        public ConnectThread(String ip, int port){
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            try{
                socket.bind(null);
                socket.connect(new InetSocketAddress(ip, port), 3000);
                connectedIp = ip;
                connectedPort = port;
                if (wifiP2pCallback != null){
                    if (readThread == null) {
                        readThread = new ReadThread();
                    }
                    readThread.start();
                    Message message = handlerConnect.obtainMessage();
                    message.what = WifiP2pCallback.STATE_SUCCESS;
                    handlerConnect.sendMessage(message);
                }
            }catch (Exception e){
                e.printStackTrace();
                if (wifiP2pCallback != null){
                    Message message = handlerConnect.obtainMessage();
                    message.what = WifiP2pCallback.STATE_FAILURE;
                    handlerConnect.sendMessage(message);
                }
            }
        }
    };

    /**
     * 连接sockt
     * @param ip
     * @param port
     */
    public void connect(String ip, int port){
        if (socket == null){
            socket = new Socket();
        }
        readAvailable = true;
        if (ip.equals(connectedIp) && port == connectedPort && socket.isConnected()){
            if (wifiP2pCallback != null){
                if (readThread == null) {
                    readThread = new ReadThread();
                }
                readThread.start();
                wifiP2pCallback.onConnected(WifiP2pCallback.STATE_SUCCESS);
            }
            return;
        }
        ConnectThread connectThread = new ConnectThread(ip, port);
        connectThread.start();
    }

    //读线程
    private class ReadThread extends Thread{

        @Override
        public void run() {
            try{
                inputStream = socket.getInputStream();
                while (readAvailable) {
                    if (inputStream != null && wifiP2pCallback != null) {
                        wifiP2pCallback.onDataArrived(inputStream);
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
        return socket == null ? false : socket.isConnected();
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
     * @return
     */
    public boolean write(byte[] data, int off, int len){
        if (socket == null || data == null || data.length == 0){
            return false;
        }
        try{
            outputStream = socket.getOutputStream();
            outputStream.write(data, off, len);
            outputStream.flush();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 释放资源
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
        if (socket != null){
            try {
                socket.close();
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
        connectedIp = null;
        connectedPort = -1;
    }

    /**
     * 断开socket连接
     */
    public void disconnect(){
        dispose();
        if (wifiP2pCallback != null){
            wifiP2pCallback.onDisconnected();
        }
    }
}