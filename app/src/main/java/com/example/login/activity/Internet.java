package com.example.login.activity;

import android.content.Context;
import android.util.Log;

import com.example.login.Data.CokAndMqt;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class Internet {
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    public static Response postResponse(String url,String data) {
        try {
            OkHttpClient client = new OkHttpClient.Builder().build();
////                        Log.d("数据", Save.getCookiePreference(MainActivity.this));

            RequestBody rb =RequestBody.create(JSON,data);
//                        //添加一些数据来验证是否成功
            Request request = new Request.Builder().post(rb).url(url).method("POST",rb).build();
            //添加请求体，然后来访问
            Response response = client.newCall(request).execute();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Response getResponse(String url,CokAndMqt cokAndMqt){
        try {
            OkHttpClient client = new OkHttpClient.Builder().build();

            Request request = new Request.Builder().url(url).addHeader("Cookie", "m_id="+cokAndMqt.getM_id()+";u_id="+cokAndMqt.getU_id()).
                    build();
            //添加请求体，然后来访问
            Log.d("请求头", request.toString());
            Response response = client.newCall(request).execute();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}