package com.tsy.sdk.social.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Bitmap相关工具类
 * Created by tsy on 16/8/5.
 */
public class BitmapUtils {

    /**
     * Bitmap保存为文件
     * @param bitmap
     * @param path
     * @return
     */
    public static File saveBitmapFile(Bitmap bitmap, String path) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new File(path);
    }

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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                if(byteArrayOutputStream.toByteArray() == null) {
                    LogUtils.e("BitmapUtils", "bitmap2Bytes byteArrayOutputStream toByteArray=null");
                }
                return byteArrayOutputStream.toByteArray();
            } catch (Exception e) {
                LogUtils.e("BitmapUtils", e.toString());
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
            LogUtils.e("BitmapUtils", "bitmap2Bytes bitmap == null or bitmap.isRecycled()");
            return null;
        }
    }

    /**
     * 压缩图片
     * 在保证质量的情况下尽可能压缩 不保证压缩到指定字节
     * @param datas
     * @param byteCount 指定压缩字节数
     * @return
     */
    public static byte[] compressBitmap(byte[] datas, int byteCount) {
        boolean isFinish = false;
        if(datas != null && datas.length > byteCount) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap tmpBitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
            int times = 1;
            double percentage = 1.0D;

            while(!isFinish && times <= 10) {
                percentage = Math.pow(0.8D, (double)times);
                int compress_datas = (int)(100.0D * percentage);
                tmpBitmap.compress(Bitmap.CompressFormat.JPEG, compress_datas, outputStream);
                if(outputStream != null && outputStream.size() < byteCount) {
                    isFinish = true;
                } else {
                    outputStream.reset();
                    ++times;
                }
            }

            if(outputStream != null) {
                byte[] outputStreamByte = outputStream.toByteArray();
                if(!tmpBitmap.isRecycled()) {
                    tmpBitmap.recycle();
                }

                if(outputStreamByte.length > byteCount) {
                    LogUtils.e("BitmapUtils", "compressBitmap cannot compress to " + byteCount + ", after compress size=" + outputStreamByte.length);
                }

                return outputStreamByte;
            }
        }

        return datas;
    }
}
