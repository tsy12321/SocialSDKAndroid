package com.tsy.sdk.social.share_media;

import android.graphics.Bitmap;

/**
 * 音乐分享 实体类
 * Created by tsy on 16/8/11.
 */
public class ShareMusicMedia implements IShareMedia {
    private String mMusicUrl;       //音乐url
    private String mTitle;          //标题
    private String mDescription;    //描述
    private Bitmap mThumb;          //缩略图

    public String getMusicUrl() {
        return mMusicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        mMusicUrl = musicUrl;
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
