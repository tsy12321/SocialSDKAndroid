package com.tsy.sdk.social.weixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tsy.sdk.social.PlatformConfig;
import com.tsy.sdk.social.PlatformType;
import com.tsy.sdk.social.SocialApi;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by tsy on 16/8/4.
 */
public abstract class WXCallbackActivity extends Activity implements IWXAPIEventHandler {

    protected WXHandler mWXHandler = null;

    public WXCallbackActivity() {

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SocialApi api = SocialApi.get(this.getApplicationContext());
        this.mWXHandler = (WXHandler)api.getSSOHandler(PlatformType.WEIXIN);
        this.mWXHandler.onCreate(this.getApplicationContext(), PlatformConfig.getPlatformConfig(PlatformType.WEIXIN));
        this.mWXHandler.getWXApi().handleIntent(this.getIntent(), this);
    }

    protected final void onNewIntent(Intent paramIntent) {
        super.onNewIntent(paramIntent);
        SocialApi api = SocialApi.get(this.getApplicationContext());
        this.mWXHandler = (WXHandler)api.getSSOHandler(PlatformType.WEIXIN);
        this.mWXHandler.onCreate(this.getApplicationContext(), PlatformConfig.getPlatformConfig(PlatformType.WEIXIN));
        this.mWXHandler.getWXApi().handleIntent(this.getIntent(), this);
    }

    public void onResp(BaseResp resp) {
        if(this.mWXHandler != null && resp != null) {
            try {
                this.mWXHandler.getWXEventHandler().onResp(resp);
            } catch (Exception var3) {
                ;
            }
        }

        this.finish();
    }

    public void onReq(BaseReq req) {
        if(this.mWXHandler != null) {
            this.mWXHandler.getWXEventHandler().onReq(req);
        }

        this.finish();
    }
}
