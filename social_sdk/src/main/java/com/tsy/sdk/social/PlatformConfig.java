package com.tsy.sdk.social;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方平台配置信息存储
 * Created by tsy on 16/8/4.
 */
public class PlatformConfig {

    public static Map<PlatformType, PlatformConfig.Platform> configs = new HashMap();

    static {
        configs.put(PlatformType.WEIXIN, new PlatformConfig.Weixin(PlatformType.WEIXIN));
        configs.put(PlatformType.WEIXIN_CIRCLE, new PlatformConfig.Weixin(PlatformType.WEIXIN_CIRCLE));
    }

    public interface Platform {
        PlatformType getName();
        boolean isConfigured();
    }

    //微信
    public static class Weixin implements PlatformConfig.Platform {
        private final PlatformType media;
        public String appId = null;
        public String appSecret = null;

        public PlatformType getName() {
            return this.media;
        }

        public Weixin(PlatformType var1) {
            this.media = var1;
        }

        public boolean isConfigured() {
            return !TextUtils.isEmpty(this.appId) && !TextUtils.isEmpty(this.appSecret);
        }
    }

    /**
     * 设置微信配置信息
     * @param appId
     * @param appSecret
     */
    public static void setWeixin(String appId, String appSecret) {
        PlatformConfig.Weixin weixin = (PlatformConfig.Weixin)configs.get(PlatformType.WEIXIN);
        weixin.appId = appId;
        weixin.appSecret = appSecret;

        PlatformConfig.Weixin weixin_circle = (PlatformConfig.Weixin)configs.get(PlatformType.WEIXIN_CIRCLE);
        weixin_circle.appId = appId;
        weixin_circle.appSecret = appSecret;
    }

    public static Platform getPlatformConfig(PlatformType platformType) {
        return configs.get(platformType);
    }
}
