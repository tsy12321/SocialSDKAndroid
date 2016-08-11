package com.tsy.sdk.social.util;

import android.accounts.NetworkErrorException;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tsy on 16/8/4.
 */
public class NetUtils {
    /**
     * 请求结果回调接口
     */
    public interface HttpResponseCallBack {
        void onSuccess(JSONObject response);    //返回数据
        void onFailure();   //网络连接错误 json解析错误
    }

    /**
     * 异步传输post请求 仅文本参数
     * @param url 请求地址
     * @param params 请求参数
     * @param callback 请求回调
     */
    public static void doPost(final String url, final Map<String, String> params, final HttpResponseCallBack callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = post(url, params, null);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response == null) {
                            callback.onFailure();
                            return;
                        }
                        try {
                            JSONObject json = new JSONObject(response);
                            callback.onSuccess(json);
                        } catch (JSONException e) {
                            callback.onFailure();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 异步传输post请求 文本 文件混合参数
     * @param url 请求地址
     * @param params 文本参数
     * @param file 上传文件参数
     * @param callback 请求回调
     */
    public static void doPost(final String url, final Map<String, String> params, final Map<String, String> file, final HttpResponseCallBack callback) {
        //CILogUtils.i("post url=" + url + " params=" + params + " file:" + file);
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = post(url, params, file);
                //CILogUtils.i("post response=" + response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response == null) {
                            callback.onFailure();
                            return;
                        }
                        try {
                            JSONObject json = new JSONObject(response);
                            callback.onSuccess(json);
                        } catch (JSONException e) {
                            callback.onFailure();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 异步传输get请求
     * @param url 请求url
     * @param callback 请求回调
     */
    public static void doGet(final String url, final HttpResponseCallBack callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = get(url);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(response == null) {
                            callback.onFailure();
                            return;
                        }
                        try {
                            JSONObject json = new JSONObject(response);
                            callback.onSuccess(json);
                        } catch (JSONException e) {
                            callback.onFailure();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 使用HttpURLConnection进行post请求
     * @param url 请求地址
     * @param params 请求参数 key-val
     * @param files 上传的文件 key-file path
     * @return 请求返回值
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static String post(String url, Map<String, String> params, Map<String, String> files) {
        HttpURLConnection conn = null;

        //http content 头尾等信息
        String PREFIX = "--";       //前缀
        String BOUNDARY =  "*****"+Long.toString(System.currentTimeMillis())+"*****";   //自定义分割串
        String END = "\r\n";    //结束

        try {
            //创建连接
            URL mURL = new URL(url);    // 创建一个URL对象
            conn = (HttpURLConnection) mURL.openConnection();   // 调用URL的openConnection()方法,获取HttpURLConnection对象

            conn.setReadTimeout(5000);// 设置读取超时为5秒
            conn.setConnectTimeout(10000);// 设置连接网络超时为10秒

            // 允许Input、Output，不使用Cache
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setRequestMethod("POST");// 设置请求方法为post
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());

            //post文件
            if(files != null && !files.isEmpty()) {
                Iterator<Map.Entry<String, String>> iterator = files.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> param = iterator.next();
                    String file_param_name = param.getKey();        //参数名
                    String file_path = param.getValue();        //路径
                    String filename = file_path.substring(file_path.lastIndexOf("/") + 1);  //文件名

                    File file = new File(file_path);
                    if(file == null || !file.exists()) {
                        //CILogUtils.e("file not exist:" + file);
                        continue;
                    }

                    //头部
                    StringBuilder sb = new StringBuilder();
                    sb.append(PREFIX + BOUNDARY + END);
                    sb.append("Content-Disposition: form-data; name=" + "\"" + file_param_name +
                            "\";filename=\"" + filename + "\"" + END);
                    sb.append("Content-Type: application/octet-stream; charset=UTF-8" + END);
                    sb.append(END);
                    outputStream.writeBytes(sb.toString());

                    // 取得文件的FileInputStream
                    FileInputStream fis = new FileInputStream(file_path);
                    byte[] buffer = new byte[1024];
                    int length = -1;
                    // 从文件读取数据至缓冲区
                    while ((length = fis.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                    fis.close();

                    outputStream.writeBytes(END);
                }
            }

            // post文本参数
            if(params != null && !params.isEmpty()) {
                Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> param = iterator.next();
                    String key = param.getKey();
                    String value = param.getValue();

                    if(key == null || value == null) {
                        continue;
                    }
                    outputStream.writeBytes(PREFIX + BOUNDARY + END);
                    outputStream.writeBytes("Content-Disposition: form-data; name=" + "\"" + key + "\"" + END);
                    outputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + END);
                    outputStream.writeBytes(END);
                    outputStream.write(value.getBytes("UTF-8"));
                    outputStream.writeBytes(END);
                }
            }

            outputStream.writeBytes(PREFIX + BOUNDARY + END);
            outputStream.flush();
            outputStream.close();

            //请求返回
            int responseCode = conn.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();     //获得输入流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));      //包装字节流为字符流
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                if(is != null) {
                    is.close();
                }

                return response.toString();
            } else {
                throw new NetworkErrorException("response status is " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();  // 关闭连接
            }
        }
    }

    /**
     * 使用HttpURLConnection进行get请求
     * @param url 请求地址
     * @return 请求返回值
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static String get(String url) {
        HttpURLConnection conn = null;
        try {
            //创建连接
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(10000);

            //请求返回
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();     //获得输入流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));      //包装字节流为字符流
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                if(is != null) {
                    is.close();
                }

                return response.toString();
            } else {
                throw new NetworkErrorException("response status is " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();  // 关闭连接
            }
        }
    }
}
