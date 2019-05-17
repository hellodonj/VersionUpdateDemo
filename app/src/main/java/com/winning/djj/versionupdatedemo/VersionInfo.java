package com.winning.djj.versionupdatedemo;

import java.io.Serializable;

/**
 * 描述: 版本更新的实体类
 * 作者|时间: djj on 2019/4/1 13:41
 * 博客地址: http://www.jianshu.com/u/dfbde65a03fc
 */

public class VersionInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String versionName; //版本名
    private int versionCode;    //版本号
    private String versionDesc; //版本描述信息内容
    private String downloadUrl; //新版本的下载路径
    private String versionSize; //版本大小


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersionSize() {
        return versionSize;
    }

    public void setVersionSize(String versionSize) {
        this.versionSize = versionSize;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

}
