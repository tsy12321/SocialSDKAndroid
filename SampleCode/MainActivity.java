package com.tsy.girl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioGroup;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.tsy.girl.util.BitmapUtils;
import com.tsy.sdk.social.PlatformConfig;
import com.tsy.sdk.social.PlatformType;
import com.tsy.sdk.social.SocialApi;
import com.tsy.sdk.social.listener.AuthListener;
import com.tsy.sdk.social.listener.ShareListener;
import com.tsy.sdk.social.share_media.IShareMedia;
import com.tsy.sdk.social.share_media.ShareImageMedia;
import com.tsy.sdk.social.share_media.ShareMusicMedia;
import com.tsy.sdk.social.share_media.ShareTextMedia;
import com.tsy.sdk.social.share_media.ShareVideoMedia;
import com.tsy.sdk.social.share_media.ShareWebMedia;
import com.tsy.sdk.social.sina.SinaWBHandler;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements IWeiboHandler.Response{

    @BindView(R.id.radioGShareMedia)
    RadioGroup radioGShareMedia;

    @BindView(R.id.radioGSharePlatform)
    RadioGroup radioGSharePlatform;

    private static final String WX_APPID = "";    //申请的wx appid
    private static final String QQ_APPID = "";    //申请的qq appid
    private static final String SINA_WB_APPKEY = "";       //申请的新浪微博 appkey

    private SocialApi mSocialApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        PlatformConfig.setWeixin(WX_APPID);
        PlatformConfig.setQQ(QQ_APPID);
        PlatformConfig.setSinaWB(SINA_WB_APPKEY);

        mSocialApi = SocialApi.get(getApplicationContext());

        if (savedInstanceState != null) {
            ((SinaWBHandler)mSocialApi.getSSOHandler(PlatformType.SINA_WB)).onNewIntent(getIntent(), this);
        }
    }

    /**
     * 微信登录
     */
    @OnClick(R.id.btnWXLogin)
    public void onWXLogin() {
        mSocialApi.doOauthVerify(this, PlatformType.WEIXIN, new AuthListener() {
            @Override
            public void onComplete(PlatformType platform_type, Map<String, String> map) {
                Log.i("tsy", "login onComplete:" + map);
            }

            @Override
            public void onError(PlatformType platform_type, String err_msg) {
                Log.i("tsy", "login onError:" + err_msg);
            }

            @Override
            public void onCancel(PlatformType platform_type) {
                Log.i("tsy", "login onCancel");
            }
        });
    }

    /**
     * qq登录
     */
    @OnClick(R.id.btnQQLogin)
    public void onQQLogin() {
        mSocialApi.doOauthVerify(this, PlatformType.QQ, new AuthListener() {
            @Override
            public void onComplete(PlatformType platform_type, Map<String, String> map) {
                Log.i("tsy", "login onComplete:" + map);
            }

            @Override
            public void onError(PlatformType platform_type, String err_msg) {
                Log.i("tsy", "login onError:" + err_msg);
            }

            @Override
            public void onCancel(PlatformType platform_type) {
                Log.i("tsy", "login onCancel");
            }
        });
    }

    /**
     * 新浪微博登录
     */
    @OnClick(R.id.btnSinaWBLogin)
    public void onSinaWBLogin() {
        mSocialApi.doOauthVerify(this, PlatformType.SINA_WB, new AuthListener() {
            @Override
            public void onComplete(PlatformType platform_type, Map<String, String> map) {
                Log.i("tsy", "login onComplete:" + map);
            }

            @Override
            public void onError(PlatformType platform_type, String err_msg) {
                Log.i("tsy", "login onError:" + err_msg);
            }

            @Override
            public void onCancel(PlatformType platform_type) {
                Log.i("tsy", "login onCancel");
            }
        });
    }

    @OnClick(R.id.btnShare)
    public void onShare() {
        //获取分享类型
        IShareMedia shareMedia;
        switch (radioGShareMedia.getCheckedRadioButtonId()) {
            case R.id.radioShareText:
                shareMedia = new ShareTextMedia();
                ((ShareTextMedia)shareMedia).setText("分享文字测试");
                break;

            case R.id.radioShareImage:
                shareMedia = new ShareImageMedia();
                ((ShareImageMedia)shareMedia).setImage(BitmapUtils.readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
                break;

            case R.id.radioShareMusic:
                shareMedia = new ShareMusicMedia();
                ((ShareMusicMedia)shareMedia).setTitle("分享音乐测试");
                ((ShareMusicMedia)shareMedia).setDescription("分享音乐测试");
                ((ShareMusicMedia)shareMedia).setMusicUrl("http://tsy.tunnel.nibaguai.com/splash/music.mp3");
                ((ShareMusicMedia)shareMedia).setThumb(BitmapUtils.readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
                break;

            case R.id.radioShareVideo:
                shareMedia = new ShareVideoMedia();
                ((ShareVideoMedia)shareMedia).setTitle("分享视频测试");
                ((ShareVideoMedia)shareMedia).setDescription("分享视频测试");
                ((ShareVideoMedia)shareMedia).setVideoUrl("http://tsy.tunnel.nibaguai.com/splash/music.mp3");
                ((ShareVideoMedia)shareMedia).setThumb(BitmapUtils.readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
                break;

            case R.id.radioShareWeb:
                shareMedia = new ShareWebMedia();
                ((ShareWebMedia)shareMedia).setTitle("分享网页测试");
                ((ShareWebMedia)shareMedia).setDescription("分享网页测试");
                ((ShareWebMedia)shareMedia).setWebPageUrl("http://www.baidu.com");
                ((ShareWebMedia)shareMedia).setThumb(BitmapUtils.readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
                break;

            default:
                return;
        }

        //分享渠道
        switch (radioGSharePlatform.getCheckedRadioButtonId()) {
            case R.id.radioShareWX:
                mSocialApi.doShare(this, PlatformType.WEIXIN, shareMedia, new MyShareListener());
                break;

            case R.id.radioShareWXCircle:
                mSocialApi.doShare(this, PlatformType.WEIXIN_CIRCLE, shareMedia, new MyShareListener());
                break;

            case R.id.radioShareQQ:
                mSocialApi.doShare(this, PlatformType.QQ, shareMedia, new MyShareListener());
                break;

            case R.id.radioShareQZone:
                mSocialApi.doShare(this, PlatformType.QZONE, shareMedia, new MyShareListener());
                break;

            case R.id.radioShareSinaWB:
                mSocialApi.doShare(this, PlatformType.SINA_WB, shareMedia, new MyShareListener());
                break;

            default:
                return;
        }
    }

    public class MyShareListener implements ShareListener {

        @Override
        public void onComplete(PlatformType platform_type) {
            Log.i("tsy", "share onComplete");
        }

        @Override
        public void onError(PlatformType platform_type, String err_msg) {
            Log.i("tsy", "share onError:" + err_msg);
        }

        @Override
        public void onCancel(PlatformType platform_type) {
            Log.i("tsy", "share onCancel");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        ((SinaWBHandler)mSocialApi.getSSOHandler(PlatformType.SINA_WB)).onNewIntent(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        ((SinaWBHandler)mSocialApi.getSSOHandler(PlatformType.SINA_WB)).onResponse(baseResponse);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSocialApi.onActivityResult(requestCode, resultCode, data);
    }
}
