package com.tsy.sdk.social.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tsy.sdk.social.PlatformConfig;
import com.tsy.sdk.social.PlatformType;
import com.tsy.sdk.social.SSOHandler;
import com.tsy.sdk.social.listener.AuthListener;
import com.tsy.sdk.social.listener.ShareListener;
import com.tsy.sdk.social.share_media.IShareMedia;
import com.tsy.sdk.social.share_media.ShareImageMedia;
import com.tsy.sdk.social.share_media.ShareMusicMedia;
import com.tsy.sdk.social.share_media.ShareWebMedia;
import com.tsy.sdk.social.util.BitmapUtils;
import com.tsy.sdk.social.util.LogUtils;
import com.tsy.sdk.social.util.Utils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * QQ 第三方 Handler
 * Created by tsy on 16/8/18.
 */
public class QQHandler extends SSOHandler {

    private Context mContext;
    private Activity mActivity;

    private Tencent mTencent;

    private PlatformConfig.QQ mConfig;
    private AuthListener mAuthListener;
    private ShareListener mShareListener;

    public QQHandler() {

    }

    @Override
    public void onCreate(Context context, PlatformConfig.Platform config) {
        this.mContext = context;
        this.mConfig = (PlatformConfig.QQ) config;

        this.mTencent = Tencent.createInstance(mConfig.appId, mContext);
    }

    @Override
    public void authorize(Activity activity, AuthListener authListener) {
        this.mActivity = activity;
        this.mAuthListener = authListener;

        this.mTencent.login(this.mActivity, "all", new IUiListener() {
            @Override
            public void onComplete(Object o) {
                if (null == o || ((JSONObject)o) == null) {
                    LogUtils.e("onComplete response=null");
                    mAuthListener.onError(mConfig.getName(), "onComplete response=null");
                    return;
                }

                JSONObject response = (JSONObject) o;

                initOpenidAndToken(response);

                mAuthListener.onComplete(mConfig.getName(), Utils.jsonToMap(response));

                mTencent.logout(mActivity);
            }

            @Override
            public void onError(UiError uiError) {
                String errmsg = "errcode=" + uiError.errorCode + " errmsg=" + uiError.errorMessage + " errdetail=" + uiError.errorDetail;
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

        String path = Environment.getExternalStorageDirectory().toString() + "/socail_qq_img_tmp.png";
        File file = new File(path);
        if(file.exists()) {
            file.delete();
        }

        Bundle params = new Bundle();

        if(this.mConfig.getName() == PlatformType.QZONE) {      //qq空间
            if(shareMedia instanceof ShareWebMedia) {       //网页分享
                ShareWebMedia shareWebMedia = (ShareWebMedia) shareMedia;

                //图片保存本地
                BitmapUtils.saveBitmapFile(shareWebMedia.getThumb(), path);

                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareWebMedia.getTitle());
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareWebMedia.getDescription());
                params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareWebMedia.getWebPageUrl());

                ArrayList<String> path_arr = new ArrayList<>();
                path_arr.add(path);
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, path_arr);  //!这里是大坑 不能用SHARE_TO_QQ_IMAGE_LOCAL_URL
            } else {
                if(this.mShareListener != null) {
                    this.mShareListener.onError(this.mConfig.getName(), "shareMedia error");
                }
                return ;
            }

            //qq zone分享
            this.mTencent.shareToQzone(this.mActivity, params, new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    mShareListener.onComplete(mConfig.getName());
                }

                @Override
                public void onError(UiError uiError) {
                    String errmsg = "errcode=" + uiError.errorCode + " errmsg=" + uiError.errorMessage + " errdetail=" + uiError.errorDetail;
                    LogUtils.e(errmsg);
                    mShareListener.onError(mConfig.getName(), errmsg);
                }

                @Override
                public void onCancel() {
                    mShareListener.onCancel(mConfig.getName());
                }
            });
        } else {        //分享到qq
            if(shareMedia instanceof ShareWebMedia) {       //网页分享
                ShareWebMedia shareWebMedia = (ShareWebMedia) shareMedia;

                //图片保存本地
                BitmapUtils.saveBitmapFile(shareWebMedia.getThumb(), path);

                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, shareWebMedia.getTitle());
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareWebMedia.getDescription());
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareWebMedia.getWebPageUrl());
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path);
            } else if(shareMedia instanceof ShareImageMedia) {  //图片分享
                ShareImageMedia shareImageMedia = (ShareImageMedia) shareMedia;

                //图片保存本地
                BitmapUtils.saveBitmapFile(shareImageMedia.getImage(), path);

                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,path);
            } else if (shareMedia instanceof ShareMusicMedia) {  //音乐分享
                ShareMusicMedia shareMusicMedia = (ShareMusicMedia) shareMedia;

                //图片保存本地
                BitmapUtils.saveBitmapFile(shareMusicMedia.getThumb(), path);

                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, shareMusicMedia.getTitle());
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  shareMusicMedia.getDescription());
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  shareMusicMedia.getMusicUrl());
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path);
                params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, shareMusicMedia.getMusicUrl());
            } else {
                if(this.mShareListener != null) {
                    this.mShareListener.onError(this.mConfig.getName(), "shareMedia error");
                }
                return ;
            }

            //qq分享
            mTencent.shareToQQ(mActivity, params, new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    mShareListener.onComplete(mConfig.getName());
                }

                @Override
                public void onError(UiError uiError) {
                    String errmsg = "errcode=" + uiError.errorCode + " errmsg=" + uiError.errorMessage + " errdetail=" + uiError.errorDetail;
                    LogUtils.e(errmsg);
                    mShareListener.onError(mConfig.getName(), errmsg);
                }

                @Override
                public void onCancel() {
                    mShareListener.onCancel(mConfig.getName());
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, null);
    }

    //要初始化open_id和token
    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);

            mTencent.setAccessToken(token, expires);
            mTencent.setOpenId(openId);
        } catch(Exception e) {
        }
    }
}
