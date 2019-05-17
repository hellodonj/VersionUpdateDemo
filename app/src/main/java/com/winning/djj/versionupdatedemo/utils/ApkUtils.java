package com.winning.djj.versionupdatedemo.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

/**
 * 描述: apk工具类
 * 作者|时间: djj on 2019/4/1 13:57
 * 博客地址: http://www.jianshu.com/u/dfbde65a03fc
 */

public class ApkUtils {
    private static final String TAG = ApkUtils.class.getSimpleName();

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序版本名称信息
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (Exception e) {

        }
        return null;
    }

    /**
     * @return 当前程序的版本号
     */
    public static int getVersionCode(Context context) {
        int version;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            version = 0;
        }
        return version;
    }

    /**
     * 得到安装的intent
     *
     * @param apkFile
     * @return
     */
    public static Intent getInstallIntent(File apkFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkFile.getAbsolutePath())),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void downloadFile(String url, int port, String username, String password, String localPath, String serverPath) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(url, port);
            ftpClient.setControlEncoding("GBK");
            ftpClient.login(username, password);// 登录
            //ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                // 断开连接
                ftpClient.disconnect();
                throw new IOException("connect fail: " + reply);
            } else {
                // 获取登录信息
                FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
                config.setServerLanguageCode("zh");
                ftpClient.configure(config);
                // 使用被动模式设为默认
                ftpClient.enterLocalPassiveMode();
                // 二进制文件支持
                ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                System.out.println("login");
            }
            ftpClient.changeWorkingDirectory(serverPath);// 转移到FTP服务器目录
            System.out.println("cd to path:" + serverPath);
            FTPFile[] files = ftpClient.listFiles(); // 得到目录的相应文件列表
            if (files.length == 0) {
                Log.e(TAG, "服务器文件不存在 serverPath=" + serverPath);

            }
            localPath = localPath + files[0].getName();
            long serverSize = files[0].getSize(); // 获取远程文件的长度  
            File localFile = new File(localPath);
            long localSize = 0;
            if (localFile.exists()) {
                localFile.delete();
            }
            // 进度  
            long step = serverSize / 100;
            long process = 0;
            long currentSize = 0;
            // 开始准备下载文件  
            ftpClient.enterLocalActiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            OutputStream out = new FileOutputStream(localFile, true);
            ftpClient.setRestartOffset(localSize); //设置从哪里开始下，就是断点下载 
            InputStream input = ftpClient.retrieveFileStream(serverPath);
            byte[] b = new byte[1024];
            int length = 0;
            while ((length = input.read(b)) != -1) {
                out.write(b, 0, length);
                currentSize = currentSize + length;
                if (currentSize / step != process) {
                    process = currentSize / step;
                    Log.e(TAG, "下载进度：" + process);
                }
            }
            out.flush();
            out.close();
            input.close();
            // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉  
            if (ftpClient.completePendingCommand()) {
                Log.e(TAG, "文件下载成功");
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
