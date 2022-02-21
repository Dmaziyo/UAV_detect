package com.example.login.utils;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.login.R;
import com.example.login.UI.MainActivity;
import com.example.login.activity.Internet;
import com.example.login.activity.PreferenceManager;

import java.io.Reader;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class INTO extends AppCompatActivity  implements View.OnClickListener {
    private String name;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setEnterTransition(new Fade().setDuration(1000));
        getWindow().setExitTransition(new Fade().setDuration(1000));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_into);
        ActionBar act = getSupportActionBar();//隐藏标题
        act.hide();//隐藏标题
        Button back = (Button) findViewById(R.id.into_back);
        Button get=(Button)findViewById(R.id.data);
        back.setOnClickListener(this);
        get.setOnClickListener(this);

//        Response response = Internet.getResponse();
//        List<String> ls=response.headers("Set-Cookie");
//        Toast.makeText(this, ls.get(0), Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.into_back:
                PreferenceManager.restartPreference(this);
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                Toast.makeText(this, "登出成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.data:
                sendRequestWithHttpURLConnection();
        }
    }

    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String SessionID = PreferenceManager.getCookiePreference(INTO.this);
                    Log.d("数据", SessionID);
                    OkHttpClient client = new OkHttpClient.Builder().build();
                    Request request = new Request.Builder().url("http://172.17.19.105:5000/").build();
                    Response response = client.newCall(request).execute();
                    ResponseBody responseBody = response.body();
                    Properties properties=new Properties();
                    Reader reader=responseBody.charStream();
                    properties.load(reader);
                    Log.d("数据", properties.getProperty("username"));;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}