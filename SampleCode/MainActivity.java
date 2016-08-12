
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.radioGShareMedia)
    RadioGroup radioGShareMedia;

    @BindView(R.id.radioGSharePlatform)
    RadioGroup radioGSharePlatform;

    private static final String WX_APPID = "";    //申请的wx appid
    private static final String WX_APPSECRET = "";      //申请的wx appsecret

    private SocialApi mSocialApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        PlatformConfig.setWeixin(WX_APPID, WX_APPSECRET);
        mSocialApi = SocialApi.get(this);
    }

    /**
     * 微信登录
     */
    @OnClick(R.id.btnWXLogin)
    public void onWXLogin() {
        PlatformConfig.setWeixin(WX_APPID, WX_APPSECRET);
        SocialApi api = SocialApi.get(this);
        api.doOauthVerify(this, PlatformType.WEIXIN, new AuthListener() {
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
                ((ShareMusicMedia)shareMedia).setMusicUrl("http://idg-tangsiyuan.tunnel.nibaguai.com/splash/music.mp3");
                ((ShareMusicMedia)shareMedia).setThumb(BitmapUtils.readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
                break;

            case R.id.radioShareVideo:
                shareMedia = new ShareVideoMedia();
                ((ShareVideoMedia)shareMedia).setTitle("分享视频测试");
                ((ShareVideoMedia)shareMedia).setDescription("分享视频测试");
                ((ShareVideoMedia)shareMedia).setVideoUrl("http://idg-tangsiyuan.tunnel.nibaguai.com/splash/music.mp3");
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
                doShareWX(shareMedia);
                break;

            case R.id.radioShareWXCircle:
                doShareWXCircle(shareMedia);
                break;

            default:
                return;
        }
    }

    //微信分享
    public void doShareWX(IShareMedia shareMedia) {
        mSocialApi.doShare(this, PlatformType.WEIXIN, shareMedia, new ShareListener() {
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
        });
    }

    //朋友圈分享
    public void doShareWXCircle(IShareMedia shareMedia) {
        mSocialApi.doShare(this, PlatformType.WEIXIN_CIRCLE, shareMedia, new ShareListener() {
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
        });
    }
}
