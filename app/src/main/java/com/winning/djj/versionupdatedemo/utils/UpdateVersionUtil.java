package com.winning.djj.versionupdatedemo.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.winning.djj.versionupdatedemo.HttpRequest;
import com.winning.djj.versionupdatedemo.R;
import com.winning.djj.versionupdatedemo.ServerReqAddress;
import com.winning.djj.versionupdatedemo.UpdateStatus;
import com.winning.djj.versionupdatedemo.UpdateVersionService;
import com.winning.djj.versionupdatedemo.VersionInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;

/**
 * 描述: 版本更新工具类
 * 作者|时间: djj on 2019/4/1 13:33
 * 博客地址: http://www.jianshu.com/u/dfbde65a03fc
 */

public class UpdateVersionUtil {

    /**
     * 接口回调
     */
    public interface UpdateListener {
        void onUpdateReturned(int updateStatus, VersionInfo versionInfo);
    }

    public UpdateListener updateListener;

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    /**
     * 网络测试 检测版本
     *
     * @param context 上下文
     */
    public static void checkVersion(final Context context, final UpdateListener updateListener) {
        HttpRequest.get(ServerReqAddress.UPDATA_VERSION_REQ, new HttpRequest.RequestCallBackListener() {

            @Override
            public void onSuccess(String resultData) {
                try {
                    JSONObject jsonObject = JsonUtils.stringToJson(resultData);
                    JSONArray array = jsonObject.getJSONArray("data");
                    VersionInfo mVersionInfo = JsonUtils.jsonToBean(array.getJSONObject(0).toString(), VersionInfo.class);
                    //客户端版本
                    int clientVersionCode = ApkUtils.getVersionCode(context);
                    //服务器版本
                    int serverVersionCode = mVersionInfo.getVersionCode();
                    //有新版本
                    if (clientVersionCode < serverVersionCode) {
                        int i = NetworkUtil.checkedNetWorkType(context);
                        if (i == NetworkUtil.NOWIFI) {
                            updateListener.onUpdateReturned(UpdateStatus.NOWIFI, mVersionInfo);
                        } else if (i == NetworkUtil.WIFI) {
                            updateListener.onUpdateReturned(UpdateStatus.YES, mVersionInfo);
                        }
                    } else {
                        //无新本
                        updateListener.onUpdateReturned(UpdateStatus.NO, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    updateListener.onUpdateReturned(UpdateStatus.ERROR, null);
                }
            }

            @Override
            public void onFailure(String error) {
                updateListener.onUpdateReturned(UpdateStatus.TIMEOUT, null);
            }
        });
    }


    /**
     * 本地测试
     */
    public static void localCheckedVersion(final Context context, final UpdateListener updateListener) {
        try {
//            JSONObject jsonObject = JsonUtils.stringToJson(resultData);
//            JSONArray array = jsonObject.getJSONArray("data");
//            VersionInfo mVersionInfo = JsonUtils.jsonToBean(array.getJSONObject(0).toString(), VersionInfo.class);
            VersionInfo mVersionInfo = new VersionInfo();
            mVersionInfo.setDownloadUrl("http://172.17.17.110/gdmsaec-app.apk");
            mVersionInfo.setVersionDesc("\n更新内容：\n1、增加xxxxxxxxx功能\n2、增加xxxxxxxxx显示！\n3、用户界面优化！\n4、处理了xxxxxxxxBUG！");
            mVersionInfo.setVersionCode(2);
            mVersionInfo.setVersionName("v2020");
            mVersionInfo.setVersionSize("20.1M");
            mVersionInfo.setId("1");
            int clientVersionCode = ApkUtils.getVersionCode(context);
            int serverVersionCode = mVersionInfo.getVersionCode();
            //有新版本
            if (clientVersionCode < serverVersionCode) {
                int i = NetworkUtil.checkedNetWorkType(context);
                if (i == NetworkUtil.NOWIFI) {
                    updateListener.onUpdateReturned(UpdateStatus.NOWIFI, mVersionInfo);
                } else if (i == NetworkUtil.WIFI) {
                    updateListener.onUpdateReturned(UpdateStatus.YES, mVersionInfo);
                }
            } else {
                //无新本
                updateListener.onUpdateReturned(UpdateStatus.NO, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            updateListener.onUpdateReturned(UpdateStatus.ERROR, null);
        }
    }


    /**
     * 弹出新版本提示
     *
     * @param context     上下文
     * @param versionInfo 更新内容
     */
    public static void showDialog(final Context context, final VersionInfo versionInfo) {
        final Dialog dialog = new AlertDialog.Builder(context).create();
        final File file = new File(SDCardUtils.getRootDirectory() + "/updateVersion/gdmsaec-app.apk");
        dialog.setCancelable(true);// 可以用“返回键”取消
        dialog.setCanceledOnTouchOutside(false);//
        dialog.show();
        View view = LayoutInflater.from(context).inflate(R.layout.version_update_dialog, null);
        dialog.setContentView(view);

        final Button btnOk = (Button) view.findViewById(R.id.btn_update_id_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_update_id_cancel);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_update_content);
        TextView tvUpdateTile = (TextView) view.findViewById(R.id.tv_update_title);
        final TextView tvUpdateMsgSize = (TextView) view.findViewById(R.id.tv_update_msg_size);

        tvContent.setText(versionInfo.getVersionDesc());
        tvUpdateTile.setText("最新版本：" + versionInfo.getVersionName());

        if (file.exists() && file.getName().equals("gdmsaec-app.apk")) {
            tvUpdateMsgSize.setText("新版本已经下载，是否安装？");
        } else {
            tvUpdateMsgSize.setText("新版本大小：" + versionInfo.getVersionSize());
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (v.getId() == R.id.btn_update_id_ok) {
                    //新版本已经下载
                    if (file.exists() && file.getName().equals("gdmsaec-app.apk")) {
                        Intent intent = ApkUtils.getInstallIntent(file);
                        context.startActivity(intent);
                    } else {
                        //没有下载，则开启服务下载新版本
                        Intent intent = new Intent(context, UpdateVersionService.class);
                        intent.putExtra("downloadUrl", versionInfo.getDownloadUrl());
                        context.startService(intent);
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 收起通知栏
     *
     * @param context
     */
    public static void collapseStatusBar(Context context) {
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;
            if (Build.VERSION.SDK_INT <= 16) {
                collapse = statusBarManager.getClass().getMethod("collapse");
            } else {
                collapse = statusBarManager.getClass().getMethod("collapsePanels");
            }
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }
}
