package com.tsy.sdk.social.share_media;

import android.graphics.Bitmap;

/**
 * 图片分享 实体类
 * Created by tsy on 16/8/11.
 */
public class ShareImageMedia implements IShareMedia {
    private Bitmap mImage;

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap image) {
        mImage = image;
    }
}
