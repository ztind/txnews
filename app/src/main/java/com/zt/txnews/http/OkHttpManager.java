package com.zt.txnews.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Author: ZT on 2017/2/23.
 * okHttp的封装工具类
 * 单列懒汉式
 */
public class OkHttpManager {

    //提交服务器的json数据格式
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    //提交字符串
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");

    private Handler handler;
    private OkHttpClient okHttpClient;
    private volatile static OkHttpManager mOkHttpManager;

    private OkHttpManager(){
        okHttpClient = new OkHttpClient();
        handler = new Handler(Looper.getMainLooper());
    }
    public static OkHttpManager getInstance(){
        OkHttpManager instance=null;
            if(mOkHttpManager==null){
                synchronized (OkHttpManager.class){
                    if(instance==null){
                        instance = new OkHttpManager();
                        mOkHttpManager = instance;
                    }
                }
            }
        return mOkHttpManager;
    }
    /**
     * 定义接口，实现方法回调和数据回传
     */
    public interface Func1{
        void onResponse(String jsonString);

        void onFailure(IOException exception);
    }
    public interface Func2{
        void onResponse(JSONObject jsonObject);

        void onFailure(IOException exception);
    }
    public interface Func3{
        void onResponse(byte[] data);

        void onFailure(IOException exception);
    }
    public interface Func4{
        void onResponse(Bitmap bitmap);

        void onFailure(IOException exception);
    }
    //请求返回json字符串
    private void onSuccessResultJsonStringMethod(final String jsonValue,final Func1 callBack){
        //handler.post()运行于主线程
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(callBack!=null){
                    callBack.onResponse(jsonValue);//回调方法
                }
            }
        });
    }
    //请求返回json数组
    private void onSuccessResultJsonObjectMethod(final JSONObject jsonObject,final Func2 callBack){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(callBack!=null){
                    callBack.onResponse(jsonObject);
                }
            }
        });
    }
    //请求返回byte[]字节数组
    private void onSuccessResultByteArrayMethod(final byte[] data,final Func3 callBack){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(callBack!=null){
                    callBack.onResponse(data);
                }
            }
        });
    }
    //请求返回Bitmap对象
    private void onSuccessResultBitmapMethod(final Bitmap bitmap,final Func4 callBack){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(callBack!=null){
                    callBack.onResponse(bitmap);
                }
            }
        });
    }

    /**
     * 同步的网络请求,android不常使用，会阻塞UI线程
     */
    public String syncGetStringByURL(String url){
        ///构建一个request请求对象
        Request request = new Request.Builder().url(url).get().build();
        try {
            Response response = okHttpClient.newCall(request).execute();//execute()同步请求网络
            if(response!=null && response.isSuccessful()){
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *异步的网络请求,下载数据
     */
    public void asyncJsonStringByURL(String url, final Func1 callBack){
        Request request = new Request.Builder().url(url).get().build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(callBack!=null){
                            callBack.onFailure(e);//回调
                        }
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response!=null && response.isSuccessful()){
                    onSuccessResultJsonStringMethod(response.body().string(),callBack);//回调
                }
            }
        });
    }
    public void asyncJsonObjectByURL(String url, final Func2 callBack){
        Request request = new Request.Builder().url(url).get().build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(callBack!=null){
                            callBack.onFailure(e);//回调
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response!=null && response.isSuccessful()){
                    String string = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(string);
                        onSuccessResultJsonObjectMethod(jsonObject,callBack);//回调
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void asyncByteArrayByURL(String url, final Func3 callBack){
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(callBack!=null){
                            callBack.onFailure(e);//回调
                        }
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response!=null && response.isSuccessful()){
                    byte[] data = response.body().bytes();
                    onSuccessResultByteArrayMethod(data,callBack);
                }
            }
        });
    }
    public void asyncBitmapByURL(String url, final Func4 callBack){
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(callBack!=null){
                            callBack.onFailure(e);//回调
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    if(response!=null && response.isSuccessful()){
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        onSuccessResultBitmapMethod(bitmap,callBack);
                    }
            }
        });
    }
    /**
     * 提交表单数据至服务器 post方式
     * 提交的数据封装到FormBody里
     */
    public void sendFormDataToService(String url, Map<String,String> map, final Func2 callBack){
        FormBody.Builder formBody_builder = new FormBody.Builder();
        if(map!=null && !map.isEmpty()){
            for (Map.Entry<String,String> entry : map.entrySet()){
                formBody_builder.addEncoded(entry.getKey(), entry.getValue());
            }
            //构建FormBody
            FormBody formBuilder = formBody_builder.build();
            //构建Request
            Request request = new Request.Builder().url(url).post(formBuilder).build();
            //异步上传数据
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(final Call call, final IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(callBack!=null){
                                callBack.onFailure(e);
                            }
                        }
                    });
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //接收服务器端返回的jsonobject
                    if(response!=null && response.isSuccessful()){
                        String string = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(string);
                            onSuccessResultJsonObjectMethod(jsonObject,callBack);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
    /**
     * 提交字符串数据到服务器端
     */
    public  void sendStringToService(String url, String content, final Func2 callback){
        Request request = new Request.Builder().url(url).post(RequestBody.create(MEDIA_TYPE_MARKDOWN, content)).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(callback!=null){
                            callback.onFailure(e);
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    //返回jsonobject
                if(response!=null && response.isSuccessful()){
                    String result = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        onSuccessResultJsonObjectMethod(jsonObject,callback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
