package com.example.login.UI;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.login.Data.UAV_Data;
import com.example.login.MQTT.Link_Mqtt;
import com.example.login.MQTT.MQTTService;
import com.example.login.MQTT.MyServiceConnection;
import com.example.login.R;

import com.example.login.fragment.CollectFragment;
//import com.example.login.fragment.HomrFragment;
import com.example.login.fragment.HomrFragment;
import com.example.login.fragment.MyFragment;
import com.example.login.utils.MapApplication;
import com.example.login.utils.RecycleAdapterDemo;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity implements Link_Mqtt {
    private MyServiceConnection serviceConnection;
    private MQTTService mqttService;
    private Activity myActivity;
    private JSONObject jsonObject;
    private Fragment[] fragments = new Fragment[]{null,null,null};//存放Fragment
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myActivity=this;
        setContentView(R.layout.activity_homepage);
        CreateMqtt();
        applyQuest();
        MapApplication.Instance.setmMainActivity(myActivity);
        ActionBar act = getSupportActionBar();//隐藏标题
        act.hide();//隐藏标题
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment fragment=null;
                switch (item.getItemId()){
                    case R.id.myhome:
                        switchFragment(0);
                        break;
                    case R.id.Mymessage:
                        switchFragment(1);
                        break;
                    case R.id.MySetting:
                        switchFragment(2);
                        break;
                }
                return true;
            }
        });
        switchFragment(0);
    }

    private void applyQuest() {
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(myActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(myActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(myActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions =permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(myActivity, permissions, 1);
        }
    }

    private void switchFragment(int fragmentIndex) {
        //在Activity中显示Fragment
        //1、获取Fragment管理器 FragmentManager
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        //2、开启fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //懒加载 - 如果需要显示的Fragment为null，就new。并添加到Fragment事务中
        if (fragments[fragmentIndex] == null) {
            switch (fragmentIndex) {
                case 0:
                    fragments[fragmentIndex] = new HomrFragment();
                    break;
                case 1:
                    fragments[fragmentIndex] = new CollectFragment();
                    break;
                case 2:
                    fragments[fragmentIndex] = new MyFragment();
                    break;
            }
            //==添加Fragment对象到Fragment事务中
            //参数：显示Fragment的容器的ID，Fragment对象
            transaction.add(R.id.fragmentContainer, fragments[fragmentIndex]);
        }

        //隐藏其他的Fragment
        for (int i = 0; i < fragments.length; i++) {
            if (fragmentIndex != i && fragments[i] != null) {
                //隐藏指定的Fragment
                transaction.hide(fragments[i]);
            }
        }
        //4、显示Fragment
        transaction.show(fragments[fragmentIndex]);

        //5、提交事务
        transaction.commit();
    }

    @Override
    public void CreateMqtt() {
        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(this);

        Intent intent = new Intent(myActivity, MQTTService.class);

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


//    接收并处理MQTT的消息
    @Override
    public void setMessage(String message) {
        UAV_Data temp=new UAV_Data();
        try {
             jsonObject=new JSONObject(message);
            Log.d("JSON消息体", jsonObject.toString());
            temp.setId(jsonObject.get("id").toString());
            temp.setUAV_id(jsonObject.get("UAV_id").toString());
            temp.setEvent_type(jsonObject.get("type").toString());
            temp.setDate(jsonObject.get("upload_time").toString());
            temp.setText(jsonObject.get("text").toString());
            temp.setLocation(jsonObject.get("lon")+(String)jsonObject.get("asl"));
            temp.setImg_url(getString(R.string.picImg)+jsonObject.get("url").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mqttService = serviceConnection.getMqttService();


        CollectFragment.list.add(temp);
        CollectFragment.adapterDome.notifyItemInserted(( CollectFragment.list.size()-1));
        CollectFragment.recyclerView.scrollToPosition( CollectFragment.list.size()-1);
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        Log.d("数据", message);
        mqttService.toCreateNotification(message);
    }
    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}