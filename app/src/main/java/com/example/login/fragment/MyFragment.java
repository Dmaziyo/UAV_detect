package com.example.login.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.Data.CokAndMqt;
import com.example.login.R;
import com.example.login.UI.Homepage;
import com.example.login.UI.MainActivity;
import com.example.login.Data.User;
import com.example.login.activity.Internet;
import com.example.login.utils.MapApplication;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Response;


public class MyFragment extends Fragment implements View.OnClickListener {
    Button btn;
    private Activity myActivity;
    private TextView textView_Count;
    private TextView textView_Sex;
    private CokAndMqt cokAndMqt;
    private JSONObject jsonObject;
    public  Mhandler mHandler;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myActivity= (Activity) context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my, container, false);
        textView_Count=v.findViewById(R.id.tv_account);
        textView_Sex=v.findViewById(R.id.tv_sex);
        textView_Sex.setText(User.myUser.getSex());
        btn=v.findViewById(R.id.quit_btn);
        textView_Count.setText(User.myUser.getName());
        Getcookie(cokAndMqt);
        mHandler=new Mhandler();
        btn.setOnClickListener(this);
        return v;
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.quit_btn:
                sendRequestWithHttpURLConnectionQuit(cokAndMqt);
        }
    }
    private void sendRequestWithHttpURLConnectionQuit(CokAndMqt cokAndMqt) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //调用自动登录接口
                    Response response = Internet.getResponse(getString(R.string.url)+getResources().getString(R.string.logout),cokAndMqt);
                    Log.d("自动登录响应数据", response.toString());
                    //访问url获取响应数据
                    String responseData = response.body().string();
                    //获得响应消息
                    Log.d("数据", responseData);
                    Message message=Message.obtain();
                    Bundle bundle=new Bundle();
                    bundle.putString("responseData",responseData);
                    message.what=0;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    //利用handler发送消息
                    //响应头

//                        String s = login.get(0);
//                        String session = s.substring(0, s.indexOf(";"));
//                        PreferenceManager.saveCookiePreference(MainActivity.this,session);
                    //存储数据
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void Getcookie(CokAndMqt cokAndMqt) {
        LitePal.getDatabase();
        cokAndMqt = new CokAndMqt();
        List<CokAndMqt> cokAndMqts = LitePal.findAll(CokAndMqt.class);
        if (cokAndMqts.isEmpty()) {
            cokAndMqts = null;
            return;
        }
        else{
            this.cokAndMqt=LitePal.find(CokAndMqt.class,1);
        }
    }
    private class Mhandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                //退出
                case 0:
                    Bundle bundle = msg.getData();
                    String responseData=bundle.getString("responseData");
                    ;
                    //将字符串数据转换成jason数据

                            Intent intent = new Intent(myActivity, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(myActivity, "退出成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}