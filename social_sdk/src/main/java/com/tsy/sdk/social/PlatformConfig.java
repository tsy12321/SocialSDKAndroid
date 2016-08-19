package com.tsy.sdk.social;

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
        configs.put(PlatformType.QQ, new PlatformConfig.QQ(PlatformType.QQ));
        configs.put(PlatformType.QZONE, new PlatformConfig.QQ(PlatformType.QZONE));
    }

    public interface Platform {
        PlatformType getName();
    }

    //微信
    public static class Weixin implements PlatformConfig.Platform {
        private final PlatformType media;
        public String appId = null;
        public String appSecret = null;

        public PlatformType getName() {
            return this.media;
        }

        public Weixin(PlatformType type) {
            this.media = type;
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

    //qq
    public static class QQ implements PlatformConfig.Platform {
        private final PlatformType media;
        public String appId = null;

        public PlatformType getName() {
            return this.media;
        }

        public QQ(PlatformType type) {
            this.media = type;
        }
    }

    /**
     * 设置qq配置信息
     * @param appId
     */
    public static void setQQ(String appId) {
        PlatformConfig.QQ qq = (PlatformConfig.QQ)configs.get(PlatformType.QQ);
        qq.appId = appId;

        PlatformConfig.QQ qzone = (PlatformConfig.QQ)configs.get(PlatformType.QZONE);
        qzone.appId = appId;
    }

    public static Platform getPlatformConfig(PlatformType platformType) {
        return configs.get(platformType);
    }
}
