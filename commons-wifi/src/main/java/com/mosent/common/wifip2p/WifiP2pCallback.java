package com.mosent.common.wifip2p;

import java.io.InputStream;
import java.net.Socket;

/**
 * Created by lj on 2017/7/2.
 */

public abstract class WifiP2pCallback {

    public final static int STATE_SUCCESS = 0;
    public final static int STATE_FAILURE = 1;

    /**
     * 连接成功
     * @param state
     */
    public void onConnected(int state){

    }

    /**
     * 连接断开
     */
    public void onDisconnected(){

    }

    /**
     * 数据可读
     * @param inputStream
     */
    public void onDataArrived(InputStream inputStream){

    }

    /***
     * 接收连接
     */
    public void onBeforeAccepted(){

    }

    /**
     * 接收连接
     * @param socket
     */
    public void onAccepted(Socket socket){

    }

}
