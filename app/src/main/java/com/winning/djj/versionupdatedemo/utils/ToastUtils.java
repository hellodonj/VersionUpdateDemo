package com.winning.djj.versionupdatedemo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 描述: 吐司工具类
 * 作者|时间: djj on 2019/4/1 13:41
 * 博客地址: http://www.jianshu.com/u/dfbde65a03fc
 */

public class ToastUtils {

    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    /**
     * 吐出一个显示时间较短的提示
     *
     * @param context 上下文
     * @param s       文本内容
     */
    public static void showToast(Context context, String s) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
    }
}
