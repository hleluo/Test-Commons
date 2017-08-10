package com.mosent.common.zxing;

import com.mosent.common.zxing.camera.FrontLightMode;

/**
 * Created by Administrator on 2017/7/10.
 */

public class KeyManager {

    /* 相机配置 */
    public final static boolean KEY_AUTO_FOCUS = true;  //是否自动聚焦
    public final static boolean KEY_DISABLE_CONTINUOUS_FOCUS = true;    //是否关闭持续聚焦
    public final static boolean KEY_INVERT_SCAN = false;    //
    public final static boolean KEY_DISABLE_BARCODE_SCENE_MODE = true;
    public final static boolean KEY_DISABLE_METERING = true;    //是否关闭测量
    public final static boolean KEY_DISABLE_EXPOSURE = true;    //
    public final static FrontLightMode KEY_FRONT_LIGHT_MODE = FrontLightMode.OFF;   //前照灯模式

    public final static boolean KEY_PLAY_BEEP = false;  //是否开启声音提示
    public final static boolean KEY_VIBRATE = true; //是否开启震动提示
    public final static String BEEP_FILE_NAME = "beep1.ogg";    //提示声文件名

    /* 配置默认解析类型 */
    public final static boolean KEY_DECODE_1D_PRODUCT = true;   //条形码
    public final static boolean KEY_DECODE_1D_INDUSTRIAL = true;    //工业条码
    public final static boolean KEY_DECODE_QR = true;   //QR二维码
    public final static boolean KEY_DECODE_DATA_MATRIX = true;  //二维码
    public final static boolean KEY_DECODE_AZTEC = false;   //Aztec条码
    public final static boolean KEY_DECODE_PDF417 = false;  //PDF417条码

}
