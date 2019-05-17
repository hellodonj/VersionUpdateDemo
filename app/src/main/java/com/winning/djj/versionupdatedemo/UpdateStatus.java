package com.winning.djj.versionupdatedemo;

/**
 * 描述: 检测版本状态类
 * 作者|时间: djj on 2019/4/1 13:52
 * 博客地址: http://www.jianshu.com/u/dfbde65a03fc
 */

public interface UpdateStatus {

    /**
     * 没有新版本
     */
    public static int NO = 1;

    /**
     * 有新版本
     */
    public static int YES = 2;

    /**
     * 链接超时
     */
    public static int TIMEOUT = 3;

    /**
     * 没有wifi
     */
    public static int NOWIFI = 4;

    /**
     * 数据解析出错
     */
    public static int ERROR = -1;
}
