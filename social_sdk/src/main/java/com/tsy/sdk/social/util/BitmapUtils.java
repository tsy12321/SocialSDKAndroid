package com.tsy.sdk.social.util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by tsy on 16/8/5.
 */
public class BitmapUtils {

    /**
     * Bitmap 转 bytes
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        if(bitmap != null && !bitmap.isRecycled()) {
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                int size = bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                int quality = 100;
                if((float)size > 3072.0F) {
                    quality = (int)(3072.0F / (float)size * (float)quality);
                }

                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                byte[] temp = byteArrayOutputStream.toByteArray();
                byte[] bytearr = temp;
                return bytearr;
            } catch (Exception e) {
                LogUtils.e("BitmapUtils-" + e.toString());
            } finally {
                if(byteArrayOutputStream != null) {
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException var14) {
                        ;
                    }
                }
            }

            return null;
        } else {
            LogUtils.e("BitmapUtils-" + "bitmap2Bytes  ==> bitmap == null or bitmap.isRecycled()");
            return null;
        }
    }
}
