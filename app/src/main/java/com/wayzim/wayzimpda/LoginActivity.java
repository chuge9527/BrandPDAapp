package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.wayzim.wayzimpda.tools.SharedHelper;

public class LoginActivity extends AppCompatActivity {
    private Button mBtnlogin;

    private SharedHelper sh;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //存入WMS的IP
         mContext = getApplicationContext();
        sh = new SharedHelper(mContext);
        sh.clearURl();
      //  String url = "120.27.143.134";
        String url = "192.168.1.111";
        sh.saveURL(url);

        mBtnlogin =(Button) findViewById(R.id.btn_login);
        mBtnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                //登录成功
                //  Intent intent = new Intent(LoginActivity.this,Http2Activity.class);
                //   Intent intent = new Intent(LoginActivity.this,Instore1Activity.class);
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
    //
}
