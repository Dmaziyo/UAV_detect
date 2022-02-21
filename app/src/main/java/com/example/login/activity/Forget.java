package com.example.login.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.login.Data.User;
import com.example.login.R;
import com.example.login.UI.MainActivity;

import org.litepal.LitePal;

import java.util.List;

public class Forget extends AppCompatActivity implements View.OnClickListener {
    private Button forback;
    private Button forget_search;
    private EditText acount;
    private EditText phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        ActionBar act = getSupportActionBar();//隐藏标题
        act.hide();//隐藏标题
         forback = (Button)findViewById(R.id.forget_back);
         forget_search = (Button)findViewById(R.id.forget_search);
        forback.setOnClickListener(this);
        forget_search.setOnClickListener(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        acount=(EditText)findViewById(R.id.forget_acount);
        phone=(EditText)findViewById(R.id.forget_phone);
        String acount1 = acount.getText().toString();
        String phone1 = phone.getText().toString();
        switch (v.getId()){
            case R.id.forget_back:
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            case R.id.forget_search:Judge(acount1,phone1);
        }
    }
    public void Judge(String acount,String phone){
        List<User> list= LitePal.findAll(User.class);
        User user=null;
        boolean flag=false;
        for (User e:list){
            if (e.getName().equals(acount)&&e.getPhone_num().equals(phone)){
                flag=true;
                user=e;
                break;
            }
        }
        if (flag){
            Toast.makeText(Forget.this,"你的密码是"+user.getPassword(),Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(Forget.this,"你账号或手机错误",Toast.LENGTH_SHORT).show();
        }
    }
}