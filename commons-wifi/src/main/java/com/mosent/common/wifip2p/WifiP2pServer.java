package com.mosent.common.wifip2p;

import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lj on 2017/6/25.
 */

public class WifiP2pServer {

    private final static int MSG_BEFORE_ACCEPTED = 1;
    private final static int MSG_ACCEPTED = 2;

    private WifiP2pCallback wifiP2pCallback;
    private ServerSocket serverSocket;
    private boolean readAvailable = true;

    public WifiP2pCallback getWifiP2pCallback() {
        return wifiP2pCallback;
    }

    public void setWifiP2pCallback(WifiP2pCallback wifiP2pCallback) {
        this.wifiP2pCallback = wifiP2pCallback;
    }

    private Handler handlerAccept = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_BEFORE_ACCEPTED:
                    if (wifiP2pCallback != null){
                        wifiP2pCallback.onBeforeAccepted();
                    }
                    break;
                case MSG_ACCEPTED:
                    if (wifiP2pCallback != null){
                        wifiP2pCallback.onAccepted((Socket)msg.obj);
                    }
                    break;
            }
        }
    };

    //接收线程
    private class AcceptThread extends Thread{

        private int port;

        public AcceptThread(int port){
            this.port = port;
        }

        @Override
        public void run() {
            try{
                serverSocket = new ServerSocket(port);
                if (handlerAccept != null) {
                    Message message = handlerAccept.obtainMessage();
                    message.what = MSG_BEFORE_ACCEPTED;
                    handlerAccept.sendMessage(message);
                }
                //接收连接
                Socket socket = serverSocket.accept();
                //连接完成
                if (handlerAccept != null) {
                    Message message = handlerAccept.obtainMessage();
                    message.what = MSG_ACCEPTED;
                    message.obj = socket;
                    handlerAccept.sendMessage(message);
                }
                if (wifiP2pCallback != null){
                    ReadThread readThread = new ReadThread(socket);
                    readThread.start();
                }
            }catch (Exception e){
            }
        }
    };

    /**
     * 监听连接
     * @param port
     */
    public void accept(int port){
        readAvailable = true;
        AcceptThread acceptThread = new AcceptThread(port);
        acceptThread.start();
    }

    //读线程
    private class ReadThread extends Thread{

        private Socket socket;

        public ReadThread(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            InputStream inputStream = null;
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
     * 写数据
     * @param data
     */
    public boolean write(Socket socket, String data){
        return data == null ? false : write(socket, data.getBytes());
    }

    /**
     * 写数据
     * @param data
     */
    public boolean write(Socket socket, byte[] data){
        return data == null ? false : write(socket, data, 0 , data.length);
    }

    /**
     * 写数据
     * @param data
     * @param off
     * @param len
     * @return
     */
    public boolean write(Socket socket, byte[] data, int off, int len){
        if (socket == null || data == null || data.length == 0){
            return false;
        }
        OutputStream outputStream = null;
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
        if (serverSocket != null){
            try {
                serverSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        readAvailable = false;
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