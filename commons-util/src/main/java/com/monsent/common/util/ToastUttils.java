package com.monsent.common.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/7/7.
 */

public class ToastUttils {

    public static void show(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
