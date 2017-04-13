package com.tsy.sdk.social.sina;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tsy.sdk.social.PlatformConfig;
import com.tsy.sdk.social.SSOHandler;
import com.tsy.sdk.social.listener.AuthListener;
import com.tsy.sdk.social.listener.ShareListener;
import com.tsy.sdk.social.share_media.IShareMedia;
import com.tsy.sdk.social.share_media.ShareTextImageMedia;
import com.tsy.sdk.social.util.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 新浪微博 第三方Hnadler
 * Created by tsy on 16/9/18.
 */
public class SinaWBHandler extends SSOHandler {

    private Context mContext;
    private Activity mActivity;

    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private IWeiboShareAPI mWeiboShareAPI;

    private PlatformConfig.SinaWB mConfig;
    private AuthListener mAuthListener;
    private ShareListener mShareListener;

    private static String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";// 应用的回调页 要和微博开放平台的回调地址一致
    private final String SCOPE = "";

    /**
     * 设置微博 REDIRECT_URL
     * @param redirctUrl
     */
    public static void setRedirctUrl(String redirctUrl) {
        REDIRECT_URL = redirctUrl;
    }

    @Override
    public void onCreate(Context context, PlatformConfig.Platform config) {
        this.mContext = context;
        this.mConfig = (PlatformConfig.SinaWB) config;

        this.mAuthInfo = new AuthInfo(mContext, mConfig.appKey, REDIRECT_URL, SCOPE);

        this.mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, mConfig.appKey);
        this.mWeiboShareAPI.registerApp();
    }

    @Override
    public void authorize(Activity activity, AuthListener authListener) {
        this.mActivity = activity;
        this.mAuthListener = authListener;

        this.mSsoHandler = new SsoHandler(mActivity, mAuthInfo);

        mSsoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                // 从 Bundle 中解析 Token
                Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
                if(accessToken.isSessionValid()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("uid", accessToken.getUid());
                    map.put("access_token", accessToken.getToken());
                    map.put("refresh_token", accessToken.getRefreshToken());
                    map.put("expire_time", "" + accessToken.getExpiresTime());

                    mAuthListener.onComplete(mConfig.getName(), map);
                } else {
                    String errmsg = "errmsg=accessToken is not SessionValid";
                    LogUtils.e(errmsg);
                    mAuthListener.onError(mConfig.getName(), errmsg);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                String errmsg = "errmsg=" + e.getMessage();
                LogUtils.e(errmsg);
                mAuthListener.onError(mConfig.getName(), errmsg);
            }

            @Override
            public void onCancel() {
                mAuthListener.onCancel(mConfig.getName());
            }
        });
    }

    @Override
    public void share(Activity activity, IShareMedia shareMedia, ShareListener shareListener) {
        this.mActivity = activity;
        this.mShareListener = shareListener;

        this.mSsoHandler = new SsoHandler(mActivity, mAuthInfo);

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        if(shareMedia instanceof ShareTextImageMedia) {       //文字图片分享
            ShareTextImageMedia shareTextImageMedia = (ShareTextImageMedia) shareMedia;

            if(shareTextImageMedia.getText().length() > 0) {
                TextObject textObject = new TextObject();
                textObject.text = shareTextImageMedia.getText();
                weiboMessage.textObject = textObject;
            }

            if(shareTextImageMedia.getImage() != null) {
                ImageObject imageObject = new ImageObject();
                imageObject.setImageObject(shareTextImageMedia.getImage());
                weiboMessage.imageObject = imageObject;
            }
        } else {
            if(this.mShareListener != null) {
                this.mShareListener.onError(this.mConfig.getName(), "weibo is not support this shareMedia");
            }
            return ;
        }

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(mContext.getApplicationContext());
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        mWeiboShareAPI.sendRequest(mActivity, request, mAuthInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException( WeiboException arg0 ) {
            }

            @Override
            public void onComplete( Bundle bundle ) {
                // TODO Auto-generated method stub
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(mContext.getApplicationContext(), newToken);
            }

            @Override
            public void onCancel() {
            }
        });

//        mWeiboShareAPI.sendRequest(mActivity, request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public void onNewIntent(Intent intent, IWeiboHandler.Response response) {
        if(mWeiboShareAPI != null) {
            mWeiboShareAPI.handleWeiboResponse(intent, response);
        }
    }

    public void onResponse(BaseResponse baseResponse) {
        if(baseResponse!= null){
            switch (baseResponse.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    if(this.mShareListener != null) {
                        this.mShareListener.onComplete(this.mConfig.getName());
                    }
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    if(this.mShareListener != null) {
                        this.mShareListener.onCancel(this.mConfig.getName());
                    }
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    if(this.mShareListener != null) {
                        this.mShareListener.onError(this.mConfig.getName(), baseResponse.errMsg);
                    }
                    break;
            }
        }
    }
}
