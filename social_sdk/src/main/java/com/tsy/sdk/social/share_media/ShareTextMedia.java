package com.tsy.sdk.social.share_media;

/**
 * 文字分享 实体类
 * Created by tsy on 16/8/11.
 */
public class ShareTextMedia implements IShareMedia {
    private String mText;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }
}
