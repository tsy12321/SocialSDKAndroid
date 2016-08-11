# SocialSDKAndroid
对第三方社会化sdk的集成和二次封装，比如第三方授权登录、第三方分享等

## 1 版本更新

|版本号|更新内容|
|---|---|
|1.0|集成微信授权登录,微信网页分享,朋友圈网页分享|

## 2 功能介绍

### 2.1 授权登录

1. 微信授权登录

### 2.2 分享

1. 微信网页分享
1. 朋友圈网页分享

### 3 集成说明

将social_sdk目录移到项目中,并在setting.gradle和项目的build.gradle中引用

### 4 开发说明

#### 4.1 初始化配置

初始各个平台的配置信息.

```java
PlatformConfig.setWeixin(WX_APPID, WX_APPSECRET);
```

#### 4.2 登录授权

先获取ShareApi

```java
SocialApi api = SocialApi.get(this);
```

调用授权接口,选择授权平台,设置授权回调监听.

```java
api.doOauthVerify(this, PlatformType.WEIXIN, new AuthListener() {
    @Override
    public void onComplete(PlatformType platform_type, Map<String, String> map) {
        Log.i("tsy", "oncomplete:" + map);
    }

    @Override
    public void onError(PlatformType platform_type, String err_msg) {
        Log.i("tsy", "onError:" + err_msg);
    }

    @Override
    public void onCancel(PlatformType platform_type) {
        Log.i("tsy", "onCancel");
    }
});
```

##### 4.2.1 微信登录授权

需要添加指定activity:.wxapi.WXEntryActivity

并在AndroidManfiest注册:

```xml
<activity
   android:name=".wxapi.WXEntryActivity"
   android:theme="@android:style/Theme.Translucent.NoTitleBar"
   android:configChanges="keyboardHidden|orientation|screenSize"
   android:exported="true"
   android:screenOrientation="portrait" />
```

然后WXEntryActivity直接继承WXCallbackActivity类即可

```java
import com.ci123.sdk.social.weixin.WXCallbackActivity;

/**
 * Created by tsy on 16/8/4.
 */
public class WXEntryActivity extends WXCallbackActivity {

}

```