package com.example.login.UI;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.login.R;
import com.example.login.activity.Internet;
import com.example.login.Data.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private EditText acount;
    private EditText password;
    private EditText passcfm;
    private EditText phone;
    private EditText sex;
    private EditText name;
    private Handler mHandler;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setEnterTransition(new Fade().setDuration(1000));
        getWindow().setExitTransition(new Fade().setDuration(1000));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        acount=(EditText) findViewById(R.id.register_acount);
        password=(EditText) findViewById(R.id.register_password);
        passcfm=(EditText)findViewById(R.id.register_passwordcfm);
        phone=(EditText)findViewById(R.id.register_phone);
        sex=(EditText)findViewById(R.id.register_sex);
        name=findViewById(R.id.register_name);
        ActionBar act = getSupportActionBar();//隐藏标题
        act.hide();//隐藏标题
        Button regisback = (Button)findViewById(R.id.regis_back);
        Button save = (Button)findViewById(R.id.save);


        regisback.setOnClickListener(this);
        save.setOnClickListener(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.regis_back:back();
            break;
            case R.id.save:Save();
                break;
        }
    }

    private void back() {
        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                Intent mainIntent = new Intent(Register.this, MainActivity.class);
                startActivity(mainIntent, ActivityOptions.makeSceneTransitionAnimation(Register.this).toBundle());

            }
        }, 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void Save(){
        LitePal.getDatabase();
        String acount1=acount.getText().toString();
        String password1=password.getText().toString();
        String passcfm1=passcfm.getText().toString();
        String phone1=phone.getText().toString();
        String sex1=sex.getText().toString();
        String name1=name.getText().toString();
        if(acount1.length()==0){
            Toast.makeText(Register.this,"账号不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(sex1.length()==0){
            Toast.makeText(Register.this,"请输入性别",Toast.LENGTH_SHORT).show();
            return;
        }
        if(name1.length()==0){
            Toast.makeText(Register.this,"请输入名字",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password1.length()==0){
            Toast.makeText(Register.this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!passcfm1.equals(password1)){
            Toast.makeText(Register.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
            return;
        }
        if(phone1.length()==0){
            Toast.makeText(Register.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        User user =new User(name1,sex1,acount1,password1,phone1);
        sendRequestWithHttpURLConnection(user);
//        List<User> allUser= LitePal.findAll(User.class);
//        for (User e:allUser){
//            if (e.getName().equals(acount1)||e.getPhone().equals(Checkcode)){
//                Toast.makeText(Register.this,"账号或手机号已注册，请重写注册", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//        user.save();
//        Toast.makeText(Register.this,"注册成功",Toast.LENGTH_SHORT).show();
//        Intent mainIntent = new Intent(Register.this,MainActivity.class);
//        startActivity(mainIntent, ActivityOptions.makeSceneTransitionAnimation(Register.this).toBundle());

    }
    private void sendRequestWithHttpURLConnection(User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient.Builder().build();
                Request request = new Request.Builder().url(getResources().getString(R.string.url)+getResources().getString(R.string.register)).build();
                //request目前没用
                try {
                    HashMap<String,String> map=new HashMap<>();
                    map.put("username",user.getName());
                    map.put("password",user.getPassword());
                    map.put("sex",user.getSex());
                    map.put("account",user.getAccount());
                    map.put("phone_num",user.getPhone_num());
                    map.put("job","");
                    map.put("id_card","");
                    Gson gson = new Gson();
                    String data=gson.toJson(map);
                    Response response = Internet.postResponse(getResources().getString(R.string.url)+getResources().getString(R.string.register),data);
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
    private class Mhandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
                    JSONObject  jsonObject = null;
                    Bundle bundle = msg.getData();
                    String responseData=bundle.getString("responseData");
                    Log.d("数据", responseData);
                    try {
                    jsonObject= new JSONObject(responseData);
                    Log.d("code",jsonObject.get("code").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ;
                    //将字符串数据转换成jason数据
                    try {
                        if((int)jsonObject.get("code")==0){
                            User.myUser=new User();
                            User.myUser.setId_card(new JSONObject(jsonObject.get("data").toString()).get("id_card").toString());
                            User.myUser.setJob(new JSONObject(jsonObject.get("data").toString()).get("job").toString());
                            User.myUser.setName(new JSONObject(jsonObject.get("data").toString()).get("name").toString());
                            User.myUser.setPhone_num(new JSONObject(jsonObject.get("data").toString()).get("phone").toString());
                            User.myUser.setSex(new JSONObject(jsonObject.get("data").toString()).get("sex").toString());
                            User.myUser.setId((int)new JSONObject(jsonObject.get("data").toString()).get("user_id"));

                            Intent intent = new Intent(Register.this,MainActivity.class );
                            startActivity(intent);
                            Toast.makeText(Register.this, "登录成功", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Register.this,jsonObject.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}