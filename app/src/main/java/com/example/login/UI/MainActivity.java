package com.example.login.UI;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.Data.CokAndMqt;
import com.example.login.MQTT.MQTTService;
import com.example.login.R;
import com.example.login.activity.Forget;
import com.example.login.activity.Internet;
import com.example.login.Data.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static int value=2;

    private static final int LOCATION_CODE = 1;
    private CheckBox remeberPass;
    public static List<User> list=new ArrayList<>();
    private EditText acount;
    private EditText password;
    private User mem=null;
    private User pre;
    public Mhandler mHandler;
    private JSONObject jsonObject;
    private CokAndMqt cokAndMqt;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        quanxian(this);
        getWindow().setEnterTransition(new Fade().setDuration(1000));
        getWindow().setExitTransition(new Fade().setDuration(1000));
        //启动动画样式
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
        super.onCreate(savedInstanceState);
        Getcookie(cokAndMqt);
        mHandler=new Mhandler();
        setContentView(R.layout.activity_main);
        ActionBar act = getSupportActionBar();//隐藏标题
        act.hide();//隐藏标题
        list=LitePal.findAll(User.class);
        for (User e:list){
            if (e.isRemeber_password()) {
                mem = e;
                pre=mem;
            }
        }
        acount=(EditText)findViewById(R.id.login_acount);
        password=(EditText)findViewById(R.id.login_password);
//        remeberPass=(CheckBox)findViewById(R.id.Checkpassword);
        Button login=(Button)findViewById(R.id.login);
        Button regist=(Button)findViewById(R.id.register);
        TextView forget=(TextView)findViewById(R.id.forget);
        isLogined(cokAndMqt);
        //绑定界面图标
        if (mem!=null){
            password.setText(mem.getPassword());
            acount.setText(mem.getName());
            remeberPass.setChecked(true);
        }//设置的记住账号密码
        login.setOnClickListener(this);
        regist.setOnClickListener(this);
        forget.setOnClickListener(this);
        forget.setOnClickListener(this);
    }

    private void isLogined(CokAndMqt cokAndMqt) {
        if (cokAndMqt==null)return;
        sendRequestWithHttpURLConnectionAuto(cokAndMqt);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {

        //数据库查询
         String  acount1=acount.getText().toString();
        String  password1=password.getText().toString();
        switch (v.getId()){
            case R.id.login:
                    if (acount1.length() == 0) {
                        Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (password1.length() == 0) {
                        Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        sendRequestWithHttpURLConnectionLogin(acount1,password1);
                            //进行账户验证

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                        //记住密码需要重写
//                    if (remeberPass.isChecked()) {
//                        if (pre != null) {
//                            pre.setToDefault("remeber_password");
//                            //把要修改的字段改成默认值
//                            pre.update(pre.getId());
//                        }
//                        mem.setRemeber_password(true);
//                        mem.update(mem.getId());
//                        //修改确认的字段
//                    } else if (!remeberPass.isChecked()) {
//                        mem.setToDefault("remeber_password");
//                        mem.update(mem.getId());
//                        if (pre != null) {
//                            pre.setToDefault("remeber_password");
//                            pre.update(pre.getId());
//                        }
//                    }
//                else{
//                    Intent intent = new Intent(MainActivity.this, INTO.class);
//                    startActivity(intent);
//                }
                break;
            case R.id.forget:
                Intent intent=new Intent(MainActivity.this, Forget.class);
                startActivity(intent);
                break;
            case R.id.register:
                Intent mainIntent = new Intent(MainActivity.this, Register.class);
                startActivity(mainIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
//    public User Login(User user){//用于验证账户
//        for (User e:list){
//            Log.d("账号密码", e.getName()+"  "+e.getPassword());
//            Log.d("list账号密码", user.getName()+"  "+user.getPassword());
//            Log.d("账号密码", (user!=null)+"");
//            boolean flag1=user.getName().equals(e.getName());
//            boolean flag2=user.getPassword().equals(e.getPassword());
//            if (flag1==true&&flag2==true)return e;
//        }
//        return null;
//    }

//自动登录的接口
private void sendRequestWithHttpURLConnectionAuto(CokAndMqt cokAndMqt) {
    new Thread(new Runnable() {
        @Override
        public void run() {
           try {
               //调用自动登录接口
                Response response = Internet.getResponse(getString(R.string.url)+getResources().getString(R.string.autologin),cokAndMqt);
                Log.d("自动登录响应数据", response.toString());
                //访问url获取响应数据
                String responseData = response.body().string();
                //获得响应消息
                Log.d("数据", responseData);
                Message message=Message.obtain();
                Bundle bundle=new Bundle();
                bundle.putString("responseData",responseData);
                message.what=1;
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
    private void sendRequestWithHttpURLConnectionLogin(String username,String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
               try {

                    String cookie = null;
                    HashMap<String,String> map=new HashMap<>();
                    map.put("username",username);
                    map.put("password",password);
                    Gson gson = new Gson();
                    String data=gson.toJson(map);
                    Response response = Internet.postResponse(getString(R.string.url)+getResources().getString(R.string.login),data);
                    String responseData = response.body().string();
                    JSONObject jsonobj= new JSONObject(responseData);
                    if (response.isSuccessful()) {//response 请求成功
                        Headers headers = response.headers();

                        List<String> cookies = headers.values("Set-Cookie");
                        String u_id= cookies.get(1).split("=",2)[1].split(";",2)[0];
                        String m_id= cookies.get(0).split("=",2)[1].split(";",2)[0];
                        Log.e("m_id",m_id);
                        Log.e("u_id",u_id);
                        if((int)jsonobj.get("code")==0) {
                            cokAndMqt = new CokAndMqt();
                            cokAndMqt.setM_id(m_id);
                            cokAndMqt.setU_id(u_id);
                            String temp=new JSONObject(jsonobj.get("data").toString()).get("mqtt_sub").toString();
                            cokAndMqt.setMqt_sub(temp);
                            MQTTService.makesubscribe(temp);
                            SaveCookie(cokAndMqt);
                        }
//                                   //登录成功就存储起来
//                                   if((int)jsonobj.get("code")==0){
//                                       cokAndMqt=new CokAndMqt();
//                                       cokAndMqt.setCookie(cookie);
//                                       SaveCookie(cokAndMqt);
//                       if (cookies.size() > 0) {
//                           String session = cookies.get(0);
//                           if (!TextUtils.isEmpty(session)) {
//                               int size = session.length();
//                               int i = session.indexOf(";");
//                               if (i < size && i >= 0) {
//                                   //最终获取到的cookie
//                                   cookie = session.substring(0, i);
//                                   //登录成功就存储起来
//                                   if((int)jsonobj.get("code")==0){
//                                       cokAndMqt=new CokAndMqt();
//                                       cokAndMqt.setCookie(cookie);
//                                       SaveCookie(cokAndMqt);
//                                   }
//                               }
//                           }
//                       }
                   }

                    //访问url获取响应数据

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


    //handler回传
    private class Mhandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                //登录成功的时候
                case 1:
                case 0:
                    Bundle bundle = msg.getData();
                    String responseData=bundle.getString("responseData");
                    try {
                        jsonObject= new JSONObject(responseData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ;
                    //将字符串数据转换成jason数据
                    try {
                        Log.d("code",jsonObject.get("code").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        if((int)jsonObject.get("code")==0){
                            User.myUser=new User();
                            CokAndMqt cokAndMqt=new CokAndMqt();
                            User.myUser.setId_card(new JSONObject(jsonObject.get("data").toString()).get("id_card").toString());
                            User.myUser.setJob(new JSONObject(jsonObject.get("data").toString()).get("job").toString());
                            User.myUser.setName(new JSONObject(jsonObject.get("data").toString()).get("name").toString());
                            User.myUser.setPhone_num(new JSONObject(jsonObject.get("data").toString()).get("phone").toString());
                            User.myUser.setSex(new JSONObject(jsonObject.get("data").toString()).get("sex").toString());
                            User.myUser.setId((int)new JSONObject(jsonObject.get("data").toString()).get("user_id"));

                            Intent intent = new Intent(MainActivity.this, Homepage.class);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this,jsonObject.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * 位置权限
     */
    public void quanxian(final Activity context) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
                }
            }
        } catch (SecurityException e) {
        }   List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions =permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
    }
    public void Getcookie(CokAndMqt cokAndMqt){
        LitePal.getDatabase();
        cokAndMqt=new CokAndMqt();
        List<CokAndMqt> cokAndMqts=LitePal.findAll(CokAndMqt.class);
        if(cokAndMqts.isEmpty()){
            cokAndMqts=null;
            return;
        }
        else{
            cokAndMqt=LitePal.find(CokAndMqt.class,1);
            this.cokAndMqt=cokAndMqt;
            MQTTService.myTopic=cokAndMqt.getMqt_sub();
        }
    }
    private void SaveCookie(CokAndMqt cokAndMqt) {
        List<CokAndMqt> cokAndMqts=LitePal.findAll(CokAndMqt.class);
        if(cokAndMqts.isEmpty()){
            cokAndMqt.save();
        }
        else{
            cokAndMqt.update(1);
        }
    }
}
