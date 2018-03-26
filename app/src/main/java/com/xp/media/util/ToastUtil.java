package com.xp.media.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    /**
     * Toast 工具类
     */

    public static void showLongToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(Context context, int id) {
        Toast.makeText(context, id, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showShortToast(Context context, int id) {
        Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
    }
}
