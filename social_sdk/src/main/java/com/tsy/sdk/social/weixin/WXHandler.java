package com.tsy.sdk.social.weixin;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tsy.sdk.social.PlatformConfig;
import com.tsy.sdk.social.PlatformType;
import com.tsy.sdk.social.SSOHandler;
import com.tsy.sdk.social.listener.AuthListener;
import com.tsy.sdk.social.listener.ShareListener;
import com.tsy.sdk.social.share_media.IShareMedia;
import com.tsy.sdk.social.share_media.ShareImageMedia;
import com.tsy.sdk.social.share_media.ShareMusicMedia;
import com.tsy.sdk.social.share_media.ShareTextMedia;
import com.tsy.sdk.social.share_media.ShareVideoMedia;
import com.tsy.sdk.social.share_media.ShareWebMedia;
import com.tsy.sdk.social.util.BitmapUtils;
import com.tsy.sdk.social.util.LogUtils;
import com.tsy.sdk.social.util.NetUtils;
import com.tsy.sdk.social.util.Utils;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

/**
 * 微信处理Handler
 * Created by tsy on 16/8/4.
 */
public class WXHandler extends SSOHandler {

    private Context mContext;

    private IWXAPI mWXApi;
    private static String sScope = "snsapi_userinfo,snsapi_friend,snsapi_message";
    private IWXAPIEventHandler mEventHandler;

    private PlatformConfig.Weixin mConfig;
    private AuthListener mAuthListener;
    private ShareListener mShareListener;

    public WXHandler() {
        this.mEventHandler = new IWXAPIEventHandler() {
            public void onResp(BaseResp resp) {
                int type = resp.getType();
                switch(type) {
                    case ConstantsAPI.COMMAND_SENDAUTH:     //授权返回
                        WXHandler.this.onAuthCallback((SendAuth.Resp)resp);
                        break;

                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:        //分享返回
                        WXHandler.this.onShareCallback((SendMessageToWX.Resp)resp);
                        break;
                }

            }

            public void onReq(BaseReq req) {
            }
        };
    }

    @Override
    public void onCreate(Context context, PlatformConfig.Platform config) {
        this.mContext = context;
        this.mConfig = (PlatformConfig.Weixin) config;

        this.mWXApi = WXAPIFactory.createWXAPI(context.getApplicationContext(), this.mConfig.appId);
        this.mWXApi.registerApp(this.mConfig.appId);
    }

    @Override
    public boolean isInstall() {
        return this.mWXApi.isWXAppInstalled();
    }

    @Override
    public void authorize(AuthListener authListener) {
        if(!isInstall()) {
            LogUtils.e("wx not install");
            return ;
        }

        this.mAuthListener = authListener;

        SendAuth.Req req1 = new SendAuth.Req();
        req1.scope = sScope;
        req1.state = "none";

        if(!this.mWXApi.sendReq(req1)) {
            this.mAuthListener.onError(this.mConfig.getName(), "sendReq fail");
            LogUtils.e("wxapi sendReq fail");
        }
    }

    //验证回调
    protected void onAuthCallback(SendAuth.Resp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:       //授权成功
                getAuthWithCode(resp.code);
                break;

            case BaseResp.ErrCode.ERR_USER_CANCEL:      //授权取消
                if(this.mAuthListener != null) {
                    this.mAuthListener.onCancel(PlatformType.WEIXIN);
                }
                break;

            default:    //授权失败
                CharSequence err = TextUtils.concat(new CharSequence[]{"weixin auth error (", String.valueOf(resp.errCode), "):", resp.errStr});
                if(mAuthListener != null) {
                    mAuthListener.onError(PlatformType.WEIXIN, err.toString());
                }
                break;
        }
    }

    private void getAuthWithCode(String code) {
        StringBuilder authURL = new StringBuilder();
        authURL.append("https://api.weixin.qq.com/sns/oauth2/access_token?");
        authURL.append("appid=").append(this.mConfig.appId);
        authURL.append("&secret=").append(this.mConfig.appSecret);
        authURL.append("&code=").append(code);
        authURL.append("&grant_type=authorization_code");

        NetUtils.doGet(authURL.toString(), new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                if(WXHandler.this.mAuthListener != null) {
                    WXHandler.this.mAuthListener.onComplete(PlatformType.WEIXIN, Utils.jsonToMap(response));
                }
            }

            @Override
            public void onFailure() {
                if(WXHandler.this.mAuthListener != null) {
                    WXHandler.this.mAuthListener.onError(PlatformType.WEIXIN, "weixin getAuthWithCode err");
                }
            }
        });
    }

    @Override
    public void share(IShareMedia shareMedia, ShareListener shareListener) {
        this.mShareListener = shareListener;

        WXMediaMessage msg = new WXMediaMessage();
        String type = "";

        if(shareMedia instanceof ShareWebMedia) {       //网页分享
            ShareWebMedia shareWebMedia = (ShareWebMedia) shareMedia;
            type = "webpage";

            //web object
            WXWebpageObject webpageObject = new WXWebpageObject();
            webpageObject.webpageUrl = shareWebMedia.getWebPageUrl();

            msg.mediaObject = webpageObject;
            msg.title = shareWebMedia.getTitle();
            msg.description = shareWebMedia.getDescription();
            msg.thumbData = BitmapUtils.bitmap2Bytes(shareWebMedia.getThumb());
        } else if(shareMedia instanceof ShareTextMedia) {   //文字分享
            ShareTextMedia shareTextMedia = (ShareTextMedia) shareMedia;
            type = "text";

            //text object
            WXTextObject textObject = new WXTextObject();
            textObject.text = shareTextMedia.getText();

            msg.mediaObject = textObject;
            msg.description = shareTextMedia.getText();
        } else if(shareMedia instanceof ShareImageMedia) {  //图片分享
            ShareImageMedia shareImageMedia = (ShareImageMedia) shareMedia;
            type = "image";

            //image object
            WXImageObject imageObject = new WXImageObject();
            //image限制10M
            imageObject.imageData = BitmapUtils.compressBitmap(BitmapUtils.bitmap2Bytes(shareImageMedia.getImage()), 10 * 1024 * 1024);

            msg.mediaObject = imageObject;

            //直接缩放图片
            Bitmap thumb = Bitmap.createScaledBitmap(shareImageMedia.getImage(), 200, 200, true);
            msg.thumbData = BitmapUtils.bitmap2Bytes(thumb);
            thumb.recycle();
        } else if(shareMedia instanceof ShareMusicMedia) {  //音乐分享
            ShareMusicMedia shareMusicMedia = (ShareMusicMedia) shareMedia;
            type = "music";

            WXMusicObject musicObject = new WXMusicObject();
            musicObject.musicUrl = shareMusicMedia.getMusicUrl();

            msg.mediaObject = musicObject;
            msg.title = shareMusicMedia.getTitle();
            msg.description = shareMusicMedia.getDescription();
            msg.thumbData = BitmapUtils.bitmap2Bytes(shareMusicMedia.getThumb());
        } else if(shareMedia instanceof ShareVideoMedia) {      //视频分享
            ShareVideoMedia shareVideoMedia = (ShareVideoMedia) shareMedia;
            type = "video";

            WXVideoObject videoObject = new WXVideoObject();
            videoObject.videoUrl = shareVideoMedia.getVideoUrl();

            msg.mediaObject = videoObject;
            msg.title = shareVideoMedia.getTitle();
            msg.description = shareVideoMedia.getDescription();
            msg.thumbData = BitmapUtils.bitmap2Bytes(shareVideoMedia.getThumb());
        } else {
            if(this.mShareListener != null) {
                this.mShareListener.onError(this.mConfig.getName(), "shareMedia error");
            }
            return ;
        }

        //压缩缩略图到32kb
        if(msg.thumbData.length > '耀') {        //微信sdk里面判断的大小
            msg.thumbData = BitmapUtils.compressBitmap(msg.thumbData, '耀');
        }

        //发起request
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.transaction = buildTransaction(type);

        if(this.mConfig.getName() == PlatformType.WEIXIN) {     //分享好友
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else if(this.mConfig.getName() == PlatformType.WEIXIN_CIRCLE) {      //分享朋友圈
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }

        if(!this.mWXApi.sendReq(req)) {
            if(this.mShareListener != null) {
                this.mShareListener.onError(this.mConfig.getName(), "sendReq fail");
            }
            LogUtils.e("wxapi sendReq fail");
        }
    }

    protected void onShareCallback(com.tencent.mm.sdk.modelmsg.SendMessageToWX.Resp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:       //分享成功
                if(this.mShareListener != null) {
                    this.mShareListener.onComplete(this.mConfig.getName());
                }
                break;

            case BaseResp.ErrCode.ERR_USER_CANCEL:      //分享取消
                if(this.mShareListener != null) {
                    this.mShareListener.onCancel(this.mConfig.getName());
                }
                break;

            default:    //分享失败
                CharSequence err = TextUtils.concat(new CharSequence[]{"weixin share error (", String.valueOf(resp.errCode), "):", resp.errStr});
                if(mShareListener != null) {
                    mShareListener.onError(this.mConfig.getName(), err.toString());
                }
                break;
        }
    }

    private String buildTransaction(String type) {
        return type == null?String.valueOf(System.currentTimeMillis()):type + System.currentTimeMillis();
    }

    public IWXAPI getWXApi() {
        return this.mWXApi;
    }

    public IWXAPIEventHandler getWXEventHandler() {
        return this.mEventHandler;
    }
}
