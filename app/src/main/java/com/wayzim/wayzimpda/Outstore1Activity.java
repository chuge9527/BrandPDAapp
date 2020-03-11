package com.wayzim.wayzimpda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Outstore1Activity extends AppCompatActivity {
    private Detail1Fragment detailfm;
    private EditText taryCodeTX;//托盘编码
    private Detail1Fragment detail1Fra;
    //数据
    private String data1, data2;//查询货物的结果,提交的结果
    private Handler handler, handler2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outstore1);
        taryCodeTX = findViewById(R.id.mCode);

        detail1Fra = new Detail1Fragment();
       getSupportFragmentManager().beginTransaction().add(R.id.frame1, detail1Fra).commit();

        //
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //  Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                //  Toast.makeText(Instore1Activity.this,info[1],Toast.LENGTH_SHORT).show();
                if (msg.what == 0x0002) {
                    Toast.makeText(Outstore1Activity.this, "json数据失败", Toast.LENGTH_SHORT).show();
                } else {
                    showinfo1();
                }

            }
        };

    }

    //出库盘点,获取数据
    public void getOutMaterial(View view) {

        String mCodes = taryCodeTX.getText().toString();
        String url = "http://192.168.1.103:8080/api/getBin2MaterialByTrayCode?trayCode=" + mCodes;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .get()//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("wy", "onFailure: ");
                    Toast.makeText(Outstore1Activity.this, "Http失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String json = response.body().string();//!!!只能调用一次!!!
                    Log.d("wy", "onResponse: " + json);
                    try {
                        //Json数据的处理
                        JSONObject jsonObject = new JSONObject(json);
                        data1 = jsonObject.getString("data");
                        //  Log.d("data:", data1);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0x0001);
                            }
                        }).start();
                    } catch (JSONException e) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0x0002);
                            }
                        }).start();


                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Outstore1Activity.this, "Http链接错误", Toast.LENGTH_LONG).show();
        }
    }
    //传送数据
    public void  showinfo1(){
        Bundle bundle = new Bundle();
        bundle.putString("data",data1);
        Detail1Fragment detail1Fra2 = new Detail1Fragment();
       detail1Fra2.setArguments(bundle);//数据传递到fragment中

        getSupportFragmentManager().beginTransaction().replace(R.id.frame1,detail1Fra2).commit();


    }
}
