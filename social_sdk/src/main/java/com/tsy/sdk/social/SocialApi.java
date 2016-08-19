package com.tsy.sdk.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tsy.sdk.social.listener.AuthListener;
import com.tsy.sdk.social.listener.ShareListener;
import com.tsy.sdk.social.qq.QQHandler;
import com.tsy.sdk.social.share_media.IShareMedia;
import com.tsy.sdk.social.weixin.WXHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * api调用统一入口
 * Created by tsy on 16/8/4.
 */
public class SocialApi {

    private static SocialApi mApi = null;
    private static Context mContext = null;

    private final Map<PlatformType, SSOHandler> mMapSSOHandler = new HashMap();

    private SocialApi(Context context) {
        mContext = context;
    }

    /**
     * 获取单例
     * @param context 建议传入全局context
     * @return
     */
    public static SocialApi get(Context context) {
        if(mApi == null) {
            mApi = new SocialApi(context);
        }

        return mApi;
    }

    public SSOHandler getSSOHandler(PlatformType platformType) {
        if(mMapSSOHandler.get(platformType) == null) {
            switch (platformType) {
                case WEIXIN:
                    mMapSSOHandler.put(platformType, new WXHandler());
                    break;

                case WEIXIN_CIRCLE:
                    mMapSSOHandler.put(platformType, new WXHandler());
                    break;

                case QQ:
                    mMapSSOHandler.put(platformType, new QQHandler());
                    break;

                case QZONE:
                    mMapSSOHandler.put(platformType, new QQHandler());
                    break;

                default:
                    break;
            }
        }

        return mMapSSOHandler.get(platformType);
    }

    /**
     * 第三方登录授权
     * @param activity
     * @param platformType 第三方平台
     * @param authListener 授权回调
     */
    public void doOauthVerify(Activity activity, PlatformType platformType, AuthListener authListener) {
        SSOHandler ssoHandler = getSSOHandler(platformType);
        ssoHandler.onCreate(mContext, PlatformConfig.getPlatformConfig(platformType));
        ssoHandler.authorize(activity, authListener);
    }

    /**
     * 分享
     * @param platformType
     * @param shareMedia
     * @param shareListener
     */
    public void doShare(Activity activity, PlatformType platformType, IShareMedia shareMedia, ShareListener shareListener) {
        SSOHandler ssoHandler = getSSOHandler(platformType);
        ssoHandler.onCreate(mContext, PlatformConfig.getPlatformConfig(platformType));
        ssoHandler.share(activity, shareMedia, shareListener);
    }

    /**
     * actvitiy result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Map.Entry<PlatformType, SSOHandler> entry : mMapSSOHandler.entrySet()) {
            entry.getValue().onActivityResult(requestCode, resultCode, data);
        }
    }
}
