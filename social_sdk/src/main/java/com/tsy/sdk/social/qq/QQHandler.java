package com.tsy.sdk.social.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tsy.sdk.social.PlatformConfig;
import com.tsy.sdk.social.SSOHandler;
import com.tsy.sdk.social.listener.AuthListener;
import com.tsy.sdk.social.listener.ShareListener;
import com.tsy.sdk.social.share_media.IShareMedia;
import com.tsy.sdk.social.util.LogUtils;
import com.tsy.sdk.social.util.Utils;

import org.json.JSONObject;

/**
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

    @Override
    public void share(IShareMedia shareMedia, ShareListener shareListener) {
        super.share(shareMedia, shareListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, null);
    }

}
