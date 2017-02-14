package com.tsy.socialsample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

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

import java.io.InputStream;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.radioGShareMedia)
    RadioGroup radioGShareMedia;

    @BindView(R.id.radioGSharePlatform)
    RadioGroup radioGSharePlatform;

    private static final String WX_APPID = "your wx appid";    //申请的wx appid
    private static final String QQ_APPID = "your qq appid";    //申请的qq appid
    private static final String SINA_WB_APPKEY = "your sina wb appkey";       //申请的新浪微博 appkey

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
    }

    /**
     * 微信登录
     */
    @OnClick(R.id.btnWXLogin)
    public void onWXLogin() {
        mSocialApi.doOauthVerify(this, PlatformType.WEIXIN , new MyAuthListener());
    }

    /**
     * qq登录
     */
    @OnClick(R.id.btnQQLogin)
    public void onQQLogin() {
        mSocialApi.doOauthVerify(this, PlatformType.QQ, new MyAuthListener());
    }

    /**
     * 新浪微博登录
     */
    @OnClick(R.id.btnSinaWBLogin)
    public void onSinaWBLogin() {
        mSocialApi.doOauthVerify(this, PlatformType.SINA_WB, new MyAuthListener());
    }

    public class MyAuthListener implements AuthListener {
        @Override
        public void onComplete(PlatformType platform_type, Map<String, String> map) {
            Toast.makeText(MainActivity.this, platform_type + " login onComplete", Toast.LENGTH_SHORT).show();
            Log.i("tsy", "login onComplete:" + map);
        }

        @Override
        public void onError(PlatformType platform_type, String err_msg) {
            Toast.makeText(MainActivity.this, platform_type + " login onError:" + err_msg, Toast.LENGTH_SHORT).show();
            Log.i("tsy", "login onError:" + err_msg);
        }

        @Override
        public void onCancel(PlatformType platform_type) {
            Toast.makeText(MainActivity.this, platform_type + " login onCancel", Toast.LENGTH_SHORT).show();
            Log.i("tsy", "login onCancel");
        }
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
                ((ShareImageMedia)shareMedia).setImage(readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
                break;

            case R.id.radioShareMusic:
                shareMedia = new ShareMusicMedia();
                ((ShareMusicMedia)shareMedia).setTitle("分享音乐测试");
                ((ShareMusicMedia)shareMedia).setDescription("分享音乐测试");
                ((ShareMusicMedia)shareMedia).setMusicUrl("http://tsy.tunnel.nibaguai.com/splash/music.mp3");
                ((ShareMusicMedia)shareMedia).setThumb(readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
                break;

            case R.id.radioShareVideo:
                shareMedia = new ShareVideoMedia();
                ((ShareVideoMedia)shareMedia).setTitle("分享视频测试");
                ((ShareVideoMedia)shareMedia).setDescription("分享视频测试");
                ((ShareVideoMedia)shareMedia).setVideoUrl("http://tsy.tunnel.nibaguai.com/splash/music.mp3");
                ((ShareVideoMedia)shareMedia).setThumb(readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
                break;

            case R.id.radioShareWeb:
                shareMedia = new ShareWebMedia();
                ((ShareWebMedia)shareMedia).setTitle("分享网页测试");
                ((ShareWebMedia)shareMedia).setDescription("分享网页测试");
                ((ShareWebMedia)shareMedia).setWebPageUrl("http://www.baidu.com");
                ((ShareWebMedia)shareMedia).setThumb(readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
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
            Toast.makeText(MainActivity.this, platform_type + " share onComplete", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(PlatformType platform_type, String err_msg) {
            Toast.makeText(MainActivity.this, platform_type + " share onError:" + err_msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(PlatformType platform_type) {
            Toast.makeText(MainActivity.this, platform_type + " share onCancel", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSocialApi.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
