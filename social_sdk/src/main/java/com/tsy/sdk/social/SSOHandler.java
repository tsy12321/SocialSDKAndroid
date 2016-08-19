package com.tsy.sdk.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tsy.sdk.social.listener.AuthListener;
import com.tsy.sdk.social.listener.ShareListener;
import com.tsy.sdk.social.share_media.IShareMedia;

/**
 * Created by tsy on 16/8/4.
 */
public abstract class SSOHandler {

    /**
     * 初始化
     * @param config 配置信息
     */
    public void onCreate(Context context, PlatformConfig.Platform config) {

    }

    /**
     * 登录授权
     * @param activity
     * @param authListener 授权回调
     */
    public void authorize(Activity activity, AuthListener authListener) {

    }

    /**
     * 分享
     * @param shareMedia 分享内容
     * @param shareListener 分享回调
     */
    public void share(Activity activity, IShareMedia shareMedia, ShareListener shareListener) {

    }

    /**
     * 重写onActivityResult
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    /**
     * 是否安装
     * @return
     */
    public boolean isInstall() {
        return true;
    }
}
