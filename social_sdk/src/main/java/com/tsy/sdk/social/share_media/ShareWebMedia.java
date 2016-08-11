package com.tsy.sdk.social.share_media;

import android.graphics.Bitmap;

/**
 * 网页分享 实体类
 * Created by tsy on 16/8/5.
 */
public class ShareWebMedia implements IShareMedia {

    private String mWebPageUrl;     //待分享的网页url
    private String mTitle;          //网页标题
    private String mDescription;    //网页描述
    private Bitmap mThumb;          //网页缩略图

    public String getWebPageUrl() {
        return mWebPageUrl;
    }

    public void setWebPageUrl(String webPageUrl) {
        mWebPageUrl = webPageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Bitmap getThumb() {
        return mThumb;
    }

    public void setThumb(Bitmap thumb) {
        mThumb = thumb;
    }
}
