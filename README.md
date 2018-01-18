# SocialSDKAndroid

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

> 对第三方社会化sdk的集成和二次封装，比如第三方授权登录、第三方分享等. 欢迎发issue建议新的功能点和渠道集成

原文阅读:

[http://www.jianshu.com/p/4ec1d9c15763](http://www.jianshu.com/p/4ec1d9c15763)


## 0 版本更新记录

[版本更新记录](https://github.com/tsy12321/SocialSDKAndroid/blob/master/CHANGELOG.md)

## 1 引用

### 第一种方式：三个平台包全部包含的Library

在项目级别的 `build-gradle` 中添加

```groovy
allprojects {
    repositories {
        maven { url "https://dl.bintray.com/thelasterstar/maven" }      //微博sdk maven库
    }
}
```

在app级别的 `build-gradle` 中添加

```groovy
dependencies {
    compile 'com.tsy.social:social-sdk-full:2.0.0'
}
```

### 第二种方式：引入核心包，然后自由选择需要的平台SDK

在项目的 `build-gradle` 中添加

```groovy
dependencies {
    compile 'com.tsy.social:social-sdk-core:2.0.0'
    //social sdk 自由选择
    //微信sdk
//    compile 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:1.4.0' 
    //QQ SDK
//    compile files('libs/qq_mta-sdk-1.6.2.jar')
//    compile files('libs/qq_sdk_v3.3.0_lite.jar')
    //微博SDK
//    compile 'com.sina.weibo.sdk:core:1.0.0:openDefaultRelease@aar'
}
```

如果使用了微博SDK，需要在项目级别的 `build-gradle` 中添加

```groovy
allprojects {
    repositories {
        maven { url "https://dl.bintray.com/thelasterstar/maven" }      //微博sdk maven库
    }
}
```
这种方式可以减少sdk的体积,需要什么平台就引入哪个平台.更为合理.

**注意：该方式在sync时可能会报引用错误（如果没有使用微博的SDK），可以忽略**

### 1.1 目录介绍

- app/ Demo代码
- social_sdk/ sdk的开发源码module 开发完成后用gradle中makejar打成jar包
- qq_sdk/ qq sdk

### 1.2 Demo介绍

替换Demo中的MainActivity中的qq appid、wx appid、weibo appkey为自己的

```java
public class MainActivity extends AppCompatActivity implements IWeiboHandler.Response{

    ...

    private static final String WX_APPID = "your wx appid";    //申请的wx appid
    private static final String QQ_APPID = "your qq appid";    //申请的qq appid
    private static final String SINA_WB_APPKEY = "your sina wb appkey";       //申请的新浪微博 appkey

    ...
}
```

替换AndroidManifest中的qq appid为自己的

```java
<!--qq-->
<activity
    android:name="com.tencent.tauth.AuthActivity"
    android:noHistory="true"
    android:launchMode="singleTask" >
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="tencent1111111" /> <!--1111111改为你的qq appid-->
    </intent-filter>
</activity>
```

替换builde.gradle文件中的签名为自己的app签名。

修改完上面3个地方后即可跑通Demo。如下

![Demo](https://github.com/tsy12321/SocialSDKAndroid/blob/master/Demo.jpg)

## 2 功能介绍

### 2.1 授权登录

1. 微信授权登录
2. QQ授权登录
3. 新浪微博授权登录

### 2.2 分享

#### 2.2.1 分享媒介

1. 文字
1. 图片
1. 音乐
1. 视频
1. 网页

#### 2.2.2 分享平台

1. 微信会话分享
1. 微信朋友圈分享
1. QQ分享
1. QQ空间分享
1. 新浪微博分享

### 2.3 API调用

SDK中封装了部分API，比如当第三方授权成功后获取到access_token，会提供一些API查询用户信息，刷新access_token等。

**！注意：由于调用这些API会用到appsecret，所以一般不建议放到客户端做**

具体API见底附录。


## 3 开发说明

### 3.1 准备

将social_sdk.jar和需要的平台sdk放入项目中引用.

AndroidManifest加上以下基本的权限(之后各个平台会注册一些不同的信息后面会说明)

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
```

### 3.2 配置平台信息

在项目入口(或者在调用前)需要配置平台的信息,配置一次即可.

```java
PlatformConfig.setWeixin(WX_APPID);
PlatformConfig.setQQ(QQ_APPID);
PlatformConfig.setSinaWB(SINA_WB_APPKEY);
```

### 3.3 接口使用说明

调用方式使用api调用登录或者分享接口,在参数中区别平台,实现回调接收成功 取消或者失败的结果.

示例如下:(某些平台会有一些特殊处理后面会在平台中说明)

初始化api:

```java
SocialApi mSocialApi = SocialApi.get(getApplicationContext());
```

登录授权:
```java
mSocialApi.doOauthVerify(this, PlatformType.WEIXIN, new AuthListener() {
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

分享:
```java

//分享媒介 后面有详细介绍
ShareWebMedia shareMedia = new ShareWebMedia();
shareMedia.setTitle("分享网页测试");
shareMedia.setDescription("分享网页测试");
shareMedia.setWebPageUrl("http://www.baidu.com");
shareMedia.setThumb(BitmapUtils.readBitMap(getApplicationContext(), R.mipmap.ic_launcher));

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

```

### 3.4 分享媒介

现在集成了文字分享,图片分享,音乐分享,视频分享,网页分享5种分享媒介.不同的平台可能只有其中某几种.

#### 3.4.1 文字分享

```java
ShareTextMedia shareMedia = new ShareTextMedia();
shareMedia.setText("分享文字测试");
```
#### 3.4.2 图片分享

```java
ShareImageMedia shareMedia = new ShareImageMedia();
shareMedia.setImage(BitmapUtils.readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
```

#### 3.4.3 音乐分享

```java
ShareMusicMedia shareMedia = new ShareMusicMedia();
shareMedia.setTitle("分享音乐测试");
shareMedia.setDescription("分享音乐测试");
shareMedia.setMusicUrl("http://idg-tangsiyuan.tunnel.nibaguai.com/splash/music.mp3");
shareMedia.setThumb(BitmapUtils.readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
```

#### 3.4.4 视频分享

```java
ShareVideoMedia shareMedia = new ShareVideoMedia();
shareMedia.setTitle("分享视频测试");
shareMedia.setDescription("分享视频测试");
shareMedia.setVideoUrl("http://idg-tangsiyuan.tunnel.nibaguai.com/splash/music.mp3");
shareMedia.setThumb(BitmapUtils.readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
```

#### 3.4.5 网页分享

```java
ShareWebMedia shareMedia = new ShareWebMedia();
shareMedia.setTitle("分享网页测试");
shareMedia.setDescription("分享网页测试");
shareMedia.setWebPageUrl("http://www.baidu.com");
shareMedia.setThumb(BitmapUtils.readBitMap(getApplicationContext(), R.mipmap.ic_launcher));
```

## 4 第三方平台接入

### 4.1 微信

#### 4.1.1 集成sdk

```
compile 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:1.4.0'
```

#### 4.1.2 配置

AndroidManifest中添加:

```java
<activity
    android:name="com.tsy.sdk.social.weixin.WXCallbackActivity"
    android:configChanges="orientation|keyboardHidden|navigation|screenSize"
    android:exported="true"
    android:launchMode="singleTop"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
<activity-alias
    android:name=".wxapi.WXEntryActivity"
    android:exported="true"
    android:targetActivity="com.tsy.sdk.social.weixin.WXCallbackActivity" />
```

#### 4.1.3 常量定义

设置配置信息:

```java
PlatformConfig.setWeixin(WX_APPID);
```

PlatformType:

微信:PlatformType.WEIXIN(可用于登录和微信回话分享)

朋友圈:PlatformType.WEIXIN_CIRCLE(用于微信朋友圈分享)


#### 4.1.4 自定义scope state

可以通过接口setScopeState修改 scope 和 state。

默认scope是 "snsapi_userinfo,snsapi_friend,snsapi_message"
默认state是 "none"

```java
WXHandler.setScopeState("your scope", "your state");
```

#### 4.1.5 注意

使用微信登录分享需要签名打包，并且签名和包名要和微信平台填入的信息一致。


### 4.2 QQ

#### 4.2.1 集成sdk

将目录中的qq_mta-sdk-1.6.2.jar和qq_sdk_v3.3.0_lite.jar放入项目.

#### 4.2.2 配置

AndroidManifest中添加:

```java
<!--qq-->
<activity
    android:name="com.tencent.tauth.AuthActivity"
    android:noHistory="true"
    android:launchMode="singleTask" >
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="tencent你的appid" />
    </intent-filter>
</activity>
<activity
    android:name="com.tencent.connect.common.AssistActivity"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

#### 4.2.3 常量定义

设置配置信息:

```java
PlatformConfig.setQQ(QQ_APPID);
```

PlatformType:

微信:PlatformType.QQ(可用于登录和QQ分享)

朋友圈:PlatformType.QZONE(用于qq控件分享)

#### 4.2.4 注意

使用QQ登录需要签名打包，并且签名和包名要和QQ开放平台填入的信息一致。

#### 4.2.5 回调设置

要在onActivityResult添加以下

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    mSocialApi.onActivityResult(requestCode, resultCode, data);
}
```

### 4.3 新浪微博

#### 4.3.1 集成sdk

在项目根目录的build.gradle中增加中央仓库

```
maven { url "https://dl.bintray.com/thelasterstar/maven/" }
```

在需要引入SDK的module目录的build.gradle中引入sdk-core依赖

```
compile 'com.sina.weibo.sdk:core:1.0.0:openDefaultRelease@aar'
```

#### 4.3.2 配置

创建文件 `WBShareActivity` 继承 `WBShareCallbackActivity`

```java
public class WBShareActivity extends WBShareCallbackActivity {
}
```

AndroidManifest中添加:

```java
<!--微博-->
<activity
    android:name="com.sina.weibo.sdk.component.WeiboSdkBrowser"
    android:configChanges="keyboardHidden|orientation"
    android:exported="false"
    android:windowSoftInputMode="adjustResize"></activity>
<activity
    android:name=".WBShareActivity"
    android:configChanges="keyboardHidden|orientation"
    android:screenOrientation="portrait" >
    <intent-filter>
        <action android:name="com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

#### 4.3.3 回调设置

要在onActivityResult添加以下(如果qq已经添加则不需要重复添加)

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    mSocialApi.onActivityResult(requestCode, resultCode, data);
}
```

#### 4.3.4 常量定义

设置配置信息:

```java
PlatformConfig.setSinaWB(SINA_WB_APPKEY);
```

#### 4.3.5 自定义REDIRECT_URL

可以通过接口setRedirctUrl修改 REDIRECT_URL

默认REDIRECT_URL是 "https://api.weibo.com/oauth2/default.html"

```java
SinaWBHandler.setRedirctUrl("your RedirctUrl");
```

#### 4.3.6 注意

使用新浪登录分享需要签名打包，并且签名和包名要和新浪平台填入的信息一致。

并且微博开放平台的回调地址（REDIRECT_URL）要和代码中的REDIRECT_URL一致

## 附录

### API列表

#### 1 微信API (WXApi)

1. 获取access_token

   ```java
	/**
	 * 获取access_token
	 * @param wxAppId wx appid
	 * @param wxAppSecret wx appsecret
	 * @param code 调用微信登录获取的code
	 * @param callback
	 */
   ```
    getAccessToken(String wxAppId, String wxAppSecret,
                                      String code,
                                      final Callback callback)

1. 获取用户信息

   ```java
   /**
     * 获取用户信息
     * @param openid openid
     * @param access_token access_token
     * @param callback
     */
   ```
    getUserInfo(String openid, String access_token,
                                       final Callback callback)

## 混淆

```
#social
-dontwarn com.tsy.sdk.social.**
-keep class com.tsy.sdk.social.**{*;}

#qq & weixin
-dontwarn  com.tencent.**
-keep class com.tencent.** {*;}

#sina
-dontwarn  com.sina.**
-keep class com.sina.** {*;}
```

## About Me
简书地址：http://www.jianshu.com/users/21716b19302d/latest_articles

微信公众号

![我的公众号](https://github.com/tsy12321/PayAndroid/blob/master/wxmp_avatar.jpg)

License
-------

    Copyright 2017 SY.Tang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
