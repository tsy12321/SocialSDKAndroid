package com.tsy.sdk.social.weixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tsy.sdk.social.PlatformConfig;
import com.tsy.sdk.social.PlatformType;
import com.tsy.sdk.social.SocialApi;

/**
 * Created by tsy on 16/8/4.
 */
public class WXCallbackActivity extends Activity implements IWXAPIEventHandler {

    protected WXHandler mWXHandler = null;
    protected WXHandler mWXCircleHandler = null;


    public WXCallbackActivity() {

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SocialApi api = SocialApi.get(this.getApplicationContext());
        this.mWXHandler = (WXHandler)api.getSSOHandler(PlatformType.WEIXIN);
        this.mWXHandler.onCreate(this.getApplicationContext(), PlatformConfig.getPlatformConfig(PlatformType.WEIXIN));

        this.mWXCircleHandler = (WXHandler)api.getSSOHandler(PlatformType.WEIXIN_CIRCLE);
        this.mWXCircleHandler.onCreate(this.getApplicationContext(), PlatformConfig.getPlatformConfig(PlatformType.WEIXIN_CIRCLE));

        this.mWXHandler.getWXApi().handleIntent(this.getIntent(), this);
    }

    protected final void onNewIntent(Intent paramIntent) {
        super.onNewIntent(paramIntent);
        SocialApi api = SocialApi.get(this.getApplicationContext());
        this.mWXHandler = (WXHandler)api.getSSOHandler(PlatformType.WEIXIN);
        this.mWXHandler.onCreate(this.getApplicationContext(), PlatformConfig.getPlatformConfig(PlatformType.WEIXIN));

        this.mWXCircleHandler = (WXHandler)api.getSSOHandler(PlatformType.WEIXIN_CIRCLE);
        this.mWXCircleHandler.onCreate(this.getApplicationContext(), PlatformConfig.getPlatformConfig(PlatformType.WEIXIN_CIRCLE));

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

        if(this.mWXCircleHandler != null && resp != null) {
            try {
                this.mWXCircleHandler.getWXEventHandler().onResp(resp);
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
