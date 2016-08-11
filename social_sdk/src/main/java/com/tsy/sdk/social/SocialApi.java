package com.tsy.sdk.social;

import android.content.Context;

import com.tsy.sdk.social.listener.AuthListener;
import com.tsy.sdk.social.listener.ShareListener;
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

                default:
                    break;
            }
        }

        return mMapSSOHandler.get(platformType);
    }

    /**
     * 第三方登录授权
     * @param context
     * @param platformType 第三方平台
     * @param authListener 授权回调
     */
    public void doOauthVerify(Context context, PlatformType platformType, AuthListener authListener) {
        SSOHandler ssoHandler = getSSOHandler(platformType);
        ssoHandler.onCreate(context, PlatformConfig.getPlatformConfig(platformType));
        ssoHandler.authorize(authListener);
    }

    /**
     * 分享
     * @param context
     * @param platformType
     * @param shareMedia
     * @param shareListener
     */
    public void doShare(Context context, PlatformType platformType, IShareMedia shareMedia, ShareListener shareListener) {
        SSOHandler ssoHandler = getSSOHandler(platformType);
        ssoHandler.onCreate(context, PlatformConfig.getPlatformConfig(platformType));
        ssoHandler.share(shareMedia, shareListener);
    }
}
