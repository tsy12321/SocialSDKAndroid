package com.tsy.sdk.social;

import android.content.Context;

import com.tsy.sdk.social.listener.AuthListener;
import com.tsy.sdk.social.listener.ShareListener;
import com.tsy.sdk.social.share_media.IShareMedia;

/**
 * Created by tsy on 16/8/4.
 */
public abstract class SSOHandler {

    /**
     * 初始化
     * @param context
     * @param config 配置信息
     */
    public void onCreate(Context context, PlatformConfig.Platform config) {

    }

    /**
     * 登录授权
     * @param authListener 授权回调
     */
    public void authorize(AuthListener authListener) {

    }

    /**
     * 分享
     * @param shareMedia 分享内容
     * @param shareListener 分享回调
     */
    public void share(IShareMedia shareMedia, ShareListener shareListener) {

    }

    /**
     * 是否安装
     * @return
     */
    public boolean isInstall() {
        return true;
    }
}
